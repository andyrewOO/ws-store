package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.util.CreateXML2ESBUtil
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.VerifyDoubtMulti
/**
 * @author Andy
 */
class VerifyDoubtMultiWorker extends BizWorker with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start VerifyDoubtMultiWorker")
    event.content match {
      case request: Elem => {

        //交易流水
        val tranSeqNo = (request \\ "TranSeqNo").text.toString

        //名单类型(目前默认：200501)
        val listSource1 = "200501"
        //数据类型（汇出）
        val dataType1 = (request \\ "OutIdType").text.toString match {
          case "01" => "AccountNumber"
          case "02" => "IDType_IDNumber"
        }
        //机构号（汇出）
        val organizationID1 = (request \\ "OutBnkId").text.toString
        //待比对数据（汇出）
        val data1 = (request \\ "OutIdCode").text.toString
        //账户名（汇出）
        val accountName1 = (request \\ "OutAcctNm").text.toString

        //名单类型(目前默认：200501)
        val listSource2 = "200501"
        //数据类型（汇入）
        val dataType2 = (request \\ "InIdType").text.toString match {
          case "01" => "AccountNumber"
          case "02" => "IDType_IDNumber"
        }
        //机构号（汇入） 
        val organizationID2 = (request \\ "InBnkId").text.toString
        //待比对数据（汇入）
        val data2 = (request \\ "InIdCode").text.toString
        //账户名（汇入）
        val accountName2 = (request \\ "InAcctNm").text.toString

        log.info(s"Start VerifyDoubtMultiWorker,data1:${data1},data2:${data2}")
        //调用CFCA服务
        val responseXml = checkMultiple(
          listSource1, dataType1, organizationID1, data1, accountName1,
          listSource2, dataType2, organizationID2, data2, accountName2)

        log.info(s"responseXml:${responseXml}")
        val error = (responseXml \\ "error").text.toString

        if (error.equals("error")) {
          log.info("error")
          val map2ESB = Map(
            "RetCode" -> "111",
            "RetMsg" -> "fail",
            "TranSeqNo" -> tranSeqNo,
            "TranSrc" -> "cfca")

          //创建报文
          val xml2ESB = CreateXML2ESBUtil.create(CFCATradeCode.suspiciousMultiVer, map2ESB)

          //异常返回结果
          Some(TEvent(HfbkUtil.getUUID(), VerifyDoubtMulti, xml2ESB, HfbkUtil.getTimeStamp(), Some(self)))
        } else {
          log.info("correct")
          //处理返回结果
          val code = (responseXml \\ "Code").text.toString
          val description = (responseXml \\ "Description").text.toString
          val status = (responseXml \\ "Status").text.toString
          val message1 = (responseXml \\ "Message1").text.toString
          val message2 = (responseXml \\ "Message2").text.toString

          val map2ESB = Map(
            "RetCode" -> code,
            "RetMsg" -> "successful",
            "SvcId" -> "120020017",
            "SvcScnId" -> "04",
            "RetMsg" -> description,
            "TranSeqNo" -> tranSeqNo,
            "TranSt" -> status,
            "OutTranDsc" -> message1,
            "InTranDsc" -> message2,
            "TranSrc" -> "cfca")

          //创建报文
          val xml2ESB = CreateXML2ESBUtil.create(CFCATradeCode.suspiciousMultiVer, map2ESB)

          //如果验证一方账户在涉案名单/可疑名单，则记录
          status match {
            case "00" =>
            case _ =>
              val channel = (request \\ "ChnlType").text.toString()
              insertIntoVerifyFailed(dataType1, data1, dataType2, data2, channel, tranSeqNo, status, "")
          }

          //封装返回结果
          Some(TEvent(HfbkUtil.getUUID(), VerifyDoubtMulti, xml2ESB, HfbkUtil.getTimeStamp(), Some(self)))
        }
      }
      case _ => None
    }
  }
}