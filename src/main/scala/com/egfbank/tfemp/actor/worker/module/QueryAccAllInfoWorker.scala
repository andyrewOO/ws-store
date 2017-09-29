package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.util.ToolUtils
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.constant.ESBRetCode
import cfca.safeguard.tx.business.bank.TxFeedbackClientWholeAccountInquiry_Accounts
import java.util.ArrayList
import cfca.safeguard.tx.Account
import com.egfbank.tfemp.util.XMLUtil
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100310
import cfca.safeguard.tx.business.bank.TxFeedbackClientWholeAccountInquiry_Measure
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.util.RetCode2Result
/**
 * @author huxp
 */
class QueryAccAllInfoWorker extends BizWorker with ESBService with CFCAService with CFCADbService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Strat QueryAccAllInfoWorker")
    val tEvent: TEvent[Map[String, Any]] = null
    var queryAllInfoResponse: Elem = null

    event.content match {
      case sourceData: Elem => {
        log.info(s"Query applicationid:${(sourceData \\ "ApplicationID").text.toString}")
        // 法律文书入库
        insertIntoForceMeasureFile(sourceData)

        val messageFrom = (sourceData \\ "MessageFrom").text.toString
        val inquiryMode = (sourceData \\ "InquiryMode").text.toString
        val applicationID = (sourceData \\ "ApplicationID").text.toString
        val accountCredentialType = (sourceData \\ "AccountCredentialType").text.toString
        val accountCredentialNumber = (sourceData \\ "AccountCredentialNumber").text.toString
        val accountSubjectName = (sourceData \\ "AccountSubjectName").text.toString

        // 封装查询客户信息接口Map
        val queryCusInfoMapDataMap = Map(
          "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "QryMode" -> "3",
          "IdType" -> accountCredentialType,
          "IdCode" -> accountCredentialNumber,
          "ClientName" -> accountSubjectName)

        // 查询客户信息
        val cusInfo = queryCusInfo(queryCusInfoMapDataMap)

        val tx100310 = new Tx100310
        tx100310.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))
        tx100310.setApplicationID(applicationID)
        tx100310.setOperatorName("")
        tx100310.setOperatorPhoneNumber("")
        tx100310.setFeedbackOrgName("")

        (cusInfo \\ "RetCode").text.toString match {
          // 获取客户号成功
          case ESBRetCode.SUCCESSFUL => {
            log.info("get CustomeInfo SuccessFul")
            val queryAccListInfoDataMap = Map(
                "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
                "ClientNo" -> (cusInfo \\ "ClientNo").text.toString)
                
            // 查询账户列表
            val accListInfo = queryAccListInfo(queryAccListInfoDataMap)
            (accListInfo \\ "RetCode").text.toString match {
              // 获取账户列表成功
              case ESBRetCode.SUCCESSFUL => {
                log.info("get AccountList SuccessFul")
                val accountsList = new ArrayList[TxFeedbackClientWholeAccountInquiry_Accounts]
                getAllAccNo(accListInfo).map { accNo =>
                  // 查询每个账号的子账户列表
                  val subAccountList = querySubAccountList(Map(
                      "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
                      "AcctNo" -> accNo))
                      
                  log.debug("get subAccountList , Size:" + subAccountList.size)
                  accountsList.add(getAccounts(subAccountList))
                }
                tx100310.setAccountsList(accountsList)
                tx100310.setResult("0000")
                // 是否查询强制措施
                if (inquiryMode.equals("02")) {
                  log.debug("get Measures Info")
                  val measuresList = new ArrayList[TxFeedbackClientWholeAccountInquiry_Measure]
                  for (accNo <- getAllAccNo(accListInfo)) {
                    // 查询强制措施
                    val forceMeasure = queryForceMeasure(Map(
                        "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
                        "AcctNo" -> accNo))
                        
                    (forceMeasure \\ "RetCode").text.toString match {
                      case ESBRetCode.SUCCESSFUL => {
                        log.info("get Measures Info Successful")
                        measuresList.addAll(getMeasuresList(forceMeasure))
                      }
                      case x => {
                        log.info("get Measures Info fail")
                      }
                    }
                  }
                  tx100310.setMeasures(measuresList)
                }
              }
              case _ => {
                log.info("get AccountList Fail")
                // 根据核心的反馈生成业务应答码
                val result = RetCode2Result.cast((cusInfo \\ "RetCode").text.toString)
                tx100310.setResult(result)
                tx100310.setFeedbackRemark((cusInfo \\ "RetMsg").text.toString)
              }
            }
          }
          // 获取客户号失败
          case x => {
            log.info("get CustomeInfo Fail")
            // 根据核心的反馈生成业务应答码
            val result = RetCode2Result.cast((cusInfo \\ "RetCode").text.toString)
            tx100310.setResult(result)
            tx100310.setFeedbackRemark((cusInfo \\ "RetMsg").text.toString)
          }
        }

        // 将反馈信息入库
        if (insertIntoUpQueue(Map(
          "applicationID" -> tx100310.getApplicationID,
          "transSerialNumber" -> tx100310.getTransSerialNumber,
          "txCode" -> "100310",
          "to" -> messageFrom)) == 1) {
          queryAllInfo(tx100310, messageFrom)
        }
      }
    }
    None
  }

  /**
   * 获取客户号下所有账号
   */
  private[this] def getAllAccNo(accListInfo: Elem): List[String] = {
    val accountNodeList = accListInfo \\ "AcctNo"
    val accountNoList = accountNodeList.map { _.text }
    log.debug("all accountNo:" + accountNoList)
    accountNoList.toList
  }

  /**
   * 构造 Accounts 节点
   */
  private[this] def getAccounts(subAccountList: Elem) = {
    val accounts = new TxFeedbackClientWholeAccountInquiry_Accounts
    accounts.setAccountName((subAccountList \\ "AcctName").text.toString)
    accounts.setCardNumber("")
    accounts.setDepositBankBranch((subAccountList \\ "OpnAcctBrchNm").text.toString)
    accounts.setDepositBankBranchCode((subAccountList \\ "OpnAcctBrchId").text.toString)
    accounts.setAccountOpenTime((subAccountList \\ "OpnAcctDt").text.toString)
    accounts.setAccountCancellationTime("")
    accounts.setAccountCancellationBranch("")
    accounts.setRemark("")

    val accountList = new ArrayList[Account]
    val accountListInfo = XMLUtil.getFVListMap("array", subAccountList.toString, "BODY")
    var serial = 0
    for (accountInfo <- accountListInfo) {
      accountList.add(getAccount(accountInfo, serial = serial + 1))
    }
    accounts.setAccountList(accountList)
    accounts
  }

  /**
   * 构造 account 节点
   */
  private[this] def getAccount(accountInfo: Map[String, String], serial: Int) = {
    val account = new Account
    account.setAccountNumber(accountInfo("AcctNo") + "_" + accountInfo("SubAcctNo"))
    account.setAccountSerial(serial.toString())
    account.setAccountType(accountInfo("AcctSpecTp"))
    account.setAccountStatus(accountInfo("AcctSt"))
    account.setCurrency(accountInfo("CcyType"))
    account.setCashRemit(accountInfo("CshRmtFlg"))
    account.setAccountBalance(accountInfo("AcctBal"))
    account.setAvailableBalance(accountInfo("AvlbBal"))
    account.setLastTransactionTime(accountInfo("LstActyDt"))
    account
  }

  /**
   * 获取一个主账号下所有的强制措施
   */
  private[this] def getMeasuresList(forceMeasure: Elem) = {
    val measuresInfoList = XMLUtil.getFVListMap("array", forceMeasure.toString, "BODY")
    val measuresList = new ArrayList[TxFeedbackClientWholeAccountInquiry_Measure]
    for (measuresInfo <- measuresInfoList) {
      val measure = new TxFeedbackClientWholeAccountInquiry_Measure
      val accountNumber = measuresInfo("AcctNo") + "_" + measuresInfo("SubAcctNo")
      measure.setAccountNumber(accountNumber)
      measure.setFreezeSerial(measuresInfo("DtlNo"))
      measure.setCardNumber("")
      measure.setCurrency(measuresInfo("CcyType"))
      measure.setFreezeBalance(measuresInfo("FrezeeFund"))
      measure.setFreezeDeptName(measuresInfo("ExcsOrgNm"))
      measure.setFreezeEndTime(measuresInfo("FrzEndDt"))
      measure.setFreezeStartTime(measuresInfo("FrzDate"))
      measure.setFreezeType(measuresInfo("FrzMsrTp"))
      measure.setRemark(measuresInfo("Remark"))
      measuresList.add(measure)
    }
    measuresList
  }
}