package com.egfbank.tfemp.actor.worker.scheduler.job

import scala.concurrent.duration._
import scala.xml.XML
import com.egfbank.tfemp.actor.QueryAccMonitor
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.worker.SchedulerWorker
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
/**
 * @author andy
 */
class TFScanDynamicTask extends SchedulerWorker with CFCAService with CFCADbService {

  //定时扫描动态查询表，找到需要执行的查询任务
  def start(): Unit = {
    log.info(s"Start TFScanDynamicTask")
    val tasks: List[Map[String, String]] = selectQueryDynamic()
    tasks.map { task =>
      log.info(s"Find one dynamic query task to execute, taskID:${task("ID")}")

      val xml = XML.loadString(task("CONTENT"))
      val tevent = TEvent(HfbkUtil.getUUID(), QueryAccMonitor, xml, HfbkUtil.getTimeStamp, Some(self))
      sendProxy(tevent)
    }
  }
}