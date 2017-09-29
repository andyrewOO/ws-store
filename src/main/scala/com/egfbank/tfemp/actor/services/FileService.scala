package com.egfbank.tfemp.actor.services

import scala.xml.Elem
import com.egfbank.tfemp.actor.worker.module.FreezePayFreeWorker
import com.egfbank.tfemp.constant.CFCATradeCode
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.util.XMLUtil
import akka.actor.Actor
import akka.actor.Props
import xitrum.Log
import com.egfbank.tfemp.actor.StopPay
import com.egfbank.tfemp.actor.StopPayDelay
import com.egfbank.tfemp.actor.StopPayFree
import com.egfbank.tfemp.actor.FreezePay
import com.egfbank.tfemp.actor.FreezePayDelay
import com.egfbank.tfemp.actor.FreezePayFree
import com.egfbank.tfemp.actor.BizScene
import sun.misc.BASE64Decoder
import java.io.FileOutputStream
import java.io.File
import xitrum.util.Loader
import com.egfbank.tfemp.actor.QueryTransDetail
import com.egfbank.tfemp.actor.QueryCardOwner
import com.egfbank.tfemp.actor.QueryAccMonitor
import com.egfbank.tfemp.actor.QueryAccMonitorFree
import com.egfbank.tfemp.actor.QueryAccAllInfo
import java.io.FileInputStream
import sun.misc.BASE64Encoder

/**
 * 将法律文书保存到对应目录下
 * @author andy
 */
trait FileService extends Actor with Log {

  val sftpInfo = Loader.propertiesFromClasspath("tfemp.properties")
  // 基本目录
  val base = sftpInfo.getProperty("base") + "file/"

  def saveFile(xml: Elem) = {

    val tradeType: BizScene = (xml \\ "TxCode").text.toString match {
      case CFCATradeCode.stopPayment                 => StopPay
      case CFCATradeCode.stopPaymentDelay            => StopPayDelay
      case CFCATradeCode.stopPaymentLift             => StopPayFree
      case CFCATradeCode.freezePay                   => FreezePay
      case CFCATradeCode.freezePayDelay              => FreezePayDelay
      case CFCATradeCode.freezePayFree               => FreezePayFree
      case CFCATradeCode.queryAccountTransactionInfo => QueryTransDetail
      case CFCATradeCode.queryAccountMainO           => QueryCardOwner
      case CFCATradeCode.queryAccountDynamic         => QueryAccMonitor
      case CFCATradeCode.queryAccountDynamicLift     => QueryAccMonitorFree
      case CFCATradeCode.queryAllInfo                => QueryAccAllInfo
      //      case _                                         =>
    }
    val filePath = base + tradeType + "/" + (xml \\ "ApplicationID").text.toString + "/"

    val Attachments = XMLUtil.getFVListMap("Attachments", xml.toString, "Body")
    Attachments.map { attachment =>
      val filename = attachment("Filename") + ".jpg"
      val content = attachment("Content")

      // 解码
      val decoder = new BASE64Decoder()
      val decoderBytes = decoder.decodeBuffer(content.replace(" ", ""))

      // 创建指定目录的文件
      val file = new File(filePath, filename)
      if (file.getParentFile.exists()) {
        file.createNewFile()
      } else {
        file.getParentFile.mkdirs()
        file.createNewFile()
      }

      // 向文件中写入内容
      val out = new FileOutputStream(file)
      out.write(decoderBytes)
      out.close()
    }
  }

  /**
   * 获取案件举报的所有文件
   * @param fileNames
   * @return
   * @throw fileNotFoundException
   */
  def getCaseReportFiles(fileNames: List[Map[String, String]]) = {
    val caseReportFilePath = Loader.propertiesFromClasspath("tfemp.properties").getProperty("caseReportFilePath")
    log.info(s"Get Base Path:${caseReportFilePath}")

    fileNames.map { fileNameMap =>
      val fileName = fileNameMap("FileName")
      getFileContent(caseReportFilePath + fileName)
    }
  }

  /**
   * 根据文件路径和文件名称读取文件并用base64编码
   * @param fileName
   * @return String
   */
  def getFileContent(fileName: String) = {
    log.info(s"To Read the file:${fileName}")
    //若文件不存在，应抛出异常
    val file = new File(fileName)
    val bytes = new Array[Byte](file.length().toInt)

    val in = new FileInputStream(file)
    in.read(bytes)
    in.close()

    val encoder = new BASE64Encoder
    val fileContent = encoder.encode(bytes)

    Map(
      "filename" -> file.getName,
      "content" -> fileContent)
  }
}