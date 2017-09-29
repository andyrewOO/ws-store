package com.egfbank.tfemp.actor.services

import xitrum.Log
import scala.xml.Elem
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.constant.Constants

/**
 * @author XuHaibin
 */
trait CFCAErrorResponse extends Log {

  /**
   * @param sourceData
   * @param result
   * @param failureCause
   * @param feedbackRemark
   * @return
   */
  def getStopPayResponse(sourceData: Elem, result: String, failureCause: String, feedbackRemark: String): Map[String, Any] = {
    Map(
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString(),
      "txCode" -> "100102",
      "mode" -> "01",
      "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
      "result" -> result,
      "failureCause" -> failureCause,
      "feedbackRemark" -> feedbackRemark)
  }

  def getStopPayDalayResponse(sourceData: Elem, result: String, failureCause: String, feedbackRemark: String) = {
    Map(
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString(),
      "txCode" -> "100106",
      "mode" -> "01",
      // 传输报文流水号
      "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
      "result" -> result,
      // 帐卡号
      "failureCause" -> failureCause,
      "feedbackRemark" -> feedbackRemark)
  }

  def getStopPayLiftResponse(sourceData: Elem, result: String, failureCause: String, feedbackRemark: String) = {
    Map(
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString(),
      "txCode" -> "100104",
      "mode" -> "01",
      "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
      "result" -> result,
      "failureCause" -> failureCause,
      "feedbackRemark" -> feedbackRemark)
  }

  def getFreezeReponse(sourceData: Elem, result: String, failureCause: String, feedbackRemark: String) = {
    Map(
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString(),
      "txCode" -> "100202",
      "mode" -> "01",
      "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
      "result" -> result,
      "failureCause" -> failureCause,
      "feedbackRemark" -> feedbackRemark)
  }

  def getFreezeDalayResponse(sourceData: Elem, result: String, failureCause: String, feedbackRemark: String) = {
    Map(
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString(),
      "txCode" -> "100206",
      "mode" -> "01",
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
      "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
      "result" -> result,
      "failureCause" -> failureCause,
      "feedbackRemark" -> feedbackRemark)
  }

  def getFreezeLiftResponse(sourceData: Elem, result: String, failureCause: String, feedbackRemark: String) = {
    Map(
      "applicationID" -> (sourceData \\ "ApplicationID").text.toString(),
      "txCode" -> "100204",
      "mode" -> "01",
      "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
      "result" -> result,
      "withdrawalTime" -> HfbkUtil.getXMLSecond(),
      "failureCause" -> failureCause,
      "feedbackRemark" -> feedbackRemark)
  }
}