package com.hfbank.actor.dao.tenant

import com.hfbank.actor.dao.OracleService
import com.hfbank.mode.bean.TenantPartion
import org.apache.commons.lang.StringUtils
import com.hfbank.mode.Page
import com.hfbank.util.HfbkUtil
import com.hfbank.constant.SearchOperater

/**
 * @author win7
 */
trait PartionDAO extends OracleService {

  def insertInto(partion: TenantPartion): Int = {
    log.info(s"Start insertInto")

    val sql = "insert into t_tenant_partion(partid, appid, partname, description, creationtime, creator, status) values(?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?)"

    executeUpdate(sql, Seq(
      partion.partId,
      partion.appId,
      partion.partName,
      partion.description,
      HfbkUtil.getSecond(),
      partion.creator,
      partion.status))
  }

  def update(partion: TenantPartion): Int = {
    log.info(s"Start update")

    val sql = "update t_tenant_partion set partid=?, appid=?, partname=?, description=?, updatedtime=to_date(?, 'yyyy-MM-dd HH24:mi:ss'), updater=?, status=? where partid=?"

    executeUpdate(sql, Seq(
      partion.partId,
      partion.appId,
      partion.partName,
      partion.description,
      HfbkUtil.getSecond(),
      partion.updater,
      partion.status,
      partion.partId))
  }

  /**
   * 分页查询
   * @param tenantuser
   * @param page
   * @return
   */
  def search(partion: TenantPartion, page: Page, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "select partid, appid, appname, partname, description, creationtime, creator, updatedtime, updater, status from (select t.partid, t.appid, t.appname, t.partname, t.description, t.creationtime, t.creator, t.updatedtime, t.updater, t.status,rownum RN from (select partid, part.appid, app.appname, part.partname, part.description, part.creationtime, part.creator, part.updatedtime, part.updater, part.status from t_tenant_partion part left join t_tenant_app app on part.appid = app.appid where 1=1 "
    val whereSqlTmp = new StringBuffer

    whereSqlTmp.append(getWhere(partion, operater))
    whereSqlTmp.append(" order by part.creationtime desc")
    whereSqlTmp.append(s") t where rownum <= ${end}) where RN>= ${began}")

    val sql = selectSqlTmp + whereSqlTmp
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }

  /**
   * 生产查询条件
   */
  private[this] def getWhere(partion: TenantPartion, operator: String): String = {
    val whereSqlTmp = new StringBuffer

    val flag = operator match {
      case SearchOperater.FUZZY   => "%"
      case SearchOperater.PRECISE => ""
    }

    if (StringUtils.isNotBlank(partion.appId)) whereSqlTmp.append(s" and part.appId = '${partion.appId}'")
    if (StringUtils.isNotBlank(partion.partId)) whereSqlTmp.append(s" and part.partId ${operator} '${flag}${partion.partId}${flag}'")
    if (StringUtils.isNotBlank(partion.partName)) whereSqlTmp.append(s" and part.partName ${operator} '${flag}${partion.partName}${flag}'")
    if (StringUtils.isNotBlank(partion.status)) whereSqlTmp.append(s" and part.status = '${partion.status}'")

    whereSqlTmp.toString()
  }

  /**
   * 获取总记录数
   */
  def getTotal(partion: TenantPartion, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start getTotal")

    val selectSqlTmp = "select count(1) total from t_tenant_partion part where 1=1 "
    val whereSqlTmp = new StringBuffer
    whereSqlTmp.append(getWhere(partion, operater))
    val sql = selectSqlTmp + whereSqlTmp
    query(sql, Seq())
  }
}