package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.StopPay
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.util.XMLUtil
import com.egfbank.tfemp.constant.CFCATradeRegex
import scala.xml.Elem
import com.egfbank.tfemp.constant.ESBRetCode
import com.egfbank.tfemp.constant.CFCAResult
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.FileService
import com.egfbank.tfemp.util.RetCode2Result

/**
 * @author huxp
 */
class StopPayWorker extends BizWorker with ESBService with CFCAService with CFCADbService with FileService {

  override def execute(event: TEvent[_]): Option[TEvent[Elem]] = {
    log.info("StopPayWorker")
    var response: Elem = null
    var beanMap: Map[String, String] = null
    event.content match {
      case sourceData: Elem => {
        log.info(s"StopPay applicationid:${(sourceData \\ "ApplicationID").text.toString}")
        // 将强制措施指令入库保存记录
        val r1 = insertIntoForceMeasure(sourceData)
        // 将强制措施法律文书入库保存
        val r2 = if (r1 == 1) insertIntoForceMeasureFile(sourceData) else 0
        // 将强制措施法律文书解码并放到本地目录下
        saveFile(sourceData)

        val applicationType = (sourceData \\ "ApplicationType").text.toString
        val accountNumber = (sourceData \\ "AccountNumber").text.toString.split("_")
        val AcctNo = accountNumber.head
        val SubAcctNo = if (accountNumber.size > 1) accountNumber.last else ""

        val TransDsc = new StringBuffer((sourceData \\ "Remark").text.toString).append(";").append((sourceData \\ "Reason").text.toString)

        val targetMap = Map(
          "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "BankID" -> (sourceData \\ "BankID").text.toString,
          "TranType" -> "1",
          "AcctNo" -> AcctNo,
          "SubAcctNo" -> SubAcctNo,
          "FrzType" -> "3",
          "FrzMode" -> "5",
          "CtfType" -> "O",
          "OrgType" -> "6",
          "OrgName" -> (sourceData \\ "ApplicationOrgName").text.toString,
          "TranAmt" -> (sourceData \\ "RransferAmount").text.toString,
          "CcyType" -> (sourceData \\ "Currency").text.toString,
          "ExprtDate" -> (sourceData \\ "ExpireTime").text.toString,
          // 经办人信息
          "OprtName" -> (sourceData \\ "OperatorName").text.toString,
          "OprtIdTp" -> (sourceData \\ "OperatorIDType").text.toString,
          "OprtIdCd" -> (sourceData \\ "OperatorIDNumber").text.toString,
          "OprtName2" -> (sourceData \\ "InvestigatorName").text.toString,
          "OprtIdTp2" -> (sourceData \\ "InvestigatorIDType").text.toString,
          "OprtIdCd2" -> (sourceData \\ "InvestigatorIDNumber").text.toString,
          "TranDsc" -> TransDsc.toString)

        //2.发送给esb报文，并拿到返回的xml
        val responseMap = FreezeOrStop(targetMap)

        // 更新任务状态
        // 强制措施记录表中，一条强制措施执行成功与否的标志
        val status = (responseMap \\ "SuccFlag").text.toString match {
          case "1" => CFCADbService.ForceMeasure_SUCC
          case "0" => CFCADbService.ForceMeasure_FAIL
          case _   => "9"
        }
        val esbid = (responseMap \\ "FrzSeqNo").text.toString
        val OrigFrzDt = (responseMap \\ "FrzDate").text.toString
        // 根据核心返回的结果,更新强制措施记录表：ESBID(核心冻结后的流水号),STATUS(表示是否成功执行),OrigFrzDt(止付日期)
        updateForceMeasure(
          (sourceData \\ "ApplicationID").text.toString,
          Map("ESBID" -> esbid, "STATUS" -> status, "ORIGFRZDT" -> OrigFrzDt))

        // 根据核心的反馈生成业务应答码
        val result = RetCode2Result.cast((responseMap \\ "RetCode").text.toString)

        //3.由前置机报文sourceMap，和esb返回报文responseMap，拼装返回给前置机的报文
        beanMap = Map(
          "txCode" -> "100102",
          "to" -> (sourceData \\ "MessageFrom").text.toString,
          "mode" -> "01",
          "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
          "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "result" -> result,
          "accountType" -> (sourceData \\ "AccountType").text.toString,
          "cardNumber" -> (sourceData \\ "AccountNumber").text.toString,
          "accountNumber" -> (sourceData \\ "AccountNumber").text.toString,
          "accountBalance" -> (responseMap \\ "OnlnBal").text.toString,
          "failureCause" -> (responseMap \\ "RetMsg").text.toString,
          "feedbackRemark" -> (responseMap \\ "CoreErrMsg").text.toString)

        // 将反馈信息入库
        if (insertIntoUpQueue(beanMap) == 1) {
          //4.调用CFCAService文件的方法，发送给前置机
          stopPayment(beanMap, (sourceData \\ "MessageFrom").text.toString)
        }
      }
    }
    None
  }
}