package com.egfbank.tfemp.actor.services

import scala.xml.Elem
import scala.xml.XML
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.util.CFCAUtil
import com.egfbank.tfemp.util.Map2BeanUtil
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100102
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100104
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100106
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100202
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100204
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100206
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100302
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100304
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100306
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100308
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100310
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100401
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100403
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100404
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100405
import xitrum.Log
import java.io.File
import xitrum.util.Loader

/**
 * @author huxp
 */
trait CFCAService extends Log {

  def stopPayment(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start stopPayment, applicationID:${beanMap("applicationID")}")
    val b: Tx100102 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPayment, beanMap).asInstanceOf[Tx100102]
    val responseXML = CFCAUtil.Tx100102Xml(b, to)
    XML.loadString(responseXML)
  }
  def stopPaymentDelay(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start stopPaymentDelay, applicationID:${beanMap("applicationID")}")
    val b: Tx100106 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPaymentDelay, beanMap).asInstanceOf[Tx100106]
    val responseXML = CFCAUtil.Tx100106Xml(b, to)
    XML.loadString(responseXML)
  }
  def stopPaymentLift(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start stopPaymentLift, applicationID:${beanMap("applicationID")}")
    val b: Tx100104 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPaymentLift, beanMap).asInstanceOf[Tx100104]
    val responseXML = CFCAUtil.Tx100104Xml(b, to)
    XML.loadString(responseXML)
  }
  def freeze(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start freeze, applicationID:${beanMap("applicationID")}")
    val b: Tx100202 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.freezePay, beanMap).asInstanceOf[Tx100202]
    val responseXML = CFCAUtil.Tx100202Xml(b, to)
    XML.loadString(responseXML)
  }
  def freezeDelay(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start freezeDelay, applicationID:${beanMap("applicationID")}")
    val b: Tx100206 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.freezePayDelay, beanMap).asInstanceOf[Tx100206]
    val responseXML = CFCAUtil.Tx100206Xml(b, to)
    XML.loadString(responseXML)
  }
  def freezeLift(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start freezeLift, applicationID:${beanMap("applicationID")}")
    val b: Tx100204 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.freezePayFree, beanMap).asInstanceOf[Tx100204]
    val responseXML = CFCAUtil.Tx100204Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryTransDetail(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start queryTransDetail, applicationID:${beanMap("applicationID")}")
    val b: Tx100302 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPayment, beanMap).asInstanceOf[Tx100302]
    val responseXML = CFCAUtil.Tx100302Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryTransDetail(b: Tx100302, to: String): Elem = {
    log.info(s"Start queryTransDetail, applicationID:${b.getApplicationID}")
    val responseXML = CFCAUtil.Tx100302Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAccountMain(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start queryAccountMain, applicationID:${beanMap("applicationID")}")
    val b: Tx100304 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.queryAccountMainO, beanMap).asInstanceOf[Tx100304]
    val responseXML = CFCAUtil.Tx100304Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAccountDynamic(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start queryAccountDynamic, applicationID:${beanMap("applicationID")}")
    val b: Tx100306 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPayment, beanMap).asInstanceOf[Tx100306]
    val responseXML = CFCAUtil.Tx100306Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAccountDynamic(b: Tx100306, to: String): Elem = {
    log.info(s"Start queryAccountDynamic, applicationID:${b.getApplicationID}")
    val responseXML = CFCAUtil.Tx100306Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAccountDynamicLift(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start queryAccountDynamicLift, applicationID:${beanMap("applicationID")}")
    val b: Tx100308 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPayment, beanMap).asInstanceOf[Tx100308]
    val responseXML = CFCAUtil.Tx100308Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAccountDynamicLift(b: Tx100308, to: String): Elem = {
    log.info(s"Start queryAccountDynamicLift, applicationID:${b.getApplicationID}")
    val responseXML = CFCAUtil.Tx100308Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAllInfo(beanMap: Map[String, Any], to: String): Elem = {
    log.info(s"Start queryAllInfo, applicationID:${beanMap("applicationID")}")
    val b: Tx100310 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.stopPayment, beanMap).asInstanceOf[Tx100310]
    val responseXML = CFCAUtil.Tx100310Xml(b, to)
    XML.loadString(responseXML)
  }

  def queryAllInfo(b: Tx100310, to: String): Elem = {
    log.info(s"Start queryAllInfo, applicationID:${b.getApplicationID}")
    val responseXML = CFCAUtil.Tx100310Xml(b, to)
    XML.loadString(responseXML)
  }

  def caseReport(beanMap: Map[String, Any], to: String, fromTGOrganizationId: String = "", mode: String = "01"): Elem = {
    val b: Tx100401 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.caseReport, beanMap).asInstanceOf[Tx100401]
    val responseXML = CFCAUtil.Tx100401Xml(b, fromTGOrganizationId, mode, to)
    XML.loadString(responseXML)
  }

  def abnormalOpenCardR(beanMap: Map[String, Any], fromTGOrganizationId: String = ""): Elem = {
    log.info(s"Start abnormalOpenCardR, applicationID:${beanMap("applicationID")}")
    val b: Tx100403 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.abnormalOpenCardR, beanMap).asInstanceOf[Tx100403]
    val responseXML = CFCAUtil.Tx100403Xml(b, fromTGOrganizationId)
    XML.loadString(responseXML)
  }

  def involvedAccount(beanMap: Map[String, Any], fromTGOrganizationId: String = ""): Elem = {
    log.info(s"Start involvedAccount, applicationID:${beanMap("applicationID")}")
    val b: Tx100404 = Map2BeanUtil.getBeanByTXCode(CFCATradeCode.involvedAccount, beanMap).asInstanceOf[Tx100404]
    val responseXML = CFCAUtil.Tx100404Xml(b, fromTGOrganizationId)
    XML.loadString(responseXML)
  }

  def abnormalEvent(b: Tx100405, fromTGOrganizationId: String = ""): Elem = {
    log.info(s"Start abnormalEvent, applicationID:${b.getApplicationID}")
    val responseXML = CFCAUtil.Tx100405Xml(b, fromTGOrganizationId)
    XML.loadString(responseXML)
  }

  def checkSingle(listSource: String, dataType: String, organizationID: String, data: String, accountName: String): Elem = {
    log.info(s"checkAccount,data:${data}")
    val responseXML = CFCAUtil.Tx100501Xml(listSource, dataType, organizationID, data, accountName)
    log.info(s"checkAccount,responseXML:${responseXML}")
    XML.loadString(responseXML)
  }

  def checkMultiple(
    listSource1: String, dataType1: String, organizationID1: String, data1: String, accountName1: String,
    listSource2: String, dataType2: String, organizationID2: String, data2: String, accountName2: String): Elem = {
    log.info(s"checkDAccount,data1:${data1},data2:${data2}")
    val responseXML = CFCAUtil.Tx100503Xml(
      listSource1, dataType1, organizationID1, data1, accountName1,
      listSource2, dataType2, organizationID2, data2, accountName2)
    log.info(s"checkDAccount,responseXML:${responseXML}")
    XML.loadString(responseXML)
  }

  def getNewMessage(): List[Elem] = {
    import scala.collection.immutable.List
    val s = CFCAUtil.Tx000000Xml().toArray().toList
    s.map {
      case xml: java.lang.String => XML.loadString(xml)
    }.toList
  }

  /**
   * 测试使用，使用config/testcfca/下的报文模拟前置机发送过来的报文
   * @return
   */
  def getTestMessage(): List[Elem] = {
    val file = new File("./config/testcfca/")
    val xmlFileList = file.listFiles().map { file => XML.loadString(Loader.stringFromFile(file)) }
    xmlFileList.toList
  }

}