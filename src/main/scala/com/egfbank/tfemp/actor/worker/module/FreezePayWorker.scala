package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.actor.FreezePay
import com.egfbank.tfemp.util.Map2BeanUtil
import com.egfbank.tfemp.constant.CFCATradeCode
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100202
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
class FreezePayWorker extends BizWorker with ESBService with CFCAService with CFCADbService with FileService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start FreezePayWorker")
    var response: Elem = null
    var beanMap: Map[String, String] = null
    event.content match {
      case sourceData: Elem => {
        log.info(s"FreePay applicationid:${(sourceData \\ "ApplicationID").text.toString}")
        // 将强制措施指令入库保存记录
        val r1 = insertIntoForceMeasure(sourceData)
        // 将强制措施法律文书入库保存
        val r2 = if (r1 == 1) insertIntoForceMeasureFile(sourceData) else 0
        // 将强制措施法律文书解码并放到本地目录下
        saveFile(sourceData)

        val accountNumber = (sourceData \\ "AccountNumber").text.toString.split("_")
        val AcctNo = accountNumber.head
        val SubAcctNo = if (accountNumber.size > 1) accountNumber.last else ""

        // 冻结类型匹配，前置机发来01/02，转换成esb要的1/3。1为部分冻结可进，3为全额冻结不可进
        val FrzType = (sourceData \\ "FreezeType").text.toString match {
          case "01" => "1"
          case "02" => "3"
          case _    => ""
        }

        // 封装请求esb接口map
        val targetMap = Map(
          "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "BankID" -> (sourceData \\ "BankID").text.toString,
          "TranType" -> "1",
          "AcctNo" -> AcctNo,
          "SubAcctNo" -> SubAcctNo,
          "FrzType" -> FrzType,
          "FrzMode" -> "5", // 5:冻结;a:止付
          "CtfType" -> "O",
          "OrgType" -> "6",
          "OrgName" -> (sourceData \\ "ApplicationOrgName").text.toString,
          "TranAmt" -> (sourceData \\ "Balance").text.toString,
          "CcyType" -> (sourceData \\ "Currency").text.toString,
          "ExprtDate" -> (sourceData \\ "ExpireTime").text.toString,
          // 经办人信息
          "OprtName" -> (sourceData \\ "OperatorName").text.toString,
          "OprtIdTp" -> (sourceData \\ "OperatorIDType").text.toString,
          "OprtIdCd" -> (sourceData \\ "OperatorIDNumber").text.toString,
          "OprtName2" -> (sourceData \\ "InvestigatorName").text.toString,
          "OprtIdTp2" -> (sourceData \\ "InvestigatorIDType").text.toString,
          "OprtIdCd2" -> (sourceData \\ "InvestigatorIDNumber").text.toString,
          "TranDsc" -> (sourceData \\ "Remark").text.toString)

        //2.发送给esb报文，并拿到返回的xml
        val responseMap = FreezeOrStop(targetMap)

        // 强制措施记录表中，一条强制措施执行成功与否的标志
        val status = (responseMap \\ "SuccFlag").text.toString match {
          case "1" => CFCADbService.ForceMeasure_SUCC
          case "0" => CFCADbService.ForceMeasure_FAIL
          case _   => CFCADbService.ForceMeasure_FAIL
        }
        val esbid = (responseMap \\ "FrzSeqNo").text.toString
        val OrigFrzDt = (responseMap \\ "FrzDate").text.toString
        // 根据核心返回的结果,更新强制措施记录表：ESBID(核心冻结后的流水号),STATUS(表示是否成功执行),OrigFrzDt(冻结日期)
        updateForceMeasure(
          (sourceData \\ "ApplicationID").text.toString,
          Map("ESBID" -> esbid, "STATUS" -> status, "ORIGFRZDT" -> OrigFrzDt))

        // 根据核心的反馈生成业务应答码
        val result = RetCode2Result.cast((responseMap \\ "RetCode").text.toString)

        //3.由前置机报文sourceMap，和esb返回报文responseMap，拼装返回给前置机的报文
        val appliedBalance = FrzType match {
              case "1" => "-"
              case "3" => (sourceData \\ "Balance").text.toString
              case _ => ""
            }
        beanMap = Map(
          "txCode" -> "100202",
          "to" -> (sourceData \\ "MessageFrom").text.toString,
          "mode" -> "01",
          "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
          "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "result" -> result,
          "accountType" -> (sourceData \\ "AccountType").text.toString,
          "cardNumber" -> (sourceData \\ "AccountNumber").text.toString,
          "accountNumber" -> (sourceData \\ "AccountNumber").text.toString,
          
          "appliedBalance" -> appliedBalance,
          "frozedBalance" -> (responseMap \\ "ActlAmt").text.toString,
          "accountBalance" -> (responseMap \\ "OnlnBal").text.toString,
          "accountAvaiableBalance" -> (responseMap \\ "AvlbBal").text.toString,
          
          "failureCause" -> (responseMap \\ "RetMsg").text.toString,
          "feedbackRemark" -> (responseMap \\ "CoreErrMsg").text.toString)

        // 将反馈信息入库
        if (insertIntoUpQueue(beanMap) == 1) {
          //4.调用 CFCAService 文件的方法，发送给前置机
          freeze(beanMap, (sourceData \\ "MessageFrom").text.toString)
        }
      }
    }
    None
  }
}