package com.egfbank.tfemp.actor.services

import com.egfbank.tfemp.config.TFEMPConfig
import xitrum.Log
import com.egfbank.tfemp.actor.THttpClient
import com.egfbank.tfemp.util.CreateXML2ESBUtil
import com.egfbank.tfemp.constant.ESBTradeCode
import scala.xml.Elem
import scala.xml.XML
import com.egfbank.tfemp.util.ToolUtils
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.THttpTelClient
import com.egfbank.tfemp.actor.HttpErrorEvent

/**
 * @author huxp
 */
trait ESBService extends THttpTelClient with CFCADbService with Log {

  val url = TFEMPConfig.ESBUrl

  /**
   * 将esb报文发送到esb
   */
  def postXML(content: String, appliactionid: String = "") = {
    log.debug("esb access xml : \n" + content)
    var rtn = ""
    val id = HfbkUtil.getUUID()
    val elem = XML.loadString(content)
    val dataMap = Map(
      "ID" -> id,
      "APPLICATIONID" -> appliactionid,
      "SVCID" -> (elem \\ "SvcId").text.toString(),
      "SVCSCNID" -> (elem \\ "SvcScnId").text.toString(),
      "REQUEST" -> content)
    insertIntoEsbXml(dataMap)
    try {
      val v = """<?xml version="1.0" encoding="UTF-8"?>""" + content
      rtn = try3TimesPost(HttpErrorEvent(url, v, 1))
//      val txcode = (elem \\ "TranCode").text.toString()
//      rtn = readPretendESBResponse(txcode)
      val rtnMap = Map(
        "SEQNO" -> (XML.loadString(rtn) \\ "PrvdSysSeqNo").text.toString(),
        "RESPONSE" -> rtn,
        "STATUS" -> CFCADbService.ESB_SUC)
      updateEsbXml(id, rtnMap)
    } catch {
      case t: Throwable => {
        log.error(s"An error occurred in post,${t}")
        updateEsbXmlError(id)
        updateDownQueueHttpError(appliactionid)
        insertError(appliactionid, t)
      }
    }
    log.debug("esb access result : \n" + rtn)
    rtn
  }

  /**
   * 止付和冻结
   */
  def FreezeOrStop(map: Map[String, Any]): Elem = {
    log.debug("Start FreezeOrStop")
    val appliactionid = map.getOrElse("ApplicationID", "").toString
    val xml = CreateXML2ESBUtil.create(ESBTradeCode.freezeOrStop, map).toString()
    XML.loadString(postXML(xml, appliactionid))
  }

  /**
   * 查询客户信息
   */
  def queryCusInfo(map: Map[String, Any]): Elem = {
    log.debug("Start queryCusInfo")
    val appliactionid = map.getOrElse("ApplicationID", "").toString
    val xml = CreateXML2ESBUtil.create(ESBTradeCode.queryCusInfo, map).toString()
    XML.loadString(postXML(xml, appliactionid))
  }
  /**
   * 查询强制措施
   */
  def queryForceMeasure(map: Map[String, Any]): Elem = {
    log.debug("Query Measure Info")
    val appliactionid = map.getOrElse("ApplicationID", "").toString
    val xml = CreateXML2ESBUtil.create(ESBTradeCode.queryForceMeasure, map).toString()
    XML.loadString(postXML(xml, appliactionid))
  }
  /**
   * 查询帐户列表
   */
  def queryAccListInfo(map: Map[String, Any]): Elem = {
    log.debug("Start queryAccListInfo")
    val appliactionid = map.getOrElse("ApplicationID", "").toString
    val xml = CreateXML2ESBUtil.create(ESBTradeCode.queryAccListInfo, map).toString()
    XML.loadString(postXML(xml, appliactionid))
  }
  /**
   * 查询交易明细
   */
  def queryTranDetail(map: Map[String, Any]): Elem = {
    log.debug("Start queryTranDetail")
    val appliactionid = map.getOrElse("ApplicationID", "").toString
    val xml = CreateXML2ESBUtil.create(ESBTradeCode.queryTranDetail, map).toString()
    XML.loadString(postXML(xml, appliactionid))
  }
  /**
   * 查询子帐户列表
   */
  def querySubAccountList(map: Map[String, Any]): Elem = {
    log.debug("Start querySubAccountList")
    val appliactionid = map.getOrElse("ApplicationID", "").toString
    val xml = CreateXML2ESBUtil.create(ESBTradeCode.querySubAccountList, map).toString()
    XML.loadString(postXML(xml, appliactionid))
  }

  def getCommonData() = {
    Map(
      "CnsmSysSeqNo" -> ToolUtils.createCnsmSysSeqNo(),
      "TranDate" -> HfbkUtil.getXmlToday(),
      "TranTime" -> HfbkUtil.getTime())
  }

}