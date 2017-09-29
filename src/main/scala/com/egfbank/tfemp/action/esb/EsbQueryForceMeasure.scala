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

/**
 * @author andy
 */
@POST("/queryForceMeasure")
class EsbQueryForceMeasure extends AppAction with SkipCsrfCheck with Log with CFCADbService {

  val sftpInfo = Loader.propertiesFromClasspath("tfemp.properties")
  // SFTP服务器地址
  val sftpId = sftpInfo.getProperty("host") + ":" + sftpInfo.getProperty("port") + sftpInfo.getProperty("base") + "file/"

  def execute = {
    try {
      log.info("receive ESB request: \n" + requestContentString)
      val requestXml: Elem = XML.loadString(requestContentString)

      val queryMode = (requestXml \\ "QryMode").text.toString

      // 根据不同的查询模式进行查询
      val records =
        queryMode match {
          case EsbQueryForceMeasure.QueryByAcct => {
            val ACCOUNT = (requestXml \\ "AcctNo").text.toString
            val SUBACCOUNT = (requestXml \\ "SubAcctNo").text.toString

            if (ACCOUNT.trim.equals("") || SUBACCOUNT.trim.equals(""))
              throw new EsbException("缺少参数")

            selectForceMeasureByAccount(ACCOUNT, SUBACCOUNT)
          }
          case EsbQueryForceMeasure.QueryByTime => {
            val StartDate = (requestXml \\ "StartDate").text.toString
            val EndDate = (requestXml \\ "EndDate").text.toString

            if (StartDate.trim.equals("") || EndDate.trim.equals(""))
              throw new EsbException("缺少参数")

            selectForceMeasureByTime(StartDate, EndDate)
          }
          case EsbQueryForceMeasure.QueryByAcctTime => {
            val SUBACCOUNT = (requestXml \\ "SubAcctNo").text.toString
            val ACCOUNT = (requestXml \\ "AcctNo").text.toString
            val StartDate = (requestXml \\ "StartDate").text.toString
            val EndDate = (requestXml \\ "EndDate").text.toString

            if (SUBACCOUNT.trim.equals("") || ACCOUNT.trim.equals("")
              || StartDate.trim.equals("") || EndDate.trim.equals(""))
              throw new EsbException("缺少参数")

            selectForceMeasureByAccount(ACCOUNT, SUBACCOUNT, StartDate, EndDate)
          }
        }

      // 封装强制措施记录
      val array = for (record <- records) yield {

        val tradeType = record("TXCODE") match {
          case CFCATradeCode.freezePay        => FreezePay.toString
          case CFCATradeCode.freezePayDelay   => FreezePayDelay.toString
          case CFCATradeCode.freezePayFree    => FreezePayFree.toString
          case CFCATradeCode.stopPayment      => StopPay.toString
          case CFCATradeCode.stopPaymentDelay => StopPayDelay.toString
          case CFCATradeCode.stopPaymentLift  => StopPayFree.toString
          case _                              => ""
        }

        Map("TranOcrDt" -> record("EXECUTE_TIME"),
          "AcctNo" -> record("ACCOUNT"),
          "SubAcctNo" -> record("SUBACCOUNT"),
          "OprtType" -> record("TXCODE"),
          "ExcsOrgNm" -> (record("APPLICATIONORGID") + "_" + record("APPLICATIONORGNAME")),
          "FilePath" -> (sftpId + tradeType + "/" + record("APPLICATIONID")))
      }

      // 生成反馈报文
      val response = CreateXML2ESBUtil.create("120030026", Map(
        "RetMsg" -> "successful",
        "RetCode" -> ESBRetCode.SUCCESSFUL,
        "SvcId" -> "120030026",
        "SvcScnId" -> "01",
        "array" -> array))

      // 反馈
      respondXml("""<?xml version="1.0" encoding="UTF-8"?>""" + response)
    } catch {
      case ex: EsbException => {
        respondText(errorMsg(ex.getMessage))
      }
      case t: Throwable =>
        respondXml(errorMsg("服务异常"))
    }
  }

  private[this] def errorMsg(msg: String) = {
    s"""<?xml version="1.0" encoding="UTF-8"?><service><SYS_HEAD><SvcId/><SvcScnId/><CnsmSysId></CnsmSysId><PrvdSysId/><CnsmSysSeqNo/><PrvdSysSeqNo></PrvdSysSeqNo><Mac/><MacOrgId/><TranDate></TranDate><TranTime></TranTime><TranRetSt/><array><RetInf><RetCode>111</RetCode><RetMsg>${msg}</RetMsg></RetInf></array><PrvdSysSvrId/></SYS_HEAD><APP_HEAD></APP_HEAD><BODY></BODY></service>"""
  }
}
object EsbQueryForceMeasure {
  val QueryByAcct = "01"
  val QueryByTime = "02"
  val QueryByAcctTime = "03"
}