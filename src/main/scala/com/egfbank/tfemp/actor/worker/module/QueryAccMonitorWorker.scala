package com.egfbank.tfemp.actor.worker.module

import com.egfbank.tfemp.actor.worker.BizWorker
import com.egfbank.tfemp.model.TEvent
import scala.xml.Elem
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.actor.services.ESBService
import com.egfbank.tfemp.constant.ESBRetCode
import cfca.safeguard.api.bank.bean.tx.upstream.Tx100306
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.util.XMLUtil
import cfca.safeguard.tx.Transaction
import java.util.ArrayList
import com.egfbank.tfemp.constant.CFCAResult
import com.egfbank.tfemp.actor.services.CFCAService
import scala.collection.mutable.ListBuffer
import com.egfbank.tfemp.util.RetCode2Result
/**
 * @author andy
 */
class QueryAccMonitorWorker extends BizWorker with CFCADbService with ESBService with CFCAService {

  override def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start QueryAccMonitorWorker")

    event.content match {
      case sourceData: Elem =>
        {
          event.tsender match {
            case Some(sender) => {
              log.info(s"Start QueryAccMonitorWorker, ${(sourceData \\ "ApplicationID").text.toString}")
              val applicationid = (sourceData \\ "ApplicationID").text.toString
              val messageFrom = (sourceData \\ "MessageFrom").text.toString
              // 电话号码是否改变
              scanPhone(sourceData) match {
                case changePhone: String => {
                  val cardNumber = (sourceData \\ "CardNumber").text.toString
                  // 查询子账户列表
                  val subAccountListInfo = querySubAccountList(Map(
                    "AcctNo" -> cardNumber,
                    "ApplicationID" -> applicationid))

                  (subAccountListInfo \\ "RetCode").text.toString match {
                    // 获取子账户列表成功
                    case ESBRetCode.SUCCESSFUL => {
                      val tx100306List = createTx100306List(sourceData, subAccountListInfo, changePhone)
                      tx100306List.map { tx100306 =>
                        tx100306.setResult(CFCAResult.SUCCESSFUL)
                        responseCFCA(tx100306, messageFrom)
                      }
                    }
                    case x => {
                      log.error(s"Get SubAccountInfo Fail,${applicationid}")
                    }
                  }
                }
                case tx100306: Tx100306 => {
                  responseCFCA(tx100306, messageFrom)
                }
              }
            }
            // 将动态查询任务落库
            case None => {
              // 法律文书入库
              insertIntoForceMeasureFile(sourceData)
              insertDynamic(sourceData)
            }
          }
        }
        None
    }
  }

  /**
   * 反馈前置机
   */
  private[this] def responseCFCA(tx100306: Tx100306, messageFrom: String) = {
    if (insertIntoUpQueue(Map(
      "applicationID" -> tx100306.getApplicationID,
      "transSerialNumber" -> tx100306.getTransSerialNumber,
      "txCode" -> "100306",
      "to" -> messageFrom), "NoCheckApplicationid") == 1) {
      // 反馈给前置机
      log.info("cfca queryAccMonitorWorker FeedBack:" + queryAccountDynamic(tx100306, messageFrom))
    }
  }

  /**
   * @param sourceData
   * @param subAccountListInfo
   * @param changePhone
   * @return
   */
  private[this] def createTx100306List(sourceData: Elem, subAccountListInfo: Elem, changePhone: String) = {
    log.info(s"Start createTx100306List,${(sourceData \\ "ApplicationID").text.toString}")
    val tx100306List = new ListBuffer[Tx100306]
    val subAccInfoList = XMLUtil.getFVListMap("array", subAccountListInfo.toString, "BODY")
    log.info(s"subAccInfoList,size:${subAccInfoList.size}")
    
    subAccInfoList.map { subAcc =>
      val acctNo = subAcc("AcctNo")
      val subAcctNo = subAcc("SubAcctNo")
      val acctNum = acctNo + "_" + subAcctNo

      // 查询交易明细--核心系统，只查当天记录
      val tranDetail = queryTranDetail(Map(
        "AcctNo" -> acctNo,
        "SubAcctNo" -> subAcctNo,
        "ApplicationID" -> (sourceData \\ "ApplicationID").text.toString))

      (tranDetail \\ "RetCode").text.toString match {
        // 获取交易明细成功
        case ESBRetCode.SUCCESSFUL => {
          val transactionsFromOneAcct = createTransactionsForOneAcct(tranDetail, changePhone)
          if (transactionsFromOneAcct.size > 0) {
            tx100306List += createTx100306(sourceData, transactionsFromOneAcct, acctNum)
          }
        }
        case x => {
          log.error("Get TransDetail Fail")
        }
      }

    }
    tx100306List
  }

  /**
   * 创建tx100306对象
   */
  private[this] def createTx100306(sourceData: Elem, trans: ArrayList[Transaction] = null, acctNum: String = null) = {
    val tx100306 = new Tx100306
    tx100306.setApplicationID((sourceData \\ "ApplicationID").text.toString)
    tx100306.setTransSerialNumber(HfbkUtil.genTransSerialNumber(Constants.efgOrgNum))
    tx100306.setAccountName((sourceData \\ "AccountName").text.toString)
    tx100306.setAccountNumber(acctNum)
    tx100306.setTransactions(trans)
    tx100306
  }

  /**
   * 创建一个账户的交易记录List
   */
  private[this] def createTransactionsForOneAcct(tranDetail: Elem, changePhone: String) = {
    log.info(s"Start createTransactionsForOneAcct")
    val transList = new ArrayList[Transaction]
    val trans = XMLUtil.getFVListMap("array", tranDetail.toString, "BODY")

    trans.map { tran =>
      val transaction = new Transaction
      transaction.setTransactionType(tran.getOrElse("TranType", ""))
      transaction.setCurrency(tran.getOrElse("CcyType", ""))
      transaction.setTransactionAmount(tran.getOrElse("TranAmt", ""))
      transaction.setAccountBalance(tran.getOrElse("TranBal", ""))
      transaction.setTransactionTime(tran.getOrElse("TranTime", ""))
      transaction.setTransactionSerial(tran.getOrElse("TranSeqNo", ""))
      transaction.setOpponentName(tran.getOrElse("TrgtAcctNm", ""))
      transaction.setOpponentAccountNumber(tran.getOrElse("TrgtAcctNo", ""))
      transaction.setOpponentCredentialNumber(tran.getOrElse("TrgtAcctNo", ""))
      transaction.setOpponentDepositBankID(tran.getOrElse("TrgtOpnAcctBrchId", ""))
      transaction.setTransactionRemark(tran.getOrElse("Remark", ""))
      transaction.setTransactionBranchName(tran.getOrElse("TranAdr", ""))
      transaction.setTransactionBranchCode(tran.getOrElse("AcptBrchId", ""))
      transaction.setLogNumber(tran.getOrElse("TranSeqNo", ""))
      transaction.setSummonsNumber("")
      transaction.setVoucherType("")
      transaction.setVoucherCode("")
      transaction.setCashMark(tran.getOrElse("CshRmtFlg", ""))
      transaction.setTerminalNumber("")
      transaction.setTransactionStatus("")
      transaction.setTransactionAddress(tran.getOrElse("TranAdr", ""))
      transaction.setMerchantName("")
      transaction.setMerchantCode("")
      transaction.setTellerCode("")
      transaction.setRemark(changePhone)

      transList.add(transaction)
    }
    transList
  }

  /**
   * 判断电话号码是否改变
   */
  private[this] def scanPhone(sourceData: Elem) = {
    log.info(s"Start scanPhone,applicationid:${(sourceData \\ "ApplicationID").text.toString}")

    val cardNumber = (sourceData \\ "CardNumber").text.toString
    val applicationid = (sourceData \\ "ApplicationID").text.toString

    // 查询客户信息,获取当前电话号码
    val cusInfo = queryCusInfo(Map(
      "QryMode" -> "1",
      "AcctNo" -> cardNumber,
      "ApplicationID" -> applicationid))

    (cusInfo \\ "RetCode").text.toString match {
      // 获取账户信息成功
      case ESBRetCode.SUCCESSFUL => {
        val nowPhone = (cusInfo \\ "MobileNo").text.toString

        // 查询动态查询记录表,获取原电话号码
        val records = selectDynamicByAppId(applicationid)
        var oriPhone = ""
        records.map { record =>
          val phone = record.getOrElse("PHONE", "")
          if (phone == null) {
            updateDynamic(applicationid, nowPhone)
            oriPhone = nowPhone
          } else oriPhone = phone
        }

        // 如果电话号码改变了，change就会有内容
        if (nowPhone.equals(oriPhone)) {
          log.info("user's Phone have not changed")
          ""
        } else {
          log.info("user's Phone have changed")
          nowPhone
        }
      }
      case x => {
        log.error(s"Get CustomerInfo Fail,${applicationid}")
        val tx100306 = createTx100306(sourceData)
        tx100306.setResult(RetCode2Result.cast(x))
        tx100306.setFeedbackRemark((cusInfo \\ "RetMsg").text.toString)
        tx100306
      }
    }
  }
}














