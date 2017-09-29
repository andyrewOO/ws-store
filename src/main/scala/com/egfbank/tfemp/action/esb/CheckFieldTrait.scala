package com.egfbank.tfemp.action.esb

import scala.xml.Elem
import scala.collection.mutable.HashMap
import xitrum.Log

/**
 * @author andy
 */
trait CheckFieldTrait extends Log {

  def check(requestXml: Elem) {
    log.info("start checking requisite field")

    //按请求的报文匹配需要校验的字段
    val need2CheckField: Option[List[String]] = {
      getSvcId(requestXml) match {
        case EsbVerifyAction.ServiceId => {
          getSvcScnId(requestXml) match {
            case EsbVerifyAction.suspiciousAccountVer => {
              log.info("EsbVerifyAction.suspiciousAccountVer")
              Some(List(
                  "TranSeqNo", 
                  "IdType",
                  "IdCode",
                  "ChnlType"
                  ))
            }
            case EsbVerifyAction.suspiciousDoubleVer => {
              log.info("EsbVerifyAction.suspiciousDoubleVer")
              Some(List(
                  "TranSeqNo", 
                  "OutIdType", 
                  "OutIdCode", 
                  "InIdType", 
                  "InIdCode",
                  "ChnlType"))
            }
            case EsbVerifyAction.caseReport => {
              log.info("EsbVerifyAction.caseReport")
              Some(List())
            }
            case _ => {
              log.info("EsbVerifyAction.None")
              Some(List())
            }
          }
        }
        case _ => None
      }
    }

    //判断需要校验的值是否为空
    need2CheckField.map { list =>
      {
        val kv = list.map { field => (field -> getFieldV(requestXml, field)) }.toMap
        log.info("Need to check field and value:" + kv)
        val errorkv = kv.filter(p => p._2 == null || p._2.equals(""))
        errorkv.isEmpty match {
          case false => {
            val messageBuffer = new StringBuffer
            errorkv.foreach(f => messageBuffer.append(f._1 + ":" + f._2 + ","))
            messageBuffer.append("必填字段为空")
            log.info("error message:" + messageBuffer)
            throw new EsbException(messageBuffer.toString)
          }
          case true =>
        }
      }
    }
  }

  private[this] def getSvcId(requestXml: Elem): String = {
    (requestXml \\ "SvcId").text.toString
  }

  private[this] def getSvcScnId(requestXml: Elem): String = {
    (requestXml \\ "SvcScnId").text.toString
  }

  private[this] def getFieldV(xml: Elem, field: String): String = {
    (xml \\ field).text.toString
  }
}