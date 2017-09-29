package com.egfbank.tfemp.manager.action

import xitrum.SkipCsrfCheck
import xitrum.Log
import com.egfbank.tfemp.action.AppAction
import xitrum.annotation.POST
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.action.AppQueryResult
import xitrum.annotation.GET

/**
 * @author andy
 */
@POST("/cfca/caseReport")
class CaseReportAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    requestMapString match {
      case paramMap: Map[String, String] => {
        log.info(s"receive paramMap,${paramMap}")
        val map = paramMap.filterNot { kv => kv._2.trim().equals("") }
        val list = selectCaseReport(map)
        respondJson(AppQueryResult("0", "success", list.size, list))
      }
      //获取请求参数失败
      case _ => {
        log.info("获取请求参数失败！")
      }
    }
  }
}

@POST("/cfca/caseReport/view")
class CaseReportViewAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    val queryID = param("id")
    log.info(s"receive request caseReport xml id,${queryID}")
    val content = selectCaseReportView(queryID)
    content.size match {
      case 1 => respondJson(AppQueryResult("0", "success", content.size, content(0).getOrElse("XMLCONTENT", "")))
      case _ => respondJson(AppQueryResult("0", "success", 0, ""))
    }
  }
}