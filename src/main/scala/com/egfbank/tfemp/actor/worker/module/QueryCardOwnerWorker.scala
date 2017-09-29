package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.actor.services.CFCAService
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.util.RetCode2Result
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.util.CastTool
/**
 * @author huxp
 */
class QueryCardOwnerWorker extends BizWorker with ESBService with CFCAService with CFCADbService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info("Start QueryCardOwnerWorker")
    
    var response: Elem = null
    var beanMap: Map[String, String] = null
    event.content match {
      case sourceData: Elem => {
        log.info(s"Query applicationid:${(sourceData \\ "ApplicationID").text.toString}")
        // 法律文书入库
        insertIntoForceMeasureFile(sourceData)

        // 封装请求客户信息接口Map
        val targetMap = Map(
          "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "QryMode" -> "1",
          "AcctNo" -> (sourceData \\ "CardNumber").text.toString)

        // 发送给ESB报文，并拿到返回的XML
        val responseMap = queryCusInfo(targetMap)

        // 根据核心的反馈生成业务应答码
        val result = RetCode2Result.cast((responseMap \\ "RetCode").text.toString)

        //3.由前置机报文sourceMap，和esb返回报文responseMap，拼装返回给前置机的报文
        beanMap = Map(
          "txCode" -> "100304",
          "to" -> (sourceData \\ "MessageFrom").text.toString,
          "mode" -> "01",
          "transSerialNumber" -> HfbkUtil.genTransSerialNumber(Constants.efgOrgNum),
          "applicationID" -> (sourceData \\ "ApplicationID").text.toString,
          "result" -> result,
          "accountCredentialType" -> CastTool.certificateCast((responseMap \\ "IdType").text.toString),
          "accountCredentialNumber" -> (responseMap \\ "IdCode").text.toString,
          "accountSubjectName" -> (responseMap \\ "ClientName").text.toString,
          "telNumber" -> (responseMap \\ "MobileNo").text.toString,
          "cardOperatorName" -> "",
          "cardOperatorCredentialType" -> "",
          "cardOperatorCredentialNumber" -> "",
          "residentAddress" -> (responseMap \\ "Address").text.toString,
          "residentTelNumber" -> (responseMap \\ "HomeTelNo").text.toString,
          "workCompanyName" -> (responseMap \\ "CoName").text.toString,
          "workAddress" -> (responseMap \\ "Address").text.toString,
          "workTelNumber" -> (responseMap \\ "OfcTelNo").text.toString,
          "emailAddress" -> (responseMap \\ "Email").text.toString,
          "artificialPersonRep" -> "",
          "artificialPersonRepCredentialType" -> (responseMap \\ "RprstIdTp").text.toString,
          "artificialPersonRepCredentialNumber" -> (responseMap \\ "RprstIdCd").text.toString,
          "businessLicenseNumber" -> "",
          "stateTaxSerial" -> "",
          "localTaxSerial" -> "",
          // 失败原因
          "failureCause" -> (responseMap \\ "RetMsg").text.toString,
          "feedbackRemark" -> (responseMap \\ "CoreErrMsg").text.toString)

        // 将反馈信息入库
        if (insertIntoUpQueue(beanMap) == 1) {
          queryAccountMain(beanMap, (sourceData \\ "MessageFrom").text.toString)
        }
      }
    }
    None
  }

}