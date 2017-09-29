package com.egfbank.tfemp.actor.worker.module

import java.util.ArrayList
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.actor.services.TDHDbService
import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.constant.CFCAResult
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.constant.ESBRetCode
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.util.XMLUtil
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100302
import cfca.safeguard.tx.Account
import cfca.safeguard.tx.Transaction
import cfca.safeguard.tx.business.bank.TxFeedbackAccountTransactionDetailEnquiry_Accounts
import cfca.safeguard.tx.business.bank.TxFeedbackAccountTransactionDetailInquiry_Account
import com.egfbank.tfemp.util.ToolUtils
import com.egfbank.tfemp.actor.services.FileService
import com.egfbank.tfemp.util.RetCode2Result
/**
 * @author andy
 */
class QueryTransDetailWorker extends BizWorker with ESBService with CFCAService with FileService with CFCADbService with TDHDbService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("QueryTransDetail, start work")
    
    val questTransDetail = event.content
    questTransDetail match {
      case questTransDetailElem: Elem => {
        log.info(s"Query applicationid:${(questTransDetailElem \\ "ApplicationID").text.toString}")
        // 法律文书入库
        insertIntoForceMeasureFile(questTransDetailElem)

        val questBeanMap = XMLUtil.getDataMapFromMessage(questTransDetail.toString)

        val messageFrom = questBeanMap("messageFrom").toString
        val applicationID = questBeanMap("applicationID").toString
        val cardNumber = questBeanMap("cardNumber").toString

        val tx100302: Tx100302 = new Tx100302
        tx100302.setApplicationID(applicationID)
        tx100302.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))

        // 查询子账户列表
        val subAccountListInfo = querySubAccountList(Map(
          "ApplicationID" -> applicationID,
          "AcctNo" -> cardNumber))
        (subAccountListInfo \\ "RetCode").text.toString match {
          // 获取子账户列表成功
          case ESBRetCode.SUCCESSFUL => {
            tx100302.setAccounts(createAccounts(subAccountListInfo, questBeanMap))
            tx100302.setResult(CFCAResult.SUCCESSFUL)
          }
          case _ => {
            log.error(s"get subAccountListInfo fail,applicationid:${applicationID}")
            val RetCode = (subAccountListInfo \\ "RetCode").text.toString
            val RetMsg = (subAccountListInfo \\ "RetMsg").text.toString

            // 根据核心的反馈生成业务应答码
            val result = RetCode2Result.cast(RetCode)
            tx100302.setResult(result)
            tx100302.setFeedbackRemark(RetMsg)
          }
        }
        
        // 将反馈信息入库
        if (insertIntoUpQueue(Map(
          "applicationID" -> tx100302.getApplicationID,
          "transSerialNumber" -> tx100302.getTransSerialNumber,
          "txCode" -> "100302",
          "to" -> messageFrom)) == 1) {
          // 反馈给前置机
          // Result需要根据情况指定
          queryTransDetail(tx100302, messageFrom)
        }
      }
      case _ =>
    }
    None
  }

  /**
   * 构造 Accounts 节点
   */
  private[this] def createAccounts(subAccountList: Elem, questBeanMap: Map[String, Any]) = {
    log.info("Start createAccounts")
    
    val inquiryMode = questBeanMap("inquiryMode").toString
    val accounts = new TxFeedbackAccountTransactionDetailEnquiry_Accounts
    accounts.setAccountName((subAccountList \\ "AcctName").text.toString)
    accounts.setCardNumber("")
    accounts.setDepositBankBranch((subAccountList \\ "OpnAcctBrchNm").text.toString)
    accounts.setDepositBankBranchCode((subAccountList \\ "OpnAcctBrchId").text.toString)
    accounts.setAccountOpenTime((subAccountList \\ "OpnAcctDt").text.toString)
    accounts.setAccountCancellationTime("")
    accounts.setAccountCancellationBranch("")
    accounts.setRemark("")

    log.info("create Accounts node,inquiryMode:" + inquiryMode)
    inquiryMode match {
      // 无交易明细
      case "01"        => getAccountsWithoutDetail(accounts, subAccountList)
      // 有交易明细
      case "02" | "03" => getAccountsWithDetail(accounts, subAccountList, questBeanMap)
      // 明细快速查询
      case "04"        => getAccountsWithDetail(accounts, subAccountList, questBeanMap, "04")
    }
  }

  /**
   * 构造 Accounts 中的 Account,无交易明细
   */
  private[this] def getAccountsWithoutDetail(
    accounts: TxFeedbackAccountTransactionDetailEnquiry_Accounts,
    subAccountList: Elem) = {
    log.info("Start getAccountsWithoutDetail")
    
    val accountList = new ArrayList[TxFeedbackAccountTransactionDetailInquiry_Account]
    val accountListInfo = XMLUtil.getFVListMap("array", subAccountList.toString, "BODY")
    val serial = 0
    for (accountInfo <- accountListInfo) {
      accountList.add(getAccount(accountInfo, serial = serial + 1))
    }
    accounts.setAccountList(accountList)
    accounts
  }

  /**
   * 构造Accounts 中的Account,有交易明细
   */
  private[this] def getAccountsWithDetail(
    accounts: TxFeedbackAccountTransactionDetailEnquiry_Accounts,
    subAccountListInfo: Elem,
    questBeanMap: Map[String, Any], inqueryMode: String = "02") = {
    log.info("Start getAccountsWithDetail")

    val subAccountList = XMLUtil.getFVListMap("array", subAccountListInfo.toString, "BODY")
    val accountList = new ArrayList[TxFeedbackAccountTransactionDetailInquiry_Account]
    // 查询交易明细开始时间
    val startday = inqueryMode match {
      case "02" => questBeanMap("inquiryPeriodStart").toString.substring(0, 8)
      case _    => HfbkUtil.getYesterday()
    }
    // 查询交易明细结束时间
    val endday = inqueryMode match {
      case "02" => questBeanMap("inquiryPeriodEnd").toString.substring(0, 8)
      case _    => HfbkUtil.getXmlToday()
    }
    
    log.info(s"startday:${startday},endday:${endday}")
    val yesterday = HfbkUtil.getYesterday()

    // 构造 Account 节点, 含交易明细
    var serial = 0
    subAccountList.map { subAccount =>
      log.info("query transDetail of subAccount")
      
      serial = serial + 1 
      val acctNo = subAccount("AcctNo")
      val subAcctNo = subAccount("SubAcctNo")

      // 从核心查出的交易明细
      var tranDetailMapFromHX: List[Map[String, String]] = List()
      // 如果截至日期大于或等于昨天，则从核心查出昨天到截至日期的交易明细
      if (ToolUtils.compareDay(endday, yesterday) >= 0 && ToolUtils.compareDay(startday, yesterday) <= 0) {
        log.info("query transDetail from hexin system, start")
        // 查询交易明细--核心系统
        val tranDetail = queryTranDetail(Map(
          "ApplicationID" -> questBeanMap("applicationID").toString,
          "AcctNo" -> acctNo,
          "SubAcctNo" -> subAcctNo,
          "StartDate" -> yesterday,
          "EndDate" -> endday))
        tranDetailMapFromHX = ToolUtils.lowwerKey(XMLUtil.getFVListMap("array", tranDetail.toString, "BODY"))
        log.info(s"query transDetail from hexin system, end, size:${tranDetailMapFromHX.size}")
      }

      // 从TDH 查出的交易明细
      var tranDetailMapFromTDH: List[Map[String, String]] = List()
      // 起始时间早于昨日，截至时间如果大于昨日，就查出到昨日，如果小于昨日，则查到截至日期
      if (ToolUtils.compareDay(startday, yesterday) < 0) {
        log.info("query transDetail from TDH system, start")
        // 查询交易明细--TDH
        tranDetailMapFromTDH = queryTransDetail(
          acctNo,
          subAcctNo,
          startday,
          if (ToolUtils.compareDay(endday, yesterday) > 0) yesterday else endday)
        log.info(s"query transDetail from TDH system, end, size:${tranDetailMapFromTDH.size}")
      }

      val tranDetailMap: List[Map[String, String]] = tranDetailMapFromHX ++ tranDetailMapFromTDH
      val account = getAccount(subAccount, tranDetailMap, serial)
      accountList.add(account)
    }
    accounts.setAccountList(accountList)
    accounts
  }

  /**
   * 构造  account 节点
   */
  private[this] def getAccount(
    accountInfo: Map[String, String],
    transDetail: List[Map[String, String]] = List(), serial: Int) = {

    val account = new TxFeedbackAccountTransactionDetailInquiry_Account
    account.setAccountNumber(accountInfo("AcctNo") + "_" + accountInfo("SubAcctNo"))
    account.setAccountSerial(serial.toString())
    account.setAccountType(accountInfo("AcctSpecTp"))
    account.setAccountStatus(accountInfo("AcctSt"))
    account.setCurrency(accountInfo("CcyType"))
    account.setCashRemit(accountInfo("CshRmtFlg"))
    account.setAccountBalance(accountInfo("AcctBal"))
    account.setAvailableBalance(accountInfo("AvlbBal"))
    account.setLastTransactionTime(accountInfo("LstActyDt"))

    // 交易明细封装
    val transList = new ArrayList[Transaction]
    transDetail.map { item =>
      val transaction = new Transaction
      transaction.setTransactionType(item("trantype"))
      transaction.setBorrowingSigns(item("dbtcrdtflg"))
      transaction.setCurrency(item("ccytype"))
      transaction.setTransactionAmount(item("tranamt"))
      transaction.setAccountBalance(item("tranbal"))
      transaction.setTransactionTime(item("trantime"))
      transaction.setTransactionSerial(item("transeqno"))
      transaction.setOpponentName((""))
      transaction.setOpponentAccountNumber(item("trgtacctno"))
      transaction.setOpponentCredentialNumber((""))
      transaction.setOpponentDepositBankID(item("trgtopnacctbrchid"))
      transaction.setTransactionRemark(item("remark"))
      transaction.setTransactionBranchCode(item("acptbrchid"))
      transaction.setTransactionBranchName(item("tranadr"))
      transaction.setLogNumber((""))
      transaction.setSummonsNumber((""))
      transaction.setVoucherCode((""))
      transaction.setVoucherType((""))
      transaction.setCashMark(item("cshrmtflg"))
      transaction.setTerminalNumber((""))
      transaction.setTransactionStatus((""))

      transList.add(transaction)
    }
    account.setTransactions(transList)
    account
  }
}