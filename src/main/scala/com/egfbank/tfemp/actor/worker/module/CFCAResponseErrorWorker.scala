package com.egfbank.tfemp.actor.worker.module

import scala.xml.Elem

import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.CFCAUtil

/**
 * @author andy
 */
class CFCAResponseErrorWorker extends BizWorker with CFCAService with CFCADbService {
  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start CFCAResponseError")
    event.content match {
      case sourceData: Elem => {
        val applicationid = (sourceData \\ "ApplicationID").text.toString
        CFCAUtil.getResponseXml(sourceData.toString, applicationid)
      }
    }
    None
  }
}