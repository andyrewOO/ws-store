package com.egfbank.tfemp.manager.action

import xitrum.SkipCsrfCheck
import xitrum.Log
import com.egfbank.tfemp.action.AppAction
import xitrum.annotation.POST
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.action.AppQueryResult

/**
 * @author andy
 */
@POST("/esb/esbXml")
class EsbXmlAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    requestMapString match {
      case paramMap: Map[String, String] => {
        log.info(s"receive paramMap,${paramMap}")
        val list = selectEsbXml(paramMap.filter(x => !x._2.trim().equals("")))
        respondJson(AppQueryResult("0", "success", list.size, list))
      }
      //获取请求参数失败
      case _ => {
        log.info("获取请求参数失败！")
      }
    }
  }
}

@POST("/esb/esbXml/view")
class EsbXmlViewAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    val queryID = param("id")
    log.info(s"receive request EsbXMLView id,${queryID}")
    val content = selectEsbXmlView(queryID)
    content.size match {
      case 1 => respondJson(AppQueryResult("0", "success", content.size, content(0).getOrElse("REQUEST", "")))
      case _ => respondJson(AppQueryResult("0", "success", 0, ""))
    }
  }
}

@POST("/esb/esbXml/view/result")
class EsbXmlViewResultAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    val queryID = param("id")
    log.info(s"receive request EsbXMLResultView id,${queryID}")
    val content = selectEsbXmlViewResult(queryID)
    content.size match {
      case 1 => respondJson(AppQueryResult("0", "success", content.size, content(0).getOrElse("RESPONSE", "")))
      case _ => respondJson(AppQueryResult("0", "success", 0, ""))
    }
  }
}