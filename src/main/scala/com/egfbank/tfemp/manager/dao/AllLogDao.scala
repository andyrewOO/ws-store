package com.egfbank.tfemp.manager.dao

import com.egfbank.tfemp.manager.model.UpdownLog
import org.apache.commons.dbutils.ResultSetHandler
import com.egfbank.tfemp.manager.model.ListCheckLog
import scala.collection.mutable.ArrayBuffer
import java.sql.ResultSet
import com.egfbank.tfemp.manager.model.AllLog
import com.egfbank.tfemp.util.OracleDBUtil

/**
 * @author Administrator
 */
object AllLogDao {

  /**
   * @param condMap
   * @return
   */
  def queryByCond(condMap: Map[String, String]) = {
    val qr = OracleDBUtil.getOracleQueryRunner()
    val baseSql = new StringBuffer("select id,text from tfemp_all_log where 1=1 ")

    condMap.get("id") match {
      case Some(id) => baseSql.append("and id = '").append(id).append("' ")
      case _        =>
    }

    println(baseSql.toString())
    val list = qr.query(baseSql.toString(), new AllLogListHandler).toList
    list
  }
}

class AllLogListHandler() extends ResultSetHandler[ArrayBuffer[AllLog]] {
  override def handle(rs: ResultSet): ArrayBuffer[AllLog] = {
    val list = ArrayBuffer[AllLog]()
    while (rs.next()) {
      val allLog = new AllLog(rs.getString("id"), rs.getString("text"))
      list += allLog
    }
    println(list)
    list
  }
}