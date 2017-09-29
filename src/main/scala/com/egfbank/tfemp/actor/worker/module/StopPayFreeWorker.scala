package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.StopPayFree
import com.egfbank.tfemp.constant.CFCAResult
import com.egfbank.tfemp.constant.ESBRetCode
import com.egfbank.tfemp.util.XMLUtil
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.FileService
import com.egfbank.tfemp.util.RetCode2Result

/**
 * @author andy
 */
class StopPayFreeWorker extends BizWorker with ESBService with CFCAService with CFCADbService with FileService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start StopPayFreeWorker")
    var response: Elem = null
    var beanMap: Map[String, String] = null
    event.content match {
      case sourceData: Elem => {
        log.info(s"StopPayFree applicationid:${(sourceData \\ "ApplicationID").text.toString}")
        // 将强制措施指令入库保存记录
        val r1 = insertIntoForceMeasure(sourceData)
        // 将强制措施法律文书入库保存
        val r2 = if (r1 == 1) insertIntoForceMeasureFile(sourceData) else 0
        // 将强制措施法律文书解码并放到本地目录下
        saveFile(sourceData)

        val accountNumber = (sourceData \\ "AccountNumber").text.toString
        val AcctNo = if (accountNumber.contains("_")) accountNumber.split("_")(0) else accountNumber
        val SubAcctNo = if (accountNumber.contains("_")) accountNumber.split("_")(1) else ""

        // 查询前道交易，获取核心系统的冻结流水号
        val forceMeasurRecord = selectForceMeasure((sourceData \\ "OriginalApplicationID").text.toString)
        val records = forceMeasurRecord.map { record =>
          Map("ESBID" -> record("ESBID"),
            "ORIGFRZDT" -> record("ORIGFRZDT"),
            "CcyType" -> record("EXTEND1"))
        }
        val OrigInfo: Map[String, String] = if (records.length > 0) records.head else Map()
        log.info(s"OriInfo:${OrigInfo}")

        if (OrigInfo.size > 0) {
          // 找到原始记录
          val targetMap = Map(
            "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
            "BankID" -> (sourceData \\ "BankID").text.toString,
            "TranType" -> "3",
            // 原冻结时间，需要从库中取前道交易的冻结时间
            "OrigFrzDt" -> OrigInfo.getOrElse("ORIGFRZDT", ""),
            // 原冻结流水，需要从库中取出前道交易的冻结流水
            "OrigFrzSeqNo" -> OrigInfo.getOrElse("ESBID", ""),
            "AcctNo" -> AcctNo,
            "SubAcctNo" -> SubAcctNo,
            "FrzType" -> "3",
            "CtfType" -> "O",
            "FrzMode" -> "5",
            "CtfCode" -> (sourceData \\ "CaseNumber").text.toString,
            "OrgType" -> "6",
            "CcyType" -> OrigInfo.getOrElse("CcyType", ""),
            "OrgName" -> (sourceData \\ "ApplicationOrgName").text.toString,
            "TranAmt" -> (sourceData \\ "TransferAmount").text.toString,
            "CcyType" -> OrigInfo.getOrElse("CcyType", ""),
            "ExprtDate" -> (sourceData \\ "ExpireTime").text.toString,
            "OprtName" -> (sourceData \\ "OperatorName").text.toString,
            "OprtIdTp" -> (sourceData \\ "OperatorIDType").text.toString,
            "OprtIdCd" -> (sourceData \\ "OperatorIDNumber").text.toString,
            "OprtName2" -> (sourceData \\ "InvestigatorName").text.toString,
            "OprtIdTp2" -> (sourceData \\ "InvestigatorIDType").text.toString,
            "OprtIdCd2" -> (sourceData \\ "InvestigatorIDNumber").text.toString,
            "TranDsc" -> (sourceData \\ "WithdrawalRemark").text.toString,
            "CntnFrzExprtDt" -> (sourceData \\ "ExpireTime").text.toString)

          //2.发送给esb报文，并拿到返回的xml
          val responseMap = FreezeOrStop(targetMap)

          // 更新任务状态
          // 强制措施记录表中，一条强制措施执行成功与否的标志
          val status = (responseMap \\ "SuccFlag").text.toString match {
            case "1" => CFCADbService.ForceMeasure_SUCC
            case "0" => CFCADbService.ForceMeasure_FAIL
            case _   => CFCADbService.ForceMeasure_FAIL
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
            "txCode" -> "100104",
            "to" -> (sourceData \\ "MessageFrom").text.toString,
            "mode" -> "01",
            "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
            "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
            "result" -> result,
            "accountType" -> (sourceData \\ "AccountType").text.toString,
            "accountNumber" -> (sourceData \\ "AccountNumber").text.toString,
            "cardNumber" -> (sourceData \\ "AccountNumber").text.toString,
            "failureCause" -> (responseMap \\ "CoreErrMsg").text.toString,
            "feedbackRemark" -> (responseMap \\ "CoreErrMsg").text.toString)
        } else {
          // 未找到原始记录
          beanMap = Map(
            "txCode" -> "100104",
            "to" -> (sourceData \\ "MessageFrom").text.toString,
            "mode" -> "01",
            "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
            "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
            "result" -> CFCAResult.NOFOUND_ORITRAN_NORECORD,
            "accountType" -> (sourceData \\ "AccountType").text.toString,
            "accountNumber" -> (sourceData \\ "AccountNumber").text.toString,
            "cardNumber" -> (sourceData \\ "AccountNumber").text.toString,
            "failureCause" -> "未找到原始记录",
            "feedbackRemark" -> "未找到原始记录")
        }
        
        // 将反馈信息入库
        if (insertIntoUpQueue(beanMap) == 1) {
          //4.调用CFCAService文件的方法，发送给前置机
          stopPaymentLift(beanMap, (sourceData \\ "MessageFrom").text.toString)
        }
      }
    }
    None
  }
}