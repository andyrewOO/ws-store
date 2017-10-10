package com.hfbank.actor.dao

/**
 * @author win7
 */
trait VeirfyIDService extends OracleService {
  /**
   * 根据传入的表名，以及查询的主键值，查询是否已经存在
   */
  def selectID(tableName: String, field: String, value: String) = {
    log.info(s"Start selectID, table:${tableName},field:${field},value:${value}")
    val sql = s"select count(1) sizeof from ${tableName} where ${field}='${value}'"
    log.info(s"sql: ${sql}")
    query(sql, Seq())
  }
}