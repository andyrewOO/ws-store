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
class ReportCaseWorker extends BizWorker with FileService with CFCAService with CFCADbService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start ReportCaseWorker")
    var responseXml: Elem = null
    event.content match {
      case requestXML: Elem => {
        try {
          //获取案件举报文件
          val fileNames = XMLUtil.getListMap("FileNmDtl", requestXML.toString)
          val attachements = getCaseReportFiles(fileNames)
          log.info(s"Get attachements successful,size:${attachements.size}")

          //获取案件举报交易明细
          val transactionList = XMLUtil.getListMap("TranDtlRcrd", requestXML.toString)
          //封装前置机map
          val targetMap = Map(
            "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
            "applicationID" -> HfbkUtil.genApplicationID(ReportCase),
            "applicationType" -> (requestXML \\ "CaseRptTp").text.toString(),
            "reportEndTime" -> HfbkUtil.getXMLSecond(),
            "victimName" -> (requestXML \\ "ClientName").text.toString(),
            "victimPhoneNumber" -> (requestXML \\ "CnctTelNo").text.toString(),
            "victimIDType" -> (requestXML \\ "IdType").text.toString(),
            "victimIDNumber" -> (requestXML \\ "IdCode").text.toString(),
            "accidentDescription" -> (requestXML \\ "TranDsc").text.toString(),
            "operatorName" -> (requestXML \\ "OprtName").text.toString(),
            "operatorPhoneNumber" -> (requestXML \\ "TelNo").text.toString(),
            "txCode" -> "100401",
            "to" -> "010000000005", //需求需确定
            "reportOrgName" -> (requestXML \\ "RptBrchNm").text.toString())

          // 将案件举报信息入库
          if (insertIntoUpQueue(targetMap) == 1) {
            val requestMap = new HashMap[String, Any]
            requestMap ++= targetMap
            requestMap ++= Map(
              "attachments" -> attachements,
              "transactionList" -> transactionList)
            responseXml = caseReport(requestMap.toMap, (requestXML \\ "RptBrchNm").text.toString())
          }

          //处理返回结果
          val code = (responseXml \\ "Code").text.toString
          val description = (responseXml \\ "Description").text.toString
          val status = (responseXml \\ "Status").text.toString
          val transSerialNumber = (responseXml \\ "TransSerialNumber").text.toString

          val map2ESB = Map(
            "RetCode" -> code,
            "RetMsg" -> description,
            "MsgId" -> transSerialNumber)
          val xml2ESB = CreateXML2ESBUtil.create(CFCATradeCode.caseReport, map2ESB)
          Some(TEvent(HfbkUtil.getUUID(), ReportCase, xml2ESB, HfbkUtil.getTimeStamp(), Some(self)))
        } catch {
          case t: Throwable =>
            log.error(t.getMessage, t)
            Some(TEvent(HfbkUtil.getUUID(), ReportCase, errorMsg("服务异常"), HfbkUtil.getTimeStamp(), Some(self)))
        }
      }
      case _ => None
    }
  }

  private[this] def errorMsg(msg: String) = {
    s"""<?xml version="1.0" encoding="UTF-8"?><service><SYS_HEAD><SvcId/><SvcScnId/><CnsmSysId></CnsmSysId><PrvdSysId/><CnsmSysSeqNo/><PrvdSysSeqNo></PrvdSysSeqNo><Mac/><MacOrgId/><TranDate></TranDate><TranTime></TranTime><TranRetSt/><array><RetInf><RetCode>111</RetCode><RetMsg>${msg}</RetMsg></RetInf></array><PrvdSysSvrId/></SYS_HEAD><APP_HEAD></APP_HEAD><BODY></BODY></service>"""
  }

}