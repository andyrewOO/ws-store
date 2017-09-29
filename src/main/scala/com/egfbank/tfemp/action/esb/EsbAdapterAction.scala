package com.egfbank.tfemp.action.esb

import scala.xml._
import com.egfbank.tfemp.action.AppAction
import com.egfbank.tfemp.actor.worker.DefaultWorker
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor._
import xitrum.Log
import xitrum.SkipCsrfCheck
import xitrum.annotation.POST
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.constant.ESBSvcId

/**
 * @author huxp
 */
@POST("/esbService")
class EsbAdapterAction extends AppAction with SkipCsrfCheck with Log {

  def execute = {
    log.info("receive ESB request: \n" + requestContentString)

    try {
      val requestXml: Elem = XML.loadString(requestContentString)
      (requestXml \\ "SvcId").text.toString() match {
        case ESBSvcId.QueryTfempForceMeasure => forwardTo(classOf[EsbQueryForceMeasure])
        case ESBSvcId.TransRiskMaintain      => forwardTo(classOf[EsbVerifyAction])
        case _                               => respondXml(errorMsg("无对应服务"))
      }
    } catch {
      case t: Throwable =>
        log.info("EsbVerifyAction error(Throwable)")
        respondText(errorMsg(t.getMessage))
    }
  }

  private[this] def errorMsg(msg: String) = {
    s"""<?xml version="1.0" encoding="UTF-8"?><service><SYS_HEAD><SvcId/><SvcScnId/><CnsmSysId></CnsmSysId><PrvdSysId/><CnsmSysSeqNo/><PrvdSysSeqNo></PrvdSysSeqNo><Mac/><MacOrgId/><TranDate></TranDate><TranTime></TranTime><TranRetSt/><array><RetInf><RetCode>111</RetCode><RetMsg>${msg}</RetMsg></RetInf></array><PrvdSysSvrId/></SYS_HEAD><APP_HEAD></APP_HEAD><BODY></BODY></service>"""
  }
}
