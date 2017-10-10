package com.hfbank.actor.dao.tenant

import com.hfbank.actor.dao.OracleService
import com.hfbank.mode.bean.TenantInterface
import com.hfbank.mode.Page
import org.apache.commons.lang.StringUtils
import com.hfbank.util.HfbkUtil


/**
 * @author win7
 */
trait InterfaceDAO extends OracleService {
  /**
   * 新增接口
   */
  def insertInterface(bean: TenantInterface): Int = {
    log.info(s"Start insertInterface")
    val sql: String = "insert into t_tenant_interface(id, appid, code, name, description, url, creationtime, creator, method, request, response, responseexample, timeout, retrytimes, interval_, status)values(SEQ_INTERFACE.nextval,?,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?,?,?,?,?,?,?,?)"
    executeUpdate(sql, Seq(
        bean.appId,
        bean.code,
        bean.name,
        bean.description,
        bean.url,
        HfbkUtil.getSecond(),
        bean.creator,
        bean.method,
        bean.request,
        bean.response,
        bean.responseExample,
        bean.timeOut,
        bean.retryTimes,
        bean.interval_,
        bean.status))
  }
  /**
   * 查询接口
   */
  def searchInterface(bean: TenantInterface, page: Page): List[Map[String, String]] = {
    log.info(s"Start searchInterface")
    
    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size
    
    val selectSqlTmp = "select id, appid, appname, code, name, description, url, creationtime, creator, updatedtime, updater, method, request, response, responseexample, timeout, retrytimes, interval_, status from (select t.id, t.appid, t.appname, t.code, t.name, t.description, t.url, t.creationtime, t.creator, t.updatedtime, t.updater, t.method, t.request, t.response, t.responseexample, t.timeout, t.retrytimes, t.interval_, t.status, rownum RN from (select inter.id, inter.appid, app.appname, inter.code, inter.name, inter.description, inter.url, inter.creationtime, inter.creator, inter.updatedtime, inter.updater, inter.method, inter.request, inter.response, inter.responseexample, inter.timeout, inter.retrytimes, inter.interval_, inter.status from t_tenant_interface inter left join t_tenant_app app on inter.appid = app.appid where 1=1 "
    val whereSqlTmp = new StringBuffer

    whereSqlTmp.append(getWhere(bean))
    whereSqlTmp.append(" order by inter.creationtime desc")
    whereSqlTmp.append(s") t where rownum <= ${end}) where RN>= ${began}")

    val sql = selectSqlTmp + whereSqlTmp
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }
  /**
   * 获取总记录数
   */
  def getTotal(bean: TenantInterface): List[Map[String, String]] = {
    log.info(s"Start getTotal")

    val selectSqlTmp = "select count(1)  total from t_tenant_interface inter where 1=1 "
    val whereSqlTmp = new StringBuffer
    whereSqlTmp.append(getWhere(bean))
    val sql = selectSqlTmp + whereSqlTmp
    query(sql, Seq())
  }
  /**
   * 更新接口
   */
  def updateInterface(bean: TenantInterface): Int = {
    log.info(s"Start updateInterface")
    
    val sql: String = "update t_tenant_interface set appid=?, code=?, name=?, description=?, url=?, updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'), updater=?, method=?, request=?, response=?, responseexample=?, timeout=?, retrytimes=?, interval_=?, status=? where id=? "
    executeUpdate(sql, Seq(
        bean.appId,
        bean.code,
        bean.name,
        bean.description,
        bean.url,
        HfbkUtil.getSecond(),
        bean.updater,
        bean.method,
        bean.request,
        bean.response,
        bean.responseExample,
        bean.timeOut,
        bean.retryTimes,
        bean.interval_,
        bean.status,
        bean.id))
  }
  /**
   * 生产查询条件
   */
  private[this] def getWhere(tenantInterface: TenantInterface): String = {
    val whereSqlTmp = new StringBuffer

    if (StringUtils.isNotBlank(tenantInterface.appId)) whereSqlTmp.append(s" and inter.appId='${tenantInterface.appId}'")
    if (StringUtils.isNotBlank(tenantInterface.code)) whereSqlTmp.append(s" and inter.code like '%${tenantInterface.code}%'")
    if (StringUtils.isNotBlank(tenantInterface.name)) whereSqlTmp.append(s" and inter.name like '%${tenantInterface.name}%'")
    if (StringUtils.isNotBlank(tenantInterface.status)) whereSqlTmp.append(s" and inter.status='${tenantInterface.status}'")

    whereSqlTmp.toString()
  }
}