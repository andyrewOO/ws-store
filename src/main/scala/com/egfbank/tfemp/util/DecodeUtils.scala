package com.egfbank.tfemp.util

import sun.misc.BASE64Decoder
import java.io.FileOutputStream
import java.io.File
import xitrum.Log

/**
 * @author andy
 */
object DecodeUtils extends Log {
  /**
   * 把Base64 位编码解码，并保存到指定目录
   */
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