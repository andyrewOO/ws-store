package com.egfbank.tfemp.actor.worker.scheduler.job

import scala.collection.immutable.List
import scala.xml.Elem
import com.egfbank.tfemp.actor._
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.worker.SchedulerWorker
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCACheckService

/**
 * @author andy
 */
class CFCARequestTask extends SchedulerWorker with CFCAService with CFCACheckService with CFCADbService {

  //定时运行前置机报文获取任务
  def start() = {
    log.info("Start CFCARequestTask")
    val msg: List[Elem] = getNewMessage()
    //获取测试数据
//    val msg: List[Elem] = getTestMessage()
    msg.foreach { xml =>
      log.info("前置机下行指令:" + (xml \\ "ApplicationID").text.toString)
      //写入下行队列
      insertIntoDownQueue(xml) match {
        case 1 => {
          //根据不同业务验证CFCA报文是否正确
        	val transSerialNumber = (xml \\ "TransSerialNumber").text.toString()
          val cfcaCheckEvent = checkCFCAXML(xml)
          if (cfcaCheckEvent.isPassed) {
            //如果验证正确则更改down表中标记为为“验证正确”,否则标记为“验证失败”并响应错误消息
            updateDownQueue(transSerialNumber, Map("STATUS" -> CFCADbService.Task_CKS))
          }else {
            updateDownQueue(transSerialNumber, Map("STATUS" -> CFCADbService.Task_CKF))
            respondCFCAErrorMsg(xml, cfcaCheckEvent.result, cfcaCheckEvent.failureCause, cfcaCheckEvent.feedbackRemark)
          }
        }
        //插入数据库失败
        case _ => 
      }
      //获取指令后直接下发
      //      createTEvent(xml) match {
      //        case Some(tevent: TEvent[Elem]) => sendProxy(tevent)
      //        case None                       =>
      //      }
    }
  }

  private def createTEvent(xml: Elem): Option[TEvent[Elem]] = {

    (xml \\ "TxCode").text.toString() match {
      //止付
      case CFCATradeCode.stopPayment                 => Some(TEvent(HfbkUtil.getUUID(), StopPay, xml, HfbkUtil.getTimeStamp, Some(self)))
      //止付解除
      case CFCATradeCode.stopPaymentLift             => Some(TEvent(HfbkUtil.getUUID(), StopPayFree, xml, HfbkUtil.getTimeStamp, Some(self)))
      //止付延期
      case CFCATradeCode.stopPaymentDelay            => Some(TEvent(HfbkUtil.getUUID(), StopPayDelay, xml, HfbkUtil.getTimeStamp, Some(self)))
      //冻结
      case CFCATradeCode.freezePay                   => Some(TEvent(HfbkUtil.getUUID(), FreezePay, xml, HfbkUtil.getTimeStamp, Some(self)))
      //冻结解除
      case CFCATradeCode.freezePayFree               => Some(TEvent(HfbkUtil.getUUID(), FreezePayFree, xml, HfbkUtil.getTimeStamp, Some(self)))
      //冻结延期
      case CFCATradeCode.freezePayDelay              => Some(TEvent(HfbkUtil.getUUID(), FreezePayDelay, xml, HfbkUtil.getTimeStamp, Some(self)))
      //帐户交易明细查询
      case CFCATradeCode.queryAccountTransactionInfo => Some(TEvent(HfbkUtil.getUUID(), QueryTransDetail, xml, HfbkUtil.getTimeStamp, Some(self)))
      //帐户持卡主体查询
      case CFCATradeCode.queryAccountMainO           => Some(TEvent(HfbkUtil.getUUID(), QueryCardOwner, xml, HfbkUtil.getTimeStamp, Some(self)))
      //帐户动态查询
      case CFCATradeCode.queryAccountDynamic         => Some(TEvent(HfbkUtil.getUUID(), QueryAccMonitor, xml, HfbkUtil.getTimeStamp, Some(self)))
      //帐户动态查询解除
      case CFCATradeCode.queryAccountDynamicLift     => Some(TEvent(HfbkUtil.getUUID(), QueryAccMonitorFree, xml, HfbkUtil.getTimeStamp, Some(self)))
      //客户全帐户查询
      case CFCATradeCode.queryAllInfo                => Some(TEvent(HfbkUtil.getUUID(), QueryAccAllInfo, xml, HfbkUtil.getTimeStamp, Some(self)))
      //Other
      case x                                         => log.warn("CFCA下行报文无法认别" + xml.toString()); None
    }

  }

}