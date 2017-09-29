package com.egfbank.tfemp.manager.dao

import com.egfbank.tfemp.util.OracleDBUtil
import scala.collection.mutable.ArrayBuffer
import java.sql.ResultSet
import com.egfbank.tfemp.manager.model.DoubtAccount
import org.apache.commons.dbutils.ResultSetHandler

/**
 * @author Administrator
 */
object DoubtAccountDao {
  
    /**
     * @param condMap
     * @return
     */
    def queryByCond(condMap:Map[String, String]) = {
      val qr = OracleDBUtil.getOracleQueryRunner()
      val baseSql = new StringBuffer("select ID,DOUBT_TYPE,ACCOUNT,ACCOUNT_NAME,CERT_TYPE,CERT_NO,UP_TIME from TFEMP_DOUBT_ACCOUNT where 1=1 ")
      
      condMap.get("doubtType") match {
        case Some(doubtType) => baseSql.append("and doubt_type = '").append(doubtType).append("' ")
        case _ => 
      }
      condMap.get("account") match {
      case Some(account) => baseSql.append("and account = '").append(account).append("' ")
      case _ => 
      }
      
      println(baseSql.toString())
      val list = qr.query(baseSql.toString(), new DoubtAccountListHandler).toList
      list
    }
}

class DoubtAccountListHandler() extends ResultSetHandler[ArrayBuffer[DoubtAccount]]{
  override def handle(rs:ResultSet):ArrayBuffer[DoubtAccount] = {
    val list = ArrayBuffer[DoubtAccount]()
    while (rs.next()) {
      val doubtAccount = new DoubtAccount(rs.getString("id"),rs.getString("doubt_type"),rs.getString("account"),rs.getString("account_name"),rs.getString("cert_type"),rs.getString("cert_no"),rs.getString("up_time"))
      list += doubtAccount
    }
    println(list)
    list
  }
}