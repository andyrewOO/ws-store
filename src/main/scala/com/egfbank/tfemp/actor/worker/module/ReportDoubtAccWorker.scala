package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.ReportDoubtCard
import scala.collection.mutable.ListBuffer
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.services.CFCADbService
/**
 * 
 * 100404
 * @author huxp
 */
class ReportDoubtAccWorker extends BizWorker with CFCAService with CFCADbService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Strat ReportDoubtAccWorker")

    event.content match {
      case tasks: List[Map[String, String]] => {
        log.info("Begin Report DoubtAcc")
        val groupedTasks = groupByIDNumber(tasks)

        groupedTasks.foreach {
          groupedTask =>
            if (insertIntoUpQueue(Map(
              "applicationID" -> groupedTask("applicationID").toString,
              "transSerialNumber" -> groupedTask("transSerialNumber").toString,
              "txCode" -> "100404",
              "to" -> groupedTask("to").toString)) == 1) {
              log.info(s"Call Front_end, applicationid:${groupedTask("applicationID")}")
              
              val responseXML = involvedAccount(groupedTask)
              val code = (responseXML \\ "Code").text.toString()
              log.info(s"submit abnormal open card task applicationid:${groupedTask("applicationID")},result code=${code}")
            }
        }
      }
    }
    None
  }

  /**
   * @param tasks
   * @return
   */
  def groupByIDNumber(tasks: List[Map[String, String]]) = {
    val rtnList = new ListBuffer[Map[String, Any]]()
    var rtnMap: Map[String, Any] = Map()
    tasks.groupBy(_.get("id_number").get).foreach { groupedByIDNumber =>
      val rtnAccountList = new ListBuffer[Map[String, Any]]()

      val iniMap = groupedByIDNumber._2(0)
      val commonMap: Map[String, Any] = Map(
        "txCode" -> "100404",
        "to" -> "",
        "mode" -> "01",
        "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
        "applicationID" -> HfbkUtil.genApplicationID(ReportDoubtCard),
        "featureCode" -> iniMap("feature_code"),
        "bankID" -> Constants.efgOrgNum,
        "cardNumber" -> iniMap("card_number"),
        "accountName" -> iniMap("account_name"),
        "idType" -> iniMap("id_type"),
        "idNumber" -> iniMap("id_number")) //取body下的账户证件等公共字段

      val accountGroupTask =
        groupedByIDNumber._2.groupBy(_.get("account_number").get).foreach { groupedByAccountNumber =>
          val accountIniMap = groupedByAccountNumber._2(0)
          val accountCommonMap: Map[String, Any] = Map(
            "accountNumber" -> accountIniMap("account_number"),
            "accountSerial" -> accountIniMap("account_serial"),
            "accountType" -> accountIniMap("account_type"),
            "accountStatus" -> accountIniMap("account_status"),
            "currency" -> accountIniMap("currency")) //取account下的公共字段

          val transList = groupedByAccountNumber._2.map {
            transMap =>
              {
                Map(
                  "transactionType" -> transMap("transaction_type"),
                  "borrowingSigns" -> transMap("borrowing_signs"),
                  "currency" -> transMap("currency_trans"),
                  "transactionAmount" -> transMap("transaction_amount"),
                  "accountBalance" -> transMap("account_balance"),
                  "transactionTime" -> transMap("transaction_time"),
                  "transactionSerial" -> transMap("transaction_serial"),
                  "opponentAccountNumber" -> transMap("opponent_account_number"),
                  "opponentDepositBankID" -> transMap("opponent_deposit_bank_id"),
                  "transactionBranchName" -> transMap("transaction_branch_name"),
                  "transactionBranchCode" -> transMap("transaction_branch_code"),
                  "cashMark" -> transMap("cash_mark"),
                  "transactionStatus" -> transMap("transaction_status")) //取交易字段
              }
          }
          val accountRtnMap = accountCommonMap.+("transactionList" -> transList)
          rtnAccountList.append(accountRtnMap)
        }
      val rtnCommonMap = commonMap.+("accountList" -> rtnAccountList)
      rtnList.append(rtnCommonMap)
    }
    rtnList
  }
}