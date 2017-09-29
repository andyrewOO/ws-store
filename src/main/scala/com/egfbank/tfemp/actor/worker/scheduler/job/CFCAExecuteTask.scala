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

/**
 * @author andy
 */
class CFCAExecuteTask extends SchedulerWorker with CFCADbService {

  //定时扫描下行指令表
  def start() = {
    log.info("Start CFCAExecuteTask")
    val tasks: List[Map[String, String]] = selectDownQueue4Exe()

    tasks.foreach { task =>
      log.info(s"Find one task to execute, taskID:${task("ID")}")
      //获取指令报文然后下发
      createTEvent(XML.loadString(task("XMLCONTENT"))) match {
        case Some(tevent: TEvent[Elem]) => {
          val r = updateDownQueue(task("TRANSSERIALNUMBER"), Map("STATUS" -> CFCADbService.Task_EXE))
          if (r == 1) sendProxy(tevent)
        }
        case None =>
      }
    }
  }

  private def createTEvent(xml: Elem): Option[TEvent[Elem]] = {

    (xml \\ "TxCode").text.toString() match {
      //止付
      case CFCATradeCode.stopPayment => {
        Some(TEvent(HfbkUtil.getUUID(), StopPay, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //止付解除
      case CFCATradeCode.stopPaymentLift => {
        Some(TEvent(HfbkUtil.getUUID(), StopPayFree, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //止付延期
      case CFCATradeCode.stopPaymentDelay => {
        Some(TEvent(HfbkUtil.getUUID(), StopPayDelay, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //冻结
      case CFCATradeCode.freezePay => {
        Some(TEvent(HfbkUtil.getUUID(), FreezePay, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //冻结解除
      case CFCATradeCode.freezePayFree => {
        Some(TEvent(HfbkUtil.getUUID(), FreezePayFree, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //冻结延期
      case CFCATradeCode.freezePayDelay => {
        Some(TEvent(HfbkUtil.getUUID(), FreezePayDelay, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //帐户交易明细查询
      case CFCATradeCode.queryAccountTransactionInfo => {
        Some(TEvent(HfbkUtil.getUUID(), QueryTransDetail, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //帐户持卡主体查询
      case CFCATradeCode.queryAccountMainO => {
        Some(TEvent(HfbkUtil.getUUID(), QueryCardOwner, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //帐户动态查询
      case CFCATradeCode.queryAccountDynamic => {
        Some(TEvent(HfbkUtil.getUUID(), QueryAccMonitor, xml, HfbkUtil.getTimeStamp, None))
      }
      //帐户动态查询解除
      case CFCATradeCode.queryAccountDynamicLift => {
        Some(TEvent(HfbkUtil.getUUID(), QueryAccMonitorFree, xml, HfbkUtil.getTimeStamp, None))
      }
      //客户全帐户查询
      case CFCATradeCode.queryAllInfo => {
        Some(TEvent(HfbkUtil.getUUID(), QueryAccAllInfo, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //案件举报反馈
      case CFCATradeCode.caseReportResult => {
    	  Some(TEvent(HfbkUtil.getUUID(), ReportCaseResult, xml, HfbkUtil.getTimeStamp, Some(self)))
      }
      //Other
      case x => log.warn("CFCA下行报文无法认别" + xml.toString()); None
    }
  }
}