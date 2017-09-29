package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.CFCAService
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100308
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.constant.Constants
import scala.tools.scalap.scalax.util.StringUtil
import org.apache.commons.lang3.StringUtils
/**
 * @author huxp
 */
class QueryAccMonitorFreeWorker extends BizWorker with CFCADbService with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start QueryAccMonitorFreeWorker")
    event.content match {
      case sourceData: Elem => {
        val applicationId = (sourceData \\ "ApplicationID").text.toString
        val messageFrom = (sourceData \\ "MessageFrom").text.toString
        log.info(s"Start QueryAccMonitorFreeWorker,applicationid:${applicationId}")

        var originalApplicationID = (sourceData \\ "OriginalApplicationID").text.toString
        // 区别自动到期和解除指令（自动到期是没有originalApplicationID的）
        if (StringUtils.isBlank(originalApplicationID)) {
          log.info("Do not have OriginalApplicationID, is autoExprie")
          originalApplicationID = (sourceData \\ "ApplicationID").text.toString
        }

        val oriInfo = selectDynamicByAppId(originalApplicationID)
        if (!oriInfo.isEmpty) {
          event.tsender match {
            case Some(sender) => {
              log.info(s"Free Task,applicationid:${applicationId}")
              // 修改任务状态为解除状态，且为解除指令时才进行回馈，自动到期的不用进行回馈
              if (applicationId.equals(originalApplicationID)) {
                // 自动到期时，applicationid和originalApplicationID相同
                log.info(s"automatic expire,applicationid:${applicationId}")
                updateDynamicFree(applicationId)
                // 更新下行指令表中该指令执行完毕(4,为执行成功状态)
                updateDownQueueStatus(applicationId, "4")
              } else {
                // 手动解除时，存在原申请编号
                log.info(s"hand to Free,applicationid:${applicationId}")
                if (updateDynamicFree(originalApplicationID) == 1
                  && updateDynamicFree(applicationId) == 1) {
                  val tx100308 = new Tx100308
                  tx100308.setApplicationID(applicationId)
                  tx100308.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))
                  tx100308.setAccountName((sourceData \\ "AccountName").text.toString)
                  tx100308.setApplicationTime((sourceData \\ "ApplicationTime").text.toString)
                  tx100308.setBankID((sourceData \\ "BankID").text.toString)
                  tx100308.setBankName((sourceData \\ "BankName").text.toString)
                  tx100308.setCardNumber((sourceData \\ "CardNumber").text.toString)
                  tx100308.setCaseNumber((sourceData \\ "CaseNumber").text.toString)
                  tx100308.setApplicationOrgID((sourceData \\ "ApplicationOrgID").text.toString)
                  tx100308.setApplicationOrgName((sourceData \\ "ApplicationOrgName").text.toString)
                  tx100308.setResult("0000")
                  responseCFCA(tx100308, messageFrom)
                } else {
                  log.info("Free task fail")
                }
              }
            }
            case None => {
              log.info(s"Insert into tfemp_query_dynamic,applicationid:${applicationId}")
              // 法律文书入库
              insertIntoForceMeasureFile(sourceData)
              insertDynamic(sourceData)
            }
          }
        } else {
          log.info(s"Do not find originalApplicationID,applicationid:${applicationId}")
          val tx100308 = new Tx100308
          tx100308.setApplicationID(applicationId)
          tx100308.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))
          tx100308.setAccountName((sourceData \\ "AccountName").text.toString)
          tx100308.setApplicationTime((sourceData \\ "ApplicationTime").text.toString)
          tx100308.setBankID((sourceData \\ "BankID").text.toString)
          tx100308.setBankName((sourceData \\ "BankName").text.toString)
          tx100308.setCardNumber((sourceData \\ "CardNumber").text.toString)
          tx100308.setCaseNumber((sourceData \\ "CaseNumber").text.toString)
          tx100308.setApplicationOrgID((sourceData \\ "ApplicationOrgID").text.toString)
          tx100308.setApplicationOrgName((sourceData \\ "ApplicationOrgName").text.toString)
          tx100308.setFeedbackRemark("未找到原始交易")
          tx100308.setResult("0400")
          responseCFCA(tx100308, messageFrom)
        }
      }
    }
    None
  }

  /**
   * 反馈前置机
   */
  private[this] def responseCFCA(tx100308: Tx100308, messageFrom: String) = {
    if (insertIntoUpQueue(Map(
      "applicationID" -> tx100308.getApplicationID,
      "transSerialNumber" -> tx100308.getTransSerialNumber,
      "txCode" -> "100308",
      "to" -> messageFrom)) == 1) {
      // 反馈给前置机
      log.info("cfca queryAccMonitorWorkerFree FeedBack:" + queryAccountDynamicLift(tx100308, messageFrom))
    }
  }
}