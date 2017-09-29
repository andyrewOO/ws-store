package com.egfbank.tfemp.action.esb

import scala.xml._
import com.egfbank.tfemp.action.AppAction
import com.egfbank.tfemp.actor._
import com.egfbank.tfemp.actor.worker.DefaultWorker
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import xitrum.Log
import xitrum.SkipCsrfCheck
import xitrum.annotation.POST
import xitrum.util.Loader

/**
 * @author huxp
 */
@POST("/suspiciousListVerification")
class EsbVerifyAction extends AppAction with SkipCsrfCheck with Log with CheckFieldTrait {

  def execute = {
    try {
      log.info("Start EsbVerifyAction")
      log.info("receive ESB request: \n" + requestContentString)

      val requestXml: Elem = XML.loadString(requestContentString)
      if (requestXml == null) {
        throw new EsbException("请求的XML格式不正确")
      }
      if (!Constants.ServiceId.equalsIgnoreCase((requestXml \\ "SvcId").text.toString())) {
        throw new EsbException("SvcId不正确")
      }
      // 校验输入参数
      check(requestXml)
      context.become(receiveResponse)

      val checkSwitch = Loader.propertiesFromClasspath("tfemp.properties").getProperty("checkBlackList")
      (requestXml \\ "SvcScnId").text.toString() match {
        //黑名单查验-单要素
        case Constants.suspiciousAccountVer => {
          // 查验开关
          checkSwitch match {
            case "on" => {
              log.info(s"The Property,checkBlackList,value:${checkSwitch}")
              ActorHolder.proxyActor ! TEvent[Elem](HfbkUtil.getUUID(), VerifyDoubtSingle, requestXml, HfbkUtil.getTime(), Some(self))
            }
            case "off" => {
              log.info(s"The Property,checkBlackList,value:${checkSwitch}")
              val responesXml = XML.loadString(errorMsg("service closed", "112"))
              self ! TEvent[Elem](HfbkUtil.getUUID(), VerifyDoubtSingle, responesXml, HfbkUtil.getTime(), Some(self))
            }
            case other => {
              log.info(s"The Property,checkBlackList, is error")
              val responesXml = XML.loadString(errorMsg("service error"))
              self ! TEvent[Elem](HfbkUtil.getUUID(), VerifyDoubtSingle, responesXml, HfbkUtil.getTime(), Some(self))
            }
          }
        }
        //黑名单查验-双要素
        case Constants.suspiciousDoubleVer => {
          // 查验开关
          checkSwitch match {
            case "on" => {
              log.info(s"The Property,checkBlackList,value:${checkSwitch}")
              ActorHolder.proxyActor ! TEvent[Elem](HfbkUtil.getUUID(), VerifyDoubtMulti, requestXml, HfbkUtil.getTime(), Some(self))
            }
            case "off" => {
              log.info(s"The Property,checkBlackList,value:${checkSwitch}")
              val responesXml = XML.loadString(errorMsg("service closed", "112"))
              self ! TEvent[Elem](HfbkUtil.getUUID(), VerifyDoubtMulti, responesXml, HfbkUtil.getTime(), Some(self))
            }
            case other => {
              log.info(s"The Property,checkBlackList,value:${checkSwitch}")
              val responesXml = XML.loadString(errorMsg("service error"))
              self ! TEvent[Elem](HfbkUtil.getUUID(), VerifyDoubtMulti, responesXml, HfbkUtil.getTime(), Some(self))
            }
          }
        }
        //案件举报、可疑账户上报
        case Constants.caseReport => {
          (requestXml \\ "ReportType").text.toString() match {
            //案件举报
            case Constants.caseReport_case => {
              log.info("Start caseReport_case")
              ActorHolder.proxyActor ! TEvent[Elem](HfbkUtil.getUUID(), ReportCase, requestXml, HfbkUtil.getTime(), Some(self))
            }
            case _ => {
              log.error("No match, error ReportType")
              throw new EsbException("error ReportType")
            }
          }
        }
        //可疑名单上报-异常事件
        case Constants.reportException => {
          log.info("Start reportException")
          ActorHolder.proxyActor ! TEvent[Elem](HfbkUtil.getUUID(), ReportExceptionAction, requestXml, HfbkUtil.getTime(), Some(self))
        }
        case _ => {
          log.error("No match, SvcScnId is error")
          throw new EsbException("SvcScnId is error")
        }
      }

    } catch {
      case ex: EsbException => {
        log.info("EsbVerifyAction error(EsbException)")
        respondText("""<?xml version="1.0" encoding="UTF-8"?>""" + errorMsg(ex.msg))
      }
      case t: Throwable => {
        log.info("EsbVerifyAction error(Throwable)")
        respondText("""<?xml version="1.0" encoding="UTF-8"?>""" + errorMsg(t.getMessage))
      }
    }
  }

  def receiveResponse: Receive = {
    case TEvent(_, _, responseXML, _, _) => {
      responseXML match {
        case xml: Elem => {
          log.info("Send to ESB:" + xml.mkString)
          respondXml("""<?xml version="1.0" encoding="UTF-8"?>""" + xml.mkString)
        }
        case _ => respondXml("""<?xml version="1.0" encoding="UTF-8"?>""" + errorMsg("服务异常"))
      }
    }
    case _ => respondXml("""<?xml version="1.0" encoding="UTF-8"?>""" + errorMsg("服务异常"))
  }

  private[this] def errorMsg(msg: String, code: String = "111") = {
    s"""<service><SYS_HEAD><SvcId/><SvcScnId/><CnsmSysId></CnsmSysId><PrvdSysId/><CnsmSysSeqNo/><PrvdSysSeqNo></PrvdSysSeqNo><Mac/><MacOrgId/><TranDate></TranDate><TranTime></TranTime><TranRetSt/><array><RetInf><RetCode>${code}</RetCode><RetMsg>${msg}</RetMsg></RetInf></array><PrvdSysSvrId/></SYS_HEAD><APP_HEAD></APP_HEAD><BODY></BODY></service>"""
  }
}

case class EsbException(msg: String) extends Exception

object EsbVerifyAction {
  // 交易风险维护（120020017）
  val ServiceId = "120020017"

  // 服务场景名称 : 账户涉案/可疑名单验证（02）     
  val suspiciousAccountVer = "02"

  // 服务场景名称 : 身份涉案/可疑名单验证（03） 
  val suspiciousIDVer = "03"

  // 服务场景名称 : 账户涉案/可疑名单验证（04）        
  val suspiciousDoubleVer = "04"

  // 服务场景名称: 电信诈骗可疑事件举报（05）      
  val caseReport = "05"
  val caseReport_case = "01"
  val caseReport_abnormalAccount = "02"
}