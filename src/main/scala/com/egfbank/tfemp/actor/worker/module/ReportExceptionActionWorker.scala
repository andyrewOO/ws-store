package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.ReportCase
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.util.XMLUtil
import com.egfbank.tfemp.constant.Constants
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100405
import java.util.ArrayList
import cfca.safeguard.tx.business.bank.TxExceptionalEvent_Accounts
import cfca.safeguard.tx.business.bank.TxExceptionalEvent_Account
import cfca.safeguard.tx.business.bank.TxExceptionalEvent_Transaction
import scala.xml.Node
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.ReportException
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.util.CreateXML2ESBUtil
/**
 * @author huxp
 */
class ReportExceptionActionWorker extends BizWorker with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Strat ReportExceptionActionWorker")

    event.content match {
      case requestXML: Elem => {
        log.info(s"Strat ReportExceptionActionWorker,${(requestXML \\ "CnsmSysSeqNo").text.toString}")

        val tx100405 = new Tx100405
        tx100405.setApplicationID(HfbkUtil.genApplicationID(ReportCase))
        tx100405.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))
        tx100405.setBankID((requestXML \\ "BankId").text.toString)
        tx100405.setOperatorName("")
        tx100405.setOperatorPhoneNumber("")
        tx100405.setAccountsList(getAccountsList(requestXML))

        // 插入上行指令表中
        if (insertIntoUpQueue(Map(
          "applicationID" -> tx100405.getApplicationID,
          "transSerialNumber" -> tx100405.getTransSerialNumber,
          "txCode" -> "100405",
          "to" -> tx100405.getMessageFrom)) == 1) {

          log.info(s"Call Front_end, applicationid:${tx100405.getApplicationID}")
          // 向前置机发送消息
          val response = abnormalEvent(tx100405)
          log.info(s"response from esb,${response}")

          val map2ESB = Map(
            "RetCode" -> (response \\ "Code").text.toString,
            "RetMsg" -> (response \\ "Description").text.toString,
            "SvcId" -> "120020017",
            "SvcScnId" -> "06",
            "CnsmSysSeqNo" -> (requestXML \\ "CnsmSysSeqNo").text.toString(),
            "MsgId" -> (response \\ "TransSerialNumber").text.toString)

          val xml2ESB = CreateXML2ESBUtil.create(CFCATradeCode.abnormalEvent, map2ESB)

          // ESB报文存库
          val dataMap = Map(
            "ID" -> HfbkUtil.getUUID().toString,
            "APPLICATIONID" -> tx100405.getApplicationID.toString,
            "SVCID" -> "120020017",
            "SVCSCNID" -> "06",
            "REQUEST" -> xml2ESB.toString)
          insertIntoEsbXml(dataMap)

          Some(TEvent(HfbkUtil.getUUID(), ReportException, xml2ESB, HfbkUtil.getTimeStamp(), Some(self)))
        } else {
          None
        }
      }
    }
  }

  /**
   * 拼装AccountsList
   */
  private[this] def getAccountsList(requestXML: Elem) = {
    log.info("Start getAccountsList")
    val accountsListNode = requestXML \\ "AcctInf"
    val accountsList = new ArrayList[TxExceptionalEvent_Accounts]
    accountsListNode.map { accountsNode =>
      val txExceptionalEvent_Accounts = new TxExceptionalEvent_Accounts
      txExceptionalEvent_Accounts.setAccountName((accountsNode \\ "PrimAcctNm").text.toString)
      txExceptionalEvent_Accounts.setCardNumber((accountsNode \\ "PrimAcctNo").text.toString)
      txExceptionalEvent_Accounts.setRemark((accountsNode \\ "Remark1").text.toString)
      txExceptionalEvent_Accounts.setAccountList(getAccountNode(accountsNode))
      accountsList.add(txExceptionalEvent_Accounts)
    }
    log.info(s"accountsList,${accountsList.size}")
    accountsList
  }

  /**
   * 拼装Account节点
   */
  private[this] def getAccountNode(accountsNode: Node) = {
    log.info("Start getAccountNode")
    val accountListNode = accountsNode \\ "AcctTranInf"
    val accountList = new ArrayList[TxExceptionalEvent_Account]
    accountListNode.map { accountNode =>
      val txExceptionalEvent_Account = new TxExceptionalEvent_Account
      txExceptionalEvent_Account.setAccountNumber((accountNode \\ "AcctNo").text.toString)
      txExceptionalEvent_Account.setAccountSerial((accountNode \\ "AcctSrlNo").text.toString)
      txExceptionalEvent_Account.setAccountStatus((accountNode \\ "AcctSt").text.toString)
      txExceptionalEvent_Account.setAccountType((accountNode \\ "AcctType").text.toString)
      txExceptionalEvent_Account.setCurrency((accountNode \\ "CcyType").text.toString)
      txExceptionalEvent_Account.setCashRemit((accountNode \\ "CshRmtFlg").text.toString)
      txExceptionalEvent_Account.setTransactionList(getTransactionList(accountNode))
      accountList.add(txExceptionalEvent_Account)
    }
    log.info(s"accountList,${accountList.size}")
    accountList
  }

  /**
   * 拼装Transaction节点
   */
  private[this] def getTransactionList(accountNode: Node) = {
    log.info("Start getTransactionList")
    val transactionListNode = accountNode \\ "Transaction"
    val transactionList = new ArrayList[TxExceptionalEvent_Transaction]
    transactionListNode.map { transactionNode =>
      val txExceptionalEvent_Transaction = new TxExceptionalEvent_Transaction
      txExceptionalEvent_Transaction.setFeatureCode("3003")
      txExceptionalEvent_Transaction.setAccountBalance((transactionNode \\ "AcctBal").text.toString)
      txExceptionalEvent_Transaction.setBorrowingSigns((transactionNode \\ "DbtCrdtFlg").text.toString)
      txExceptionalEvent_Transaction.setCashMark((transactionNode \\ "CshTrsfFlg").text.toString)
      txExceptionalEvent_Transaction.setCurrency((transactionNode \\ "TranCcy").text.toString)
      txExceptionalEvent_Transaction.setIpAddress((transactionNode \\ "IP").text.toString)
      txExceptionalEvent_Transaction.setLogNumber((transactionNode \\ "LogSeqNo").text.toString)
      txExceptionalEvent_Transaction.setMac((transactionNode \\ "MACAdr").text.toString)
      txExceptionalEvent_Transaction.setMerchantCode((transactionNode \\ "MrchNo").text.toString)
      txExceptionalEvent_Transaction.setMerchantName((transactionNode \\ "MrchName").text.toString)
      txExceptionalEvent_Transaction.setOpponentAccountNumber((transactionNode \\ "TrgtAcctNo").text.toString)
      txExceptionalEvent_Transaction.setOpponentCredentialNumber((transactionNode \\ "TrgtIdCode").text.toString)
      txExceptionalEvent_Transaction.setOpponentDepositBankID((transactionNode \\ "TrgtBnkNo").text.toString)
      txExceptionalEvent_Transaction.setOpponentName((transactionNode \\ "TrgtAcctNm").text.toString)
      txExceptionalEvent_Transaction.setRemark((transactionNode \\ "Remark").text.toString)
      txExceptionalEvent_Transaction.setSummonsNumber((transactionNode \\ "VchrSeqNo").text.toString)
      txExceptionalEvent_Transaction.setTellerCode((transactionNode \\ "TranTlrId").text.toString)
      txExceptionalEvent_Transaction.setTerminalNumber((transactionNode \\ "TranTmnlNo").text.toString)
      txExceptionalEvent_Transaction.setTransactionAddress((transactionNode \\ "TranAdr").text.toString)
      txExceptionalEvent_Transaction.setTransactionAmount((transactionNode \\ "TranAmt").text.toString)
      txExceptionalEvent_Transaction.setTransactionBranchCode((transactionNode \\ "TranBrchId").text.toString)
      txExceptionalEvent_Transaction.setTransactionBranchName((transactionNode \\ "TranBrchNm").text.toString)
      txExceptionalEvent_Transaction.setTransactionRemark((transactionNode \\ "TranRmrk").text.toString)
      txExceptionalEvent_Transaction.setTransactionSerial((transactionNode \\ "TranSeqNo").text.toString)
      txExceptionalEvent_Transaction.setTransactionStatus((transactionNode \\ "TranSt").text.toString)
      txExceptionalEvent_Transaction.setTransactionTime((transactionNode \\ "TranOcrTmStmp").text.toString)
      txExceptionalEvent_Transaction.setTransactionType((transactionNode \\ "TranType").text.toString)
      txExceptionalEvent_Transaction.setVoucherCode((transactionNode \\ "VchrType").text.toString)
      txExceptionalEvent_Transaction.setVoucherType((transactionNode \\ "VchrNo").text.toString)
      transactionList.add(txExceptionalEvent_Transaction)
    }
    log.info(s"transactionList,${transactionList.size}")
    transactionList
  }
}