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
 * 可疑名单上报-100403
 * @author andy
 */
class CFCAReportOpenCardTask extends SchedulerWorker with TDHDbService with CFCAService {

  def start() = {
    log.info("Start CFCAReportOpenCardTask")
    val tasks: List[Map[String, String]] = queryT100403()
    log.info(s"Find ${tasks.size} 100403 task to report")

    tasks.foreach { task =>
      updateT100403(task("id"))
    }
    
    if (tasks.size > 0)
      sendProxy(TEvent(HfbkUtil.getUUID(), ReportDoubtCard, tasks, HfbkUtil.getTimeStamp, Some(self)))
    else
      log.info("do not need to Report 100403 task")
  }
}