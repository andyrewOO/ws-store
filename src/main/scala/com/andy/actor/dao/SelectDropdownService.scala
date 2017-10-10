package com.hfbank.actor.dao

/**
 * @author win7
 */
trait SelectDropdownService extends OracleService {
  /**
   * 根据传入的表名，返回所需要的字段的值
   */
  def seleceFields(tableName: String, fields: List[String], conditions: Map[String, String]) = {
    log.info(s"Start SelectDropdownService select table:${tableName}")
    var sql = "select "
    for(x <- fields){
      sql += x.toUpperCase() + ","
    }
    sql = sql.substring(0, sql.length()-1)
    sql += " from " + tableName
    
    if(null != conditions) {
      sql += " where "
      for((m,n) <- conditions) {
        sql += m + "='" + n + "' and " 
      }
      sql = sql.substring(0, sql.length()-4)
    }
    log.info(s"sql is:${sql}")
    query(sql, Seq())
  }
  
}