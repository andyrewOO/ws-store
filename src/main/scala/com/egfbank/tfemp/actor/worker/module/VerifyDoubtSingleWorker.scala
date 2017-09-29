package com.egfbank.tfemp.actor.worker.module

import scala.xml.Elem
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.CreateXML2ESBUtil
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.VerifyDoubtSingle

/**
 * 帐户查验
 * @author Andy
 */
class VerifyDoubtSingleWorker extends BizWorker with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[Elem]] = {
    log.info("Start VerifyDoubtSingleWorker")
    event.content match {
      case request: Elem => {

        //交易流水
        val tranSeqNo = (request \\ "TranSeqNo").text.toString
        //数据类型
        val dataType = (request \\ "IdType").text.toString match {
          case "01" => "AccountNumber"
          case "02" => "IDType_IDNumber"
        }
        //机构号
        val organizationID = (request \\ "BankId").text.toString
        //待比对数据
        val data = (request \\ "IdCode").text.toString
        //账户名/姓名(取不到送空，不参与校验)
        val accountName = (request \\ "Name").text.toString

        log.info(s"Start VerifyDoubtSingleWorker,data:${data}")
        //调用CFCA服务
        val responseXml = checkSingle("200501", dataType, organizationID, data, accountName)
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
          val xml2ESB = CreateXML2ESBUtil.create(CFCATradeCode.suspiciousSingleVer, map2ESB)

          //异常返回结果
          Some(TEvent(HfbkUtil.getUUID(), VerifyDoubtSingle, xml2ESB, HfbkUtil.getTimeStamp(), Some(self)))
        } else {
          log.info("correct")
          //处理返回结果
          val code = (responseXml \\ "Code").text.toString
          val description = (responseXml \\ "Description").text.toString
          val status = (responseXml \\ "Status").text.toString
          val message = (responseXml \\ "Message").text.toString

          val map2ESB = Map(
            "RetCode" -> code,
            "RetMsg" -> "successful",
            "SvcId" -> "120020017",
            "SvcScnId" -> "02",
            "RetMsg" -> description,
            "TranSeqNo" -> tranSeqNo,
            "TranSt" -> status,
            "TranDsc" -> message,
            "TranSrc" -> "cfca")

          //创建报文
          val xml2ESB = CreateXML2ESBUtil.create(CFCATradeCode.suspiciousSingleVer, map2ESB)

          //封装返回结果
          Some(TEvent(HfbkUtil.getUUID(), VerifyDoubtSingle, xml2ESB, HfbkUtil.getTimeStamp(), Some(self)))
        }
      }
      case _ => None
    }
  }

}