package com.egfbank.tfemp.action.esb

import com.egfbank.tfemp.action.AppAction
import com.egfbank.tfemp.actor.BizScene
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.util.CreateXML2ESBUtil
import com.egfbank.tfemp.constant.CFCATradeCode
import scala.xml.Elem
import scala.xml.XML
import xitrum.Log
import xitrum.SkipCsrfCheck
import xitrum.annotation.POST
import xitrum.util.Loader
import com.egfbank.tfemp.constant.ESBRetCode
import com.egfbank.tfemp.actor.FreezePay
import com.egfbank.tfemp.actor.FreezePayFree
import com.egfbank.tfemp.actor.StopPay
import com.egfbank.tfemp.actor.StopPayDelay
import com.egfbank.tfemp.actor.FreezePayDelay
import com.egfbank.tfemp.actor.StopPayFree
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.services.CFCACheckService

/**
 * @author andy
 */
@POST("/test")
class TfempTest extends AppAction with SkipCsrfCheck with Log with CFCAService with CFCACheckService with CFCADbService {

  def execute = {
    log.info("receive test request: \n" + requestContentString)
    val xml: Elem = XML.loadString(requestContentString)

      insertIntoDownQueue(xml) match {
        case 1 => {
          //根据不同业务验证CFCA报文是否正确
          val transSerialNumber = (xml \\ "TransSerialNumber").text.toString()
          val cfcaCheckEvent = checkCFCAXML(xml)
          if (cfcaCheckEvent.isPassed) {
            //如果验证正确则更改down表中标记为为“验证正确”,否则标记为“验证失败”并响应错误消息
            val successCount = updateDownQueue(transSerialNumber, Map("STATUS" -> CFCADbService.Task_CKS))
            respondJsonText("执行成功："+successCount)
          }else {
            updateDownQueue(transSerialNumber, Map("STATUS" -> CFCADbService.Task_CKF))
//            respondCFCAErrorMsg(xml, cfcaCheckEvent.result, cfcaCheckEvent.failureCause, cfcaCheckEvent.feedbackRemark)
            respondJsonText("校验失败！")
          }
        }
        //插入数据库失败
        case _ =>  respondJsonText("重试！")
      }
  }
}
