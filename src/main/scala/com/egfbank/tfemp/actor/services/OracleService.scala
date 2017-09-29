package com.egfbank.tfemp.actor.services

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Connection
import com.egfbank.tfemp.util.OracleDBUtil
import ch.qos.logback.core.db.dialect.DBUtil
import xitrum.Log

/**
 * @author huxp
 */
trait OracleService extends Log {

  /**
   * 查询SQL
   */
  def query(sql: String, v: Seq[String]): List[Map[String, String]] = {
    var conn: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null
    var list: scala.collection.mutable.ArrayBuffer[Map[String, String]] = scala.collection.mutable.ArrayBuffer[Map[String, String]]()
    try {
      conn = OracleDBUtil.getConn
      ps = conn.prepareStatement(sql)
      var i: Int = 1
      if (v != null) v.foreach { x => { ps.setString(i, x); i = i + 1 } }
      rs = ps.executeQuery()
      while (rs.next()) {
        val dm = scala.collection.mutable.Map[String, String]()
        for (i <- 1 to rs.getMetaData.getColumnCount) {
          dm.put(rs.getMetaData.getColumnName(i), rs.getString(i))
        }
        list += dm.toMap
      }
    } catch {
      case ex: Exception => log.error(ex.getMessage, ex)
    } finally {
      OracleDBUtil.close(rs, ps, conn)
    }
    list.toList
  }

  /**
   * 执行更新或写入
   */
  def executeUpdate(sql: String, v: Seq[Object]): Int = {
    var conn: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null
    var rtn = 0
    try {
      conn = OracleDBUtil.getConn
      ps = conn.prepareStatement(sql)
      var i: Int = 1
      if (v != null) v.foreach { x => { ps.setObject(i, x); i = i + 1 } }
      rtn = ps.executeUpdate()
    } catch {
      case ex: Exception => log.error(ex.getMessage, ex)
    } finally {
      OracleDBUtil.close(rs, ps, conn)
    }
    rtn
  }

}