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
import com.egfbank.tfemp.util.ToolUtils
/**
 * 100405
 * @author huxp
 */
class ReportExceptionWorker extends BizWorker with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Strat ReportExceptionWorker")

    event.content match {
      case tasks: List[Map[String, String]] => {
        log.info("Begin Report Exception")
        val accountsList = groupByICardNumber(tasks).toList

        val tx100405: Tx100405 = new Tx100405()
        tx100405.setAccountsList(ToolUtils.scalaList2JavaList(accountsList))
        tx100405.setApplicationID(HfbkUtil.genApplicationID(ReportException))
        tx100405.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))
        tx100405.setBankID(Constants.efgOrgNum)

        if (insertIntoUpQueue(Map(
          "applicationID" -> tx100405.getApplicationID,
          "transSerialNumber" -> tx100405.getTransSerialNumber,
          "txCode" -> "100403",
          "to" -> tx100405.getMessageFrom)) == 1) {
          log.info(s"Call Front_end, applicationid:${tx100405.getApplicationID}")
          
          val responseXML = abnormalEvent(tx100405)
          val code = (responseXML \\ "Code").text.toString
          log.info(s"submit abnormal open card task applicationid:${tx100405.getApplicationID},result code=${code}")
        }
      }
    }
    None
  }

  /**
   * @param tasks
   * @return accountsList[Map[String, Any]]
   */
  def groupByICardNumber(tasks: List[Map[String, String]]) = {

    val txExceptionalEvent_Accounts = tasks.groupBy(_.get("card_number")).map { accountsList =>
      val txExceptionalEvent_Accounts = new TxExceptionalEvent_Accounts()
      val iniAccountsMap = accountsList._2(0)
      //accounts标签下字段
      txExceptionalEvent_Accounts.setAccountName(iniAccountsMap("account_name"))
      txExceptionalEvent_Accounts.setCardNumber(iniAccountsMap("card_number"))

      val txExceptionalEvent_AccountList = accountsList._2.groupBy(_.get("account_number")).map { accountList =>
        val txExceptionalEvent_Account = new TxExceptionalEvent_Account()
        val iniAccountMap = accountList._2(0)
        //account标签下字段
        txExceptionalEvent_Account.setAccountNumber(iniAccountMap("account_number"))
        txExceptionalEvent_Account.setAccountSerial(iniAccountMap("account_serial"))
        txExceptionalEvent_Account.setAccountType(iniAccountMap("account_type"))
        txExceptionalEvent_Account.setAccountStatus(iniAccountMap("account_status"))
        txExceptionalEvent_Account.setCurrency(iniAccountMap("currency"))

        val txExceptionalEvent_TransactionList = accountList._2.map { transMap =>
          val txExceptionalEvent_Transaction = new TxExceptionalEvent_Transaction()
          txExceptionalEvent_Transaction.setFeatureCode(transMap("feature_code"))
          txExceptionalEvent_Transaction.setTransactionType(transMap("transaction_type"))
          txExceptionalEvent_Transaction.setBorrowingSigns(transMap("borrowing_signs"))
          txExceptionalEvent_Transaction.setCurrency(transMap("currency_trans"))
          txExceptionalEvent_Transaction.setTransactionAmount(transMap("transaction_amount"))
          txExceptionalEvent_Transaction.setAccountBalance(transMap("account_balance"))
          txExceptionalEvent_Transaction.setTransactionTime(transMap("transaction_time"))
          txExceptionalEvent_Transaction.setTransactionSerial(transMap("transaction_serial"))
          txExceptionalEvent_Transaction.setOpponentAccountNumber(transMap("opponent_account_number"))
          txExceptionalEvent_Transaction.setOpponentDepositBankID(transMap("opponent_deposit_bank_id"))
          txExceptionalEvent_Transaction.setTransactionBranchName(transMap("transaction_branch_name"))
          txExceptionalEvent_Transaction.setTransactionBranchCode(transMap("transaction_branch_code"))
          txExceptionalEvent_Transaction.setCashMark(transMap("cash_mark"))
          txExceptionalEvent_Transaction.setTransactionStatus(transMap("transaction_status"))
          txExceptionalEvent_Transaction
        }
        //transactionList
        txExceptionalEvent_Account.setTransactionList(ToolUtils.scalaList2JavaList(txExceptionalEvent_TransactionList))
        txExceptionalEvent_Account
      }
      txExceptionalEvent_Accounts.setAccountList(ToolUtils.scalaList2JavaList(txExceptionalEvent_AccountList.toList))
      txExceptionalEvent_Accounts
    }
    txExceptionalEvent_Accounts.toList
  }
}