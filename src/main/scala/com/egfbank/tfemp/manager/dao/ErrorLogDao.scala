package com.egfbank.tfemp.manager.dao

import java.sql.ResultSet

import scala.collection.mutable.ArrayBuffer

import org.apache.commons.dbutils.ResultSetHandler

import com.egfbank.tfemp.manager.model.ErrorLog
import com.egfbank.tfemp.util.OracleDBUtil

/**
 * @author Administrator
 */
object ErrorLogDao {
  
    /**
     * @param condMap
     * @return
     */
    def queryByCond(condMap:Map[String, String]) = {
      val qr = OracleDBUtil.getOracleQueryRunner()
      val baseSql = new StringBuffer("select ID,ERROR_TYPE,UP_OR_DOWN,LOG_ID,ERROR_TIME,STATUS,TRANS_TYPE from tfemp_error_log where 1=1 ")
      
      condMap.get("errorTimeFrom") match {
        case Some(errorTimeFrom) => baseSql.append("and ERROR_TIME > '").append(errorTimeFrom).append("' ")
        case _ => 
      }
      condMap.get("errorTimeTo") match {
      case Some(errorTimeTo) => baseSql.append("and ERROR_TIME > '").append(errorTimeTo).append("' ")
      case _ => 
      }
      condMap.get("logId") match {
      case Some(logId) => baseSql.append("and LOG_ID = '").append(logId).append("' ")
      case _ => 
      }
      condMap.get("transType") match {
      case Some(transType) => baseSql.append("and trans_type = '").append(transType).append("' ")
      case _ => 
      }
      
      println(baseSql.toString())
      val list = qr.query(baseSql.toString(), new ErrorLogListHandler()).toList
      list
    }
}

class ErrorLogListHandler() extends ResultSetHandler[ArrayBuffer[ErrorLog]]{
  override def handle(rs:ResultSet):ArrayBuffer[ErrorLog] = {
    val list = ArrayBuffer[ErrorLog]()
    while (rs.next()) {
      val allLog = new ErrorLog(rs.getString("id"),rs.getString("error_type"),rs.getString("up_or_down"),rs.getString("log_id"),rs.getString("error_time"),rs.getString("status"),rs.getString("trans_type"))
      list += allLog
    }
    println(list)
    list
  }
}