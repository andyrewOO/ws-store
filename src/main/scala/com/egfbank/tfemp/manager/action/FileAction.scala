package com.egfbank.tfemp.manager.action

import xitrum.SkipCsrfCheck
import xitrum.Log
import com.egfbank.tfemp.action.AppAction
import xitrum.annotation.POST
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.action.AppQueryResult
import java.io.FileOutputStream
import java.io.File
import sun.misc.BASE64Decoder
import xitrum.annotation.GET


/**
 * @author ly
 */
@POST("/cfca/file")
@GET("/cfca/file")
class FileAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    requestMapString match {
      case paramMap: Map[String, String] => {
        log.info(s"receive paramMap,${paramMap}")
//        val fileBytes:Array[Byte] = getFileContent(paramMap)
        val fileBytes = getFileContent3(paramMap)
        log.info(s"query forceMeasure,size:${fileBytes.size}")
        response.headers().add("content-type","image/jpeg")
        respondBinary(fileBytes.getBytes)
      }
      //获取请求参数失败
      case _ => {
        log.info("获取请求参数失败！")

      }
    }
    def decodeBase64ToImage(base64: String, path: String, imgName: String) = {
    val decoder = new BASE64Decoder
    try {
      val write = new FileOutputStream(new File(path + imgName))
      val decoderBytes = decoder.decodeBuffer(base64.replace(" ", ""))
      write.write(decoderBytes)
      write.close
    } catch {
      case t: Throwable => log.info("Decode the Base64 error")
    }
  }
  }


}