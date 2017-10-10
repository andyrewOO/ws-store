package com.hfbank.action.business

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.util.HfbkUtil
import com.hfbank.util.UploadFileUtil
import com.hfbank.mode.UpdateResult
import xitrum.annotation.POST
import io.netty.handler.codec.http.multipart.FileUpload

/**
 * @author win7
 */
@POST("uploadFile/:fileType")
class uploadFileAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val file = paramo[FileUpload]("file")
    log.info(s"---${file}")
    
    val commonPath = param("fileType") match {
      case "message"   => "/share/share1/ride/uploadMessage/"
      case "interface" => "/share/share1/ride/uploadInterface/"
    }

    checkFile(file) match {
      case true => {
        val path = commonPath + HfbkUtil.getTimeStampStr() + "_" + file.get.getFilename
        log.info(s"===${path}")
        val response = UploadFileUtil.uploadFile(path, file.get)
        respondJson(UpdateResult(true,response))
      }
      case false => {
        respondJson(UpdateResult(false,"文件不存在！"))
      }
    }
  }
  //判断文件是否存在
  def checkFile(file: Option[FileUpload]) = {
    val result = file.getOrElse(null) match {
      case a: FileUpload => true
      case _             => false
    }
    result
  }
}