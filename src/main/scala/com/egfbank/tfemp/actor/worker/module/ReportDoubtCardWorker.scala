package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import xitrum.Log
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.ReportDoubtCard
import scala.collection.mutable.ListBuffer
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.CFCAService
/**
 * 100403
 * @author huxp
 */
class ReportDoubtCardWorker extends BizWorker with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Strat ReportDoubtCardWorker")

    event.content match {
      case tasks: List[Map[String, String]] => {
        log.info("Begin Report DoubtCard")

        val groupedTasks = groupByIDNumber(tasks)

        groupedTasks.foreach {
          groupedTask =>
            {
              if (insertIntoUpQueue(Map(
                "applicationID" -> groupedTask("applicationID").toString,
                "transSerialNumber" -> groupedTask("transSerialNumber").toString,
                "txCode" -> "100403",
                "to" -> groupedTask("to").toString)) == 1) {
                log.info(s"Call Front_end, applicationid:${groupedTask("applicationID")}")
                
                val responseXML = abnormalOpenCardR(groupedTask)
                val code = (responseXML \\ "Code").text.toString()
                log.info(s"submit abnormal open card task applicationid:${groupedTask("applicationID")},result code=${code}")
              }
            }
        }
        None
      }
    }
  }

  /**
   * @param tasks
   * @return
   */
  def groupByIDNumber(tasks: List[Map[String, String]]) = {
    var rtnMap: Map[String, Any] = Map()
    val rtnList = new ListBuffer[Map[String, Any]]()
    val groupTasks = tasks.groupBy { map => map("id_number") }.foreach { kv =>
      val iniMap = kv._2(0)
      val commonMap: Map[String, Any] = Map(
        "txCode" -> "100403",
        "to" -> "",
        "mode" -> "01",
        "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
        "applicationID" -> HfbkUtil.genApplicationID(ReportDoubtCard),
        "featureCode" -> iniMap("feature_code"),
        "bankID" -> Constants.efgOrgNum,
        "idType" -> iniMap("id_type"),
        "idNumber" -> iniMap("id_number"),
        "idName" -> iniMap("id_name"))
      val idNumberList = kv._2.map {
        accountsMap =>
          {
            Map(
              "cardNumber" -> accountsMap("card_number"),
              "accountOpenTime" -> accountsMap("account_open_time"),
              "accountOpenPlace" -> accountsMap("account_open_place"))
          }
      }
      rtnMap = commonMap.+("accountsList" -> idNumberList)
      rtnList.append(rtnMap)
    }
    rtnList
  }
}