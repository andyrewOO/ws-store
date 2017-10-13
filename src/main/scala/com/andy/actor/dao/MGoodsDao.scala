package com.andy.actor.dao

import com.andy.mode.bean.MGoods
import com.andy.mode.Page
import java.sql.Connection
import com.andy.constant.TableName
import org.apache.commons.lang3.StringUtils
import com.andy.constant.SearchOperater

/**
 * @author andy
 */
trait MGoodsDao extends MySqlService {

  /**
   *  查询
   */
  def search(mgoods: MGoods, page: Page)(implicit connect: Connection): List[Map[String, String]] = {

    // 分页参数
    val index = page.start
    val size = page.size
    val begin = (index - 1) * size
    val end = index * size

    val selectSqlTmp = s"select id, kind, lprice, sprice, place, describtion, ctime, dtime, total, remark, status from ${TableName.MGOODS} where 1=1"
    val whereSql = new StringBuffer

    whereSql.append(getWhere(mgoods))
    whereSql.append(s" ORDER BY ctime DESC")
    whereSql.append(s" limit ${begin},${end}")

    val sql = selectSqlTmp + whereSql

    log.info(s"sql:${sql}")

    query(sql, Seq())
  }

  /**
   * 获取记录总数
   */
  def getTotal(mgoods: MGoods) = {
    log.info(s"Start getTotal")

    val selectSqlTmp = s"select count(id) total from ${TableName.MGOODS} where 1=1"
    val whereSql = new StringBuffer
    whereSql.append(getWhere(mgoods))

    val sql = selectSqlTmp + whereSql

    log.info(s"Get Total:${sql}")
    
    query(sql, Seq())
  }

  private[this] def getWhere(mgoods: MGoods, operator: String = SearchOperater.PRECISE): String = {

    val whereSqlTmp = new StringBuffer

    if (StringUtils.isNotBlank(mgoods.description)) whereSqlTmp.append(s" and describtion like '%${mgoods.description}%'")
    if (StringUtils.isNotBlank(mgoods.place)) whereSqlTmp.append(s" and place like '%${mgoods.place}%'")
    if (StringUtils.isNotBlank(mgoods.kind)) whereSqlTmp.append(s" and kind = '${mgoods.kind}'")
    if (StringUtils.isNotBlank(mgoods.status)) whereSqlTmp.append(s" and status = '${mgoods.status}'")

    whereSqlTmp.toString()
  }

  /**
   * 新增
   */
  def insert(mgoods: MGoods)(implicit connect: Connection): Int = {

    val sql = ""

    executeUpdate(sql, Seq())
  }

  /**
   * 修改
   */
  def update(mgoods: MGoods)(implicit connect: Connection): Int = {

    val sql = ""

    executeUpdate(sql, Seq())
  }
}