package com.egfbank.tfemp.manager.action

import xitrum.SkipCsrfCheck
import xitrum.Log
import com.egfbank.tfemp.action.AppAction
import xitrum.annotation.POST
import com.egfbank.tfemp.actor.services.CFCADbService
import com.egfbank.tfemp.action.AppQueryResult

/**
 * @author ly
 */
@POST("/cfca/force2")
class ForceAction2 extends AppAction with SkipCsrfCheck with CFCADbService with Log {
  def execute = {
    requestMapString match {
      case paramMap: Map[String, String] => {
        log.info(s"receive paramMap,${paramMap}")
        val list = selectForce2(paramMap.filter(x => !x._2.trim().equals("")))
        log.info(s"query forceMeasure,size:${list.size}")
        val resList = list.map { fileMap =>
          val fileName = fileMap("FILENAME")
          val fileType = getFileType(fileName)
          val newFileMap = fileMap.+("fileType" -> fileType)
          newFileMap
        }
        respondJson(AppQueryResult("0", "success", resList.size, resList))
        //        respondJson(AppQueryResult("0", "success", list.size, list))
      }
      //获取请求参数失败
      case _ => {
        log.info("获取请求参数失败！")

      }
    }
  }

  /**
   * @param fileName
   * @return
   */
  def getFileType(fileName: String) = {
    val fn = fileName
    fn.substring(0, 2) match {
      case "01" => "警官身份证件"
      case "02" => "冻结法律文书"
      case "03" => "紧急止付申请表"
      case "04" => "身份证"
      case _    => ""
    }
  }
}