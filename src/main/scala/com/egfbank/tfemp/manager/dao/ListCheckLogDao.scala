package com.egfbank.tfemp.manager.dao

import java.sql.ResultSet

import scala.collection.mutable.ArrayBuffer

import org.apache.commons.dbutils.ResultSetHandler

import com.egfbank.tfemp.manager.model.ListCheckLog
import com.egfbank.tfemp.util.OracleDBUtil

/**
 * @author Administrator
 */
object ListCheckLogDao {
  
    /**
     * 根据条件查询listchecklog
     * @param appName
     * @param searchTimeFrom
     * @param searchTimeTo
     * @return
     */
    def queryByCond(condMap:Map[String, String]) = {
      val qr = OracleDBUtil.getOracleQueryRunner()
      val baseSql = new StringBuffer("select id,search_time,app_name,account_num,account_name,names,cert_type,cert_no,status,descs from tfemp_acc_check_log where 1=1 ")
      
      condMap.get("appName") match {
        case Some(appName) => baseSql.append("and app_name='").append(appName).append("' ")
        case _ => 
      }
      
      condMap.get("searchTimeFrom") match {
        case Some(searchTimeFrom) => baseSql.append("and search_time > '").append(searchTimeFrom).append("' ")
        case _ => 
      }
      
      condMap.get("searchTimeTo") match {
      case Some(searchTimeTo) => baseSql.append("and search_time < '").append(searchTimeTo).append("' ")
      case _ => 
      }
      
      println(baseSql.toString())
      val list = qr.query(baseSql.toString(), new ListCheckLogListHandler).toList
      list
    }
}

class ListCheckLogHandler() extends ResultSetHandler[ListCheckLog]{
  override def handle(rs:ResultSet):ListCheckLog = {
    if (rs.next()) {
      val listCheckLog = new ListCheckLog(rs.getString("id"),rs.getString("search_time"),rs.getString("app_name"),rs.getString("account_num"),rs.getString("account_name"),rs.getString("names"),rs.getString("cert_type"),rs.getString("cert_no"),rs.getString("status"),rs.getString("descs"))
      listCheckLog
    }else {
      null
    }
  }
}

class ListCheckLogListHandler() extends ResultSetHandler[ArrayBuffer[ListCheckLog]]{
  
  override def handle(rs:ResultSet):ArrayBuffer[ListCheckLog] = {
    val list = ArrayBuffer[ListCheckLog]()
    
    while (rs.next()) {
      val listCheckLog = new ListCheckLog(rs.getString("id"),rs.getString("search_time"),rs.getString("app_name"),rs.getString("account_num"),rs.getString("account_name"),rs.getString("names"),rs.getString("cert_type"),rs.getString("cert_no"),rs.getString("status"),rs.getString("descs"))
      list += listCheckLog
    }
    println(list)
    list
  }
}
