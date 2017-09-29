package com.egfbank.tfemp.actor.worker.scheduler.job

import scala.xml.XML
import com.egfbank.tfemp.actor.QueryAccMonitorFree
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.worker.DefaultWorker
import com.egfbank.tfemp.actor.worker.Start
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil

/**
 * @author andy
 */
class TFScanDynamicFreeTask extends DefaultWorker with CFCAService with CFCADbService {

  //定时扫描动态查询任务表，找到需要解除的动态查询
  override def receive() = {
    case Start => {
      log.info(s"Start TFScanDynamicFreeTask")

      val tasks: List[Map[String, String]] = selectQueryDynamicFree()
      tasks.map { task =>
        log.info(s"Find one dynamic query task to execute, taskID:${task("ID")}")

        val xml = XML.loadString(task("CONTENT"))
        val tevent = TEvent(HfbkUtil.getUUID(), QueryAccMonitorFree, xml, HfbkUtil.getTimeStamp, Some(self))
        sendProxy(tevent)
      }
    }
  }
}