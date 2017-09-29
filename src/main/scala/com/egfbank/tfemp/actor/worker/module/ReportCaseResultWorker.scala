package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.ReportCase
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.util.CreateXML2ESBUtil
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.util.XMLUtil
import com.egfbank.tfemp.actor.services.FileService
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.services.CFCADbService
import scala.collection.mutable.HashMap
import com.egfbank.tfemp.util.ToolUtils
/**
 * @author huxp
 */
class ReportCaseResultWorker extends BizWorker with CFCAService with CFCADbService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start ReportCaseResultWorker")
    event.content match {
      case elem:Elem => insertCaseReport(elem)
      case xmlStr:String => insertCaseReport(xmlStr)
      case _ => 
    }
    None
  }

}