package com.egfbank.tfemp.actor.worker.scheduler.job

import scala.collection.immutable.List
import scala.xml.Elem
import com.egfbank.tfemp.actor._
import com.egfbank.tfemp.actor.worker.SchedulerWorker
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.services.CFCADbService
import scala.xml.XML
import com.egfbank.tfemp.actor.services.TDHDbService
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.CFCAService
import scala.collection.mutable.ListBuffer

/**
 * 可疑名单上报-100404
 * @author andy
 */
class CFCAReportDoubtAccTask extends SchedulerWorker with TDHDbService with CFCAService {

  def start() = {
    log.info("Start CFCAReportDoubtAccTask")
    val tasks: List[Map[String, String]] = queryT100404()
    log.info(s"Find ${tasks.size} 100404 task to report")
    
    tasks.foreach { task =>
      updateT100404(task("id"))
    }

    if (tasks.size > 0)
      sendProxy(TEvent(HfbkUtil.getUUID(), ReportDoubtAcc, tasks, HfbkUtil.getTimeStamp, Some(self)))
    else
      log.info("do not need to Report 100404 task")
  }
}