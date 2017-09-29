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
@POST("/cfca/up")
class CFCAUpAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    requestMapString match {
      case paramMap: Map[String, String] => {
        log.info(s"receive paramMap,${paramMap}")
        val list = selectCFCAUp(paramMap.filter(x => !x._2.trim().equals("")))
        respondJson(AppQueryResult("0", "success", list.size, list))
      }
      //获取请求参数失败
      case _ => {
        log.info("获取请求参数失败！")
      }
    }
  }
}

@POST("/cfca/up/view")
class CFCAUpViewAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    val queryID = param("id")
    log.info(s"receive request upXMLView id,${queryID}")
    val content = selectCFCAUpView(queryID)
    content.size match {
      case 1 => respondJson(AppQueryResult("0", "success", content.size, content(0).getOrElse("XMLCONTENT", "")))
      case _ => respondJson(AppQueryResult("0", "success", 0, ""))
    }

  }
}

@POST("/cfca/up/view/result")
class CFCAUpViewResultAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    val queryID = param("id")
    log.info(s"receive request upXMLResultView id,${queryID}")
    val content = selectCFCAUpViewResult(queryID)
    content.size match {
      case 1 => respondJson(AppQueryResult("0", "success", content.size, content(0).getOrElse("RESPONSE", "")))
      case _ => respondJson(AppQueryResult("0", "success", 0, ""))
    }
  }
}