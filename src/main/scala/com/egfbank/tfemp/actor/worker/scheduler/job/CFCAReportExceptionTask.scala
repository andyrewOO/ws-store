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
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100405
import cfca.safeguard.tx.business.bank.TxExceptionalEvent_Accounts
import cfca.safeguard.tx.business.bank.TxExceptionalEvent_Account
import cfca.safeguard.tx.business.bank.TxExceptionalEvent_Transaction
import com.egfbank.tfemp.util.ToolUtils

/**
 * 可疑名单上报-100405
 * @author andy
 */
class CFCAReportExceptionTask extends SchedulerWorker with TDHDbService with CFCAService {

  def start() = {
    log.info("Start CFCAReportExceptionTask")
    val tasks: List[Map[String, String]] = queryT100405()
    log.info(s"Find ${tasks.size} 100405 task to report")
    
    tasks.foreach { task =>
      updateT100405(task("id"))
    }

    if (tasks.size > 0)
      sendProxy(TEvent(HfbkUtil.getUUID(), ReportException, tasks, HfbkUtil.getTimeStamp, Some(self)))
    else
      log.info("do not need to Report 100405 task")
  }
}