package com.egfbank.tfemp.actor.worker.scheduler.job

import scala.concurrent.duration._
import scala.xml.XML
import com.egfbank.tfemp.actor.QueryAccMonitor
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.worker.SchedulerWorker
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.CFCAResponseError
/**
 * @author andy
 */
class TFScanUpQueue4Err extends SchedulerWorker with CFCAService with CFCADbService {

  //定时扫描上行指令表，找到没有上送成功的指令
  def start(): Unit = {
    log.info(s"Start TFScanUpQueue")
    val tasks: List[Map[String, String]] = findFeedbackError()
    tasks.map { task =>
      log.info(s"Find one Feedback failure task, applicationid:${task.getOrElse("APPLICATIONID", "")},retry feedback")

      val xml = XML.loadString(task("XMLCONTENT"))
      val tevent = TEvent(HfbkUtil.getUUID(), CFCAResponseError, xml, HfbkUtil.getTimeStamp, Some(self))
      sendProxy(tevent)
    }
  }
}