package com.hfbank.actor.dao

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Connection
import xitrum.Log
import com.hfbank.util.OracleDBUtil
import java.sql.SQLException

/**
 * @author andy
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
  def executeUpdate(sql: String, v: Seq[Object], autoCommit: Boolean = true): Int = {
    var conn: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null
    var rtn = 0
    try {
      conn = OracleDBUtil.getConn
      conn.setAutoCommit(autoCommit)
      ps = conn.prepareStatement(sql)
      var i: Int = 1
      if (v != null) v.foreach { x => { ps.setObject(i, x); i = i + 1 } }
      rtn = ps.executeUpdate()
    } catch {
      case ex: SQLException => {
        log.error(ex.getMessage, ex)
        ex.getSQLState match {
          case "23000" =>
            //唯一约束ERROR会抛出自定义异常，各业务模块自行捕获处理报错message
            throw new RepetitionException(v)
          case x =>
        }
      }
      case ex: Exception => log.error(ex.getMessage, ex)
    } finally {
      OracleDBUtil.close(rs, ps, conn)
    }
    rtn
  }

  /**
   * 执行更新或写入(支持多语句 Sql 事务)
   */
  def executeUpdate(sqls: Tuple2[String, Seq[Object]]*) = {
    var conn: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null
    var rtn = 0
    var rtnT = Seq[Object]() //此变量用于存放报错对象
    try {
      conn = OracleDBUtil.getConn
      conn.setAutoCommit(false)
      // 执行每一个Sql
      sqls.map { sql =>
        rtnT = sql._2 //先赋值，后执行
        ps = conn.prepareStatement(sql._1)
        var i: Int = 1
        if (sql._2 != null) sql._2.foreach { x => { ps.setObject(i, x); i = i + 1 } }
        rtn = ps.executeUpdate()
      }
      conn.commit()
    } catch {
      case ex: SQLException => {
        log.error(ex.getMessage, ex)
         conn.rollback()
        ex.getSQLState match {
          case "23000" =>
            //唯一约束ERROR会抛出自定义异常，各业务模块自行捕获处理报错message
            throw new RepetitionException(rtnT)
          case x =>
        }
      }
      case ex: Exception => {
        log.error(ex.getMessage, ex)
        conn.rollback()
      }
    } finally {
      OracleDBUtil.close(rs, ps, conn)
    }
    rtn
  }
  case class RepetitionException(returnT: Seq[Object]) extends Throwable
}