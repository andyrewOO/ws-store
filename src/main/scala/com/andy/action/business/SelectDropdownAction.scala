package com.hfbank.action.business

import xitrum.annotation.POST
import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.dao.SelectDropdownService
import com.hfbank.mode.AppQueryResult
import java.sql.Timestamp
import io.netty.handler.codec.http.multipart.FileUpload
import com.hfbank.util.HfbkUtil
import com.hfbank.util.UploadFileUtil
import com.hfbank.mode.UpdateResult
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JObject
import org.json4s.jackson.JsonMethods

/**
 * @author win7
 */
@POST("select/dropdown")
class SelectDropdownAction extends BaseAppAction with SkipCsrfCheck with SelectDropdownService {
  def execute(): Unit = {
    log.info(s"Start SelectDropdownAction")

    val tableName = paramo("tablename")
    val fields = param("fields").split(",").toList
    //加入条件限定
    val conditionMap = paramo("conditions") match {
      case Some(x) => {
        val json = parse(x)
        implicit val formats = DefaultFormats
        json.extract[Map[String, String]]
      }
      case _       => null
    }

    tableName match {
      case Some(t) => {
        val resultFields = seleceFields(t, fields, conditionMap)
        respondJson(AppQueryResult(true, "", resultFields.size, resultFields))
      }
      case _ => {
        respondJson(AppQueryResult(false, "无有效数据", 0, null))
      }
    }
  }
}