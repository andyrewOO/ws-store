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
@POST("/cfca/dynamicquery")
class DynamicQueryAction extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    requestMapString match {
      case paramMap: Map[String, String] => {
        log.info(s"receive paramMap,${paramMap}")
        val map = paramMap.filterNot { kv => kv._2.trim().equals("") }
        val list = selectDynamicQuery(map)
        respondJson(AppQueryResult("0", "success", list.size, list))
      }
      //获取请求参数失败
      case _ => {
        log.info("获取请求参数失败！")
      }
    }
  }
}