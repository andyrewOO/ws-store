package com.hfbank.actor.dao.tenant

import com.hfbank.actor.dao.OracleService
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.Page
import org.apache.commons.lang.StringUtils
import com.hfbank.util.HfbkUtil
import com.hfbank.constant.SearchOperater
import com.hfbank.mode.bean.TenantSourceV
import com.hfbank.constant.SourceType
import com.hfbank.constant.BeanStatus

/**
 * @author win7
 */
trait SourceDAO extends OracleService {
  /**
   * 新增资源分配
   */
  def insertSource(list: List[TenantSource]): Int = {
    log.info(s"Start insertSource")

    val tupleList = for (item <- list) yield {
      val sql: String = "insert into t_tenant_source(id, appid, sourceid, type_, description, creationtime, creator, status)values(SEQ_SOURCE.nextval,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?)"
      (sql, Seq(
        item.appId,
        item.sourceId,
        item.type_,
        item.description,
        HfbkUtil.getSecond(),
        item.creator,
        "1"))
    }
    //更新资源的状态
    val updateList = for (item <- list) yield {
      item.type_ match {
        case SourceType.ENGINEE => {
          val sql: String = "update t_source_engine t set t.status=? where t.id=? "
          (sql, Seq(BeanStatus.USED, item.sourceId))
        }
        case SourceType.DB => {
          val sql: String = "update t_source_db t set t.status=? where t.id=? "
          (sql, Seq(BeanStatus.USED, item.sourceId))
        }
        case SourceType.QUEUE => {
          val sql: String = "update t_source_queue t set t.status=? where t.id=? "
          (sql, Seq(BeanStatus.USED, item.sourceId))
        }
      }
    }

    val sqls = tupleList ++ updateList
    executeUpdate(sqls: _*)
  }

  /**
   * 查询资源分配
   */
  def searchV(bean: TenantSourceV, page: Page, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start searchSource")

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "select id, sourceid, name, appid, appname, userid, username, type_, description, creationtime, creator, updatedtime, updater, status from (select t.id, t.sourceid, t.name, t.appid, t.appname, t.userid, t.username, t.type_, t.description, t.creationtime, t.creator, t.updatedtime, t.updater, t.status, rownum RN from (select id, sourceid, name, appid, appname, userid, username, type_, description, creationtime, creator, updatedtime, updater, status from v_tenant_source s where 1=1 "
    val whereSqlTmp = new StringBuffer

    whereSqlTmp.append(getWhere(bean, operater))
    whereSqlTmp.append(" order by s.creationtime desc")
    whereSqlTmp.append(s") t where rownum <= ${end}) where RN>= ${began}")

    val sql = selectSqlTmp + whereSqlTmp
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }

  /**
   * 获取总记录数
   */
  def getTotal(bean: TenantSourceV, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start getTotal")

    val selectSqlTmp = "select count(1)  total from v_tenant_source s where 1=1 "
    val whereSqlTmp = new StringBuffer
    whereSqlTmp.append(getWhere(bean, operater))
    val sql = selectSqlTmp + whereSqlTmp
    query(sql, Seq())
  }

  /**
   * 更新资源分配
   */
  def updateSource(bean: TenantSource): Int = {
    log.info(s"Start updateSource")

    val sql: String = "update t_tenant_source set appid=?, sourceid=?, type_=?, description=?, updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'), updater=?, status=? where id=?  "
    executeUpdate(sql, Seq(
      bean.appId,
      bean.sourceId,
      bean.type_,
      bean.description,
      HfbkUtil.getSecond(),
      bean.updater,
      bean.status,
      bean.id))
  }
  /**
   * 生产查询条件
   */
  private[this] def getWhere(tenantSourceV: TenantSourceV, operator: String): String = {
    val whereSqlTmp = new StringBuffer

    val flag = operator match {
      case SearchOperater.FUZZY   => "%"
      case SearchOperater.PRECISE => ""
    }

    if (StringUtils.isNotBlank(tenantSourceV.appId)) whereSqlTmp.append(s" and s.appId = '${tenantSourceV.appId}'")
    if (StringUtils.isNotBlank(tenantSourceV.type_)) whereSqlTmp.append(s" and s.type_ = '${tenantSourceV.type_}'")
    if (StringUtils.isNotBlank(tenantSourceV.name)) whereSqlTmp.append(s" and s.name ${operator} '${flag}${tenantSourceV.name}${flag}'")
    if (StringUtils.isNotBlank(tenantSourceV.status)) whereSqlTmp.append(s" and s.status = '${tenantSourceV.status}'")

    whereSqlTmp.toString()
  }

  /**
   * 检索资源表
   */
  def searchSource(tenantsource: TenantSource) = {
    log.info(s"Start searchSource")

    val sqlTmp = "select id, appid, sourceid, type_, description, creationtime, creator, updatedtime, updater, status from t_tenant_source where 1=1 "
    val whereSqlTmp = new StringBuffer

    if (StringUtils.isNotBlank(tenantsource.id)) whereSqlTmp.append(s" and id='${tenantsource.id}'")
    if (StringUtils.isNotBlank(tenantsource.appId)) whereSqlTmp.append(s" and appid='${tenantsource.appId}'")
    if (StringUtils.isNotBlank(tenantsource.sourceId)) whereSqlTmp.append(s" and sourceId='${tenantsource.sourceId}'")
    if (StringUtils.isNotBlank(tenantsource.status)) whereSqlTmp.append(s" and status='${tenantsource.status}'")

    val sql = sqlTmp + whereSqlTmp

    log.info(s"sql:${sql}")
    query(sql, Seq())
  }

  /**
   * 更新Source分配表
   */
  def sourceUpdate(update: List[TenantSource], add: List[TenantSource]) = {
    log.info(s"Start searchUpdate")

    // 更新动作
    val updateItem = for (item <- update) yield {
      val sqlUpdate: String = "update %s set updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'), updater=?, status=? where id=?  "
      val sqlDelete: String = "delete from t_tenant_source where id=?"

      val sourceTableName = item.type_ match {
        case SourceType.ENGINEE => "t_source_engine"
        case SourceType.DB      => "t_source_db"
        case SourceType.QUEUE   => "t_source_queue"
      }

      // 对分配表的更新动作
      val updateSourceItem = (sqlDelete, Seq(item.id))
      // 对各资源的更新动作
      val releaseSourceItem = (sqlUpdate.format(sourceTableName), Seq(HfbkUtil.getSecond(), item.updater, BeanStatus.INI, item.sourceId))

      (updateSourceItem, releaseSourceItem)
    }

    // 新增动作
    val addItem = for (item <- add) yield {
      // 分配表的新增动作
      val sqlForTS: String = "insert into t_tenant_source(id, appid, sourceid, type_, description, creationtime, creator,updatedtime, updater, status)values(SEQ_SOURCE.nextval,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?)"
      val addSourceItem = (sqlForTS, Seq(item.appId, item.sourceId, item.type_, item.description, HfbkUtil.getSecond(), item.creator, HfbkUtil.getSecond(), item.updater, "1"))

      // 各资源表的状态更新动作
      val tableName = item.type_ match {
        case SourceType.ENGINEE => "t_source_engine"
        case SourceType.DB      => "t_source_db"
        case SourceType.QUEUE   => "t_source_queue"
      }
      val sqlForSS: String = s"update ${tableName} set updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'), updater=?, status=? where id=? "
      val usedSourceItem = (sqlForSS, Seq(HfbkUtil.getSecond(), item.updater, BeanStatus.USED, item.sourceId))

      (addSourceItem, usedSourceItem)
    }

    val sqlsItem = updateItem ++ addItem

    // 将元组打平
    val sqls = sqlsItem.flatMap { item =>
      List(item._1, item._2)
    }

    executeUpdate(sqls: _*)
  }
}