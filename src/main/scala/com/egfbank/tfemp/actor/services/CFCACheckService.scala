package com.egfbank.tfemp.actor.services

import xitrum.Log
import scala.xml.Elem
import com.egfbank.tfemp.constant.CFCATradeRegex
import com.egfbank.tfemp.util.XMLUtil
import com.egfbank.tfemp.constant.CFCADataMap
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.model.CFCACheckEvent
import com.egfbank.tfemp.constant.CFCAResult
import com.egfbank.tfemp.constant.Constants

/**
 * @author XuHaibin
 */
trait CFCACheckService extends Log with CFCAService with CFCAErrorResponse {

  /**
   * 检查通用字段以及业务字段是否具备
   * @param xml
   * @return
   */
  def checkCFCAXML(xml: Elem) = {
    val commonCheckEvent = checkCommonFields(xml)
    if (commonCheckEvent.isPassed) checkTransFields(xml) else commonCheckEvent
  }

  /**
   * @param xml
   * @return
   */
  def checkCommonFields(xml: Elem) = {
    //校验bankid字段是否合规，只支持恒丰+5个村镇银行
    val bankid = (xml \\ "BankID").text.toString()
    bankid match {
      case Constants.efgOrgNum | Constants.ChongQingJiangBeiOrgNum | Constants.ChongQingYunYangOrgNum | Constants.TongLuOrgNum | Constants.YangZhongOrgNum | Constants.GuangAnOrgNum => 
         CFCACheckEvent(true, "", "", "")
      case _ => CFCACheckEvent(false, "BankID not exist", "", "BankID not exist")
    }
  }

  /**
   * @param xml
   * @return
   */
  def checkTransFields(xml: Elem) = {
    val txCode = (xml \\ "TxCode").text.toString()
    val transCheckEvent = txCode match {
      //强制措施6个接口
      case CFCATradeRegex.forceMeasure() => checkForceMeasure(txCode, xml)
      case _                             => CFCACheckEvent(true, "", "", "")
    }
    transCheckEvent
  }

  /**
   * @param txCode
   * @param xml
   */
  def checkForceMeasure(txCode: String, xml: Elem) = {
    txCode match {
      case CFCATradeRegex.paymentRegex()   => checkStopPay(txCode, xml)
      case CFCATradeRegex.freezePayRegex() => checkFreezePay(txCode, xml)
      case _                               => CFCACheckEvent(true, "", "", "")
    }
  }

  /**
   * @param txCode
   * @param xml
   */
  def checkStopPay(txCode: String, xml: Elem) = {
    //检查法律文书
    checkStopPayFile(xml)
  }

  /**
   * @param xml
   * @return
   */
  def checkStopPayFile(xml: Elem) = {
    //检查法律文书为两个警官证和一个文书
    val fileNames = getFileNamesFromXml(xml).map(x => x.getOrElse("Filename", ""))
    val filesType = fileNames.map { x => x.substring(0, 2) }
    val officeCardCount = filesType.filter { CFCADataMap.CFCAFileType_OfficeCard.equals(_) }.size
    val stopPayExcelCount = filesType.filter { CFCADataMap.CFCAFileType_StopPayExcel.equals(_) }.size
    officeCardCount == 2 && stopPayExcelCount == 1 match {
      case true => CFCACheckEvent(true, "", "", "")
      case _    => CFCACheckEvent(false, CFCAResult.FILE_LOSS, "", "止付报文缺失相应法律文书文件")
    }
  }
  /**
   * @param txCode
   * @param xml
   */
  def checkFreezePay(txCode: String, xml: Elem) = {
    //检查法律文书
    checkFreezePayFile(xml)
  }

  /**
   * @param xml
   * @return
   */
  def checkFreezePayFile(xml: Elem) = {
    //检查法律文书为两个警官证和一个文书
    val fileNames = getFileNamesFromXml(xml).map(x => x.getOrElse("Filename", ""))
    val filesType = fileNames.map { x => x.substring(0, 2) }
    val officeCardCount = filesType.filter { CFCADataMap.CFCAFileType_OfficeCard.equals(_) }.size
    val stopPayExcelCount = filesType.filter { CFCADataMap.CFCAFileType_FreezePaper.equals(_) }.size
    officeCardCount == 2 && stopPayExcelCount == 1 match {
      case true => CFCACheckEvent(true, "", "", "")
      case _    => CFCACheckEvent(false, CFCAResult.FILE_LOSS, "", "冻结报文缺失相应法律文书文件")
    }
  }

  /**
   * @param xml
   * @return
   */
  def getFileNamesFromXml(xml: Elem) = {
    XMLUtil.getListMap("Attachment", xml.toString).map { file => file.filterKeys { key => "Filename".equals(key) } }
  }

  /**
   * 根据不同的交易类型响应前置机错误信息
   * @param xml
   * @return
   */
  def respondCFCAErrorMsg(xml: Elem, result: String, failureCause: String, feedbackRemark: String) = {
    val txCode = (xml \\ "TxCode").text.toString()
    txCode match {
      case CFCATradeCode.stopPayment      => stopPayment(getStopPayResponse(xml, result, failureCause, feedbackRemark), (xml \\ "MessageFrom").text.toString)
      case CFCATradeCode.stopPaymentDelay => stopPaymentDelay(getStopPayResponse(xml, result, failureCause, feedbackRemark), (xml \\ "MessageFrom").text.toString)
      case CFCATradeCode.stopPaymentLift  => stopPaymentLift(getStopPayResponse(xml, result, failureCause, feedbackRemark), (xml \\ "MessageFrom").text.toString)
      case CFCATradeCode.freezePay        => freeze(getStopPayResponse(xml, result, failureCause, feedbackRemark), (xml \\ "MessageFrom").text.toString)
      case CFCATradeCode.freezePayDelay   => freezeDelay(getStopPayResponse(xml, result, failureCause, feedbackRemark), (xml \\ "MessageFrom").text.toString)
      case CFCATradeCode.freezePayFree    => freezeLift(getStopPayResponse(xml, result, failureCause, feedbackRemark), (xml \\ "MessageFrom").text.toString)
      case _                              =>
    }
  }
}