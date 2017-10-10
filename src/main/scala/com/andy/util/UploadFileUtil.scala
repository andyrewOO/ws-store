package com.hfbank.util

import io.netty.handler.codec.http.multipart.FileUpload
import xitrum.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File

/**
 * @author win7
 */
object UploadFileUtil extends Log {

  def uploadFile(path: String, file: FileUpload) = {

    val inputf = new File(path)
    if (inputf.getParentFile.exists()) {
      inputf.createNewFile()
    } else {
      inputf.getParentFile.mkdirs()
      inputf.createNewFile()
    }
    val out = new FileOutputStream(inputf)

    file.isInMemory() match {
      case true => {
        try {
        	log.info(s"file is in memory!")
        	out.write(file.get)
        	out.flush()
        } catch {
          case t: Throwable => t.printStackTrace()
        } finally {
          out.close()
        }
      }
      case false => {
        val in = new FileInputStream(file.getFile.getPath)
        try {
          val bytes = new Array[Byte](1024 * 1024)
          var n = 0
          while ({ n = in.read(bytes); n } != -1) {
            out.write(bytes, 0, n)
          }
          out.flush()
        } catch {
          case t: Throwable => t.printStackTrace()
        } finally {
          out.close()
          in.close()
        }
      }
    }
    path
  }
}