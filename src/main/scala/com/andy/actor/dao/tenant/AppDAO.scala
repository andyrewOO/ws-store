package com.hfbank.actor.dao.tenant

import com.hfbank.actor.dao.OracleService
import org.apache.commons.lang.StringUtils
import com.hfbank.mode.Page
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.util.HfbkUtil
import com.hfbank.constant.SearchOperater

/**
 * @author win7
 */
trait AppDAO extends OracleService {

  def insertInto(app: TenantAPP): Int = {
    log.info(s"Start insertInto")

    val sql = "insert into t_tenant_app(appid, userid, appname, description, creationtime, creator, status) values(?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?)"

    executeUpdate(sql, Seq(
      app.appId,
      app.userId,
      app.appName,
      app.description,
      HfbkUtil.getSecond(),
      app.creator,
      app.status))
  }

  def update(app: TenantAPP): Int = {
    log.info(s"Start update")

    val sql = "update t_tenant_app set userid=?, appname=?, description=?, updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'), updater=?, status=? where appid=?"

    executeUpdate(sql, Seq(
      app.userId,
      app.appName,
      app.description,
      HfbkUtil.getSecond(),
      app.updater,
      app.status,
      app.appId))
  }

  /**
   * 分页查询
   * @param tenantuser
   * @param page
   * @return
   */
  def search(tenantapp: TenantAPP, page: Page, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start search")

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "select appid,userid,username,appname,description,creationtime,updatedtime,creator,updater,status from (select t.appid,t.userid,t.username,t.appname,t.description,t.creationtime,t.updatedtime,t.creator,t.updater,t.status,rownum RN from (select app.appid,app.userid,u.username,app.appname,app.description,app.creationtime,app.updatedtime,app.creator,app.updater,app.status from t_tenant_app app left join t_tenant_user u on app.userid=u.userid where 1=1 "
    val whereSqlTmp = new StringBuffer

    whereSqlTmp.append(getWhere(tenantapp, operater))
    whereSqlTmp.append(" order by creationtime desc")
    whereSqlTmp.append(s") t where rownum <= ${end}) where RN>= ${began}")

    val sql = selectSqlTmp + whereSqlTmp
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }

  /**
   * 获取总记录数
   */
  def getTotal(tenantapp: TenantAPP, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start getTotal")

    val selectSqlTmp = "select count(1) total from t_tenant_app where 1=1 "
    val whereSqlTmp = new StringBuffer
    whereSqlTmp.append(getWhere(tenantapp, operater))
    val sql = selectSqlTmp + whereSqlTmp
    query(sql, Seq())
  }

  /**
   * 生产查询条件
   */
  private[this] def getWhere(tenantapp: TenantAPP, operator: String): String = {
    val whereSqlTmp = new StringBuffer

    val flag = operator match {
      case SearchOperater.FUZZY   => "%"
      case SearchOperater.PRECISE => ""
    }

    if (StringUtils.isNotBlank(tenantapp.appId)) whereSqlTmp.append(s" and appId ${operator} '${flag}${tenantapp.appId}${flag}'")
    if (StringUtils.isNotBlank(tenantapp.appName)) whereSqlTmp.append(s" and appName ${operator} '${flag}${tenantapp.appName}${flag}'")
    if (StringUtils.isNotBlank(tenantapp.status)) whereSqlTmp.append(s" and status = '${tenantapp.status}'")

    whereSqlTmp.toString()
  }
}