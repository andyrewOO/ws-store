package com.egfbank.tfemp.manager.dao

import com.egfbank.tfemp.util.OracleDBUtil
import com.egfbank.tfemp.manager.model.UpdownLog
import org.apache.commons.dbutils.ResultSetHandler
import com.egfbank.tfemp.manager.model.ListCheckLog
import scala.collection.mutable.ArrayBuffer
import java.sql.ResultSet

/**
 * @author Administrator
 */
object UpdownLogDao {

  /**
   * @param condMap
   * @return
   */
  def queryByCond(condMap: Map[String, String]) = {
    val qr = OracleDBUtil.getOracleQueryRunner()
    val baseSql = new StringBuffer("select id,up_type,up_time,up_content,down_content,up_result from tfemp_force_measure_log where 1=1 ")

    condMap.get("upTimeFrom") match {
      case Some(upTimeFrom) => baseSql.append("and up_time > '").append(upTimeFrom).append("' ")
      case _                =>
    }
    condMap.get("upTimeTo") match {
      case Some(upTimeTo) => baseSql.append("and up_time < '").append(upTimeTo).append("' ")
      case _              =>
    }

    println(baseSql.toString())
    val list = qr.query(baseSql.toString(), new UpdownLogListHandler).toList
    list
  }
}

class UpdownLogListHandler() extends ResultSetHandler[ArrayBuffer[UpdownLog]] {
  override def handle(rs: ResultSet): ArrayBuffer[UpdownLog] = {
    val list = ArrayBuffer[UpdownLog]()
    while (rs.next()) {
      val updownLog = new UpdownLog(rs.getString("id"), rs.getString("up_type"), rs.getString("up_time"), rs.getString("up_content"), rs.getString("down_content"), rs.getString("up_result"))
      list += updownLog
    }
    println(list)
    list
  }
}