package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.FreezePayFree
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.util.XMLUtil
import com.egfbank.tfemp.constant.CFCAResult
import com.egfbank.tfemp.constant.ESBRetCode
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.actor.services.FileService
import com.egfbank.tfemp.util.RetCode2Result

/**
 * @author andy
 */
class FreezePayFreeWorker extends BizWorker with ESBService with CFCAService with CFCADbService with FileService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start FreezePayFreeWorker")
    var response: Elem = null
    var beanMap: Map[String, String] = null
    event.content match {
      case sourceData: Elem => {
        log.info(s"FreePayFree applicationid:${(sourceData \\ "ApplicationID").text.toString}")
        // 将强制措施指令入库保存记录
        val r1 = insertIntoForceMeasure(sourceData)
        // 将强制措施法律文书入库保存
        val r2 = if (r1 == 1) insertIntoForceMeasureFile(sourceData) else 0
        // 将强制措施法律文书解码并放到本地目录下
        saveFile(sourceData)

        val accountNumber = (sourceData \\ "AccountNumber").text.toString.split("_")
        val AcctNo = accountNumber.head
        val SubAcctNo = if (accountNumber.size > 1) accountNumber.last else ""

        // 查询前道交易，获取核心系统的冻结流水号
        val forceMeasurRecord = selectForceMeasure((sourceData \\ "OriginalApplicationID").text.toString)
        val records = forceMeasurRecord.map { record =>
          Map("ESBID" -> record("ESBID"),
            "ORIGFRZDT" -> record("ORIGFRZDT"),
            "CcyType" -> record("EXTEND1"),
            "Balance" -> record("BALANCE"),
            "FreezeType" -> record("FREEZE_TYPE"))
        }
        val OrigInfo: Map[String, String] = if (records.length > 0) records.head else Map()
        log.info(s"OriInfo:${OrigInfo}")

        if (OrigInfo.size > 0) {
          val tranDsc = if ((sourceData \\ "WithdrawalRemark").text.toString.trim.equals("")) "none" else (sourceData \\ "WithdrawalRemark").text.toString
          val FrzType = OrigInfo.getOrElse("FreezeType", "") match {
            case "01" => "1"
            case "02" => "3"
            case _    => ""
          }
          val targetMap = Map(
            "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
            "BankID" -> (sourceData \\ "BankID").text.toString,
            "TranType" -> "3",
            // 解除时需要原冻结日期，这个日期核心会在冻结成功的时候返回，最好保存这个日期为原冻结日期
            "OrigFrzDt" -> OrigInfo.getOrElse("ORIGFRZDT", ""),
            // 冻结完后的流水号，要从库中取原冻结流水
            "OrigFrzSeqNo" -> OrigInfo.getOrElse("ESBID", ""),
            "AcctNo" -> AcctNo,
            "SubAcctNo" -> SubAcctNo,
            "FrzType" -> FrzType,
            "CtfType" -> "O",
            "OrgType" -> "6",
            "FrzMode" -> "5",
            "CcyType" -> OrigInfo.getOrElse("CcyType", ""),
            "OrgName" -> (sourceData \\ "ApplicationOrgName").text.toString,
            "OprtName" -> (sourceData \\ "OperatorName").text.toString,
            "OprtIdTp" -> (sourceData \\ "OperatorIDType").text.toString,
            "OprtIdCd" -> (sourceData \\ "OperatorIDNumber").text.toString,
            "OprtName2" -> (sourceData \\ "InvestigatorName").text.toString,
            "OprtIdTp2" -> (sourceData \\ "InvestigatorIDType").text.toString,
            "OprtIdCd2" -> (sourceData \\ "InvestigatorIDNumber").text.toString,
            "TranDsc" -> tranDsc)

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
          val appliedBalance = FrzType match {
            case "1" => "-"
            case "3" => OrigInfo.getOrElse("Balance", "")
            case _   => ""
          }

          beanMap = Map(
            "txCode" -> "100204",
            "to" -> (sourceData \\ "MessageFrom").text.toString,
            "mode" -> "01",
            "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),

            "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
            "result" -> result,
            "accountType" -> (sourceData \\ "AccountType").text.toString,
            "accountNumber" -> (sourceData \\ "AccountNumber").text.toString,
            "cardNumber" -> (sourceData \\ "AccountNumber").text.toString,

            "appliedBalance" -> appliedBalance,
            "unfreezeBalance" -> (responseMap \\ "ActlAmt").text.toString,
            "accountBalance" -> (responseMap \\ "OnlnBal").text.toString,

            "withdrawalTime" -> HfbkUtil.getXMLSecond(),
            "failureCause" -> (responseMap \\ "RetMsg").text.toString,
            "feedbackRemark" -> (responseMap \\ "CoreErrMsg").text.toString)
        } else {
          beanMap = Map(
            "txCode" -> "100204",
            "to" -> (sourceData \\ "MessageFrom").text.toString,
            "mode" -> "01",
            "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
            "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
            "result" -> CFCAResult.NOFOUND_ORITRAN_NORECORD,
            "accountType" -> (sourceData \\ "AccountType").text.toString,
            "accountNumber" -> (sourceData \\ "AccountNumber").text.toString,
            "withdrawalTime" -> HfbkUtil.getXMLSecond(),
            "failureCause" -> "找不到原始交易",
            "feedbackRemark" -> "找不到原始交易")
        }
        // 将反馈信息入库
        if (insertIntoUpQueue(beanMap) == 1) {
          //4.调用CFCAService文件的方法，发送给前置机
          freezeLift(beanMap, (sourceData \\ "MessageFrom").text.toString)
        }
      }
    }
    None
  }
}