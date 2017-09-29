package com.egfbank.tfemp.actor.worker.scheduler.job

import scala.collection.immutable.List
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.worker.SchedulerWorker
import com.egfbank.tfemp.actor.services.TDHDbService

/**
 * @author andy
 */
class TFScanVerFaild extends SchedulerWorker with CFCADbService with TDHDbService {

  //定时把查验记录表内容转存到TDH环境
  def start() = {
    log.info("Start TFScanVerFaild")
    val items: List[Map[String, String]] = getVerFaild()

    items.foreach { item =>
      insertVerifyFailed(Map(
        "id" -> item.getOrElse("ID", ""),
        "serial" -> item.getOrElse("TRANS_SERIAL_NUMBER", ""),
        "channel" -> item.getOrElse("CHANNEL", ""),
        "datatype1" -> item.getOrElse("OUTIDTYPE", ""),
        "data1" -> item.getOrElse("OUTIDCODE", ""),
        "datatype2" -> item.getOrElse("INIDTYPE", ""),
        "data2" -> item.getOrElse("INIDCODE", ""),
        "time" -> item.getOrElse("TRANS_TIME", ""),
        "verifyStatus" -> item.getOrElse("VERIFY_FAILED_STATUS", ""),
        "status" -> "0"))
      updateVerFaild(item.getOrElse("ID", ""))
    }
  }
}