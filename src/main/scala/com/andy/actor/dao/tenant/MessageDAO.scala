package com.hfbank.actor.dao.tenant

import com.hfbank.actor.dao.OracleService
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantMessage
import org.apache.commons.lang.StringUtils
import com.hfbank.mode.Page


/**
 * @author win7
 */
trait MessageDAO extends OracleService {
  /**
   * 新增报文
   */
  def insertMessage(bean : TenantMessage): Int = {
    log.info(s"Start insertMessage, param is ${bean}")
    val sql : String = "insert into t_tenant_message(ID,APPID,CODE,NAME,DESCRIPTION,CREATIONTIME,CREATOR,MESSAGEPATH,INTERFACEPATH,STATUS)values(SEQ_MESSAGE.nextval,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?,?,?) "
    executeUpdate(sql, Seq(
        bean.appId,
        bean.code,
        bean.name,
        bean.description,
        HfbkUtil.getSecond(),
        bean.creator,
        bean.messagePath,
        bean.interfacePath,
        bean.status))
  }
  /**
   * 查询报文
   */
  def searchMessage(bean : TenantMessage,  page: Page): List[Map[String,String]] = {
    log.info(s"Start searchMessage")
    
    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size
    
    val selectSqlTmp = "select id, appid, appname, code, name, description, creationtime, creator, updatedtime, updater, messagepath, interfacepath, status from (select t.id, t.appid, t.appname, t.code, t.name, t.description, t.creationtime, t.creator, t.updatedtime, t.updater, t.messagepath, t.interfacepath, t.status,rownum RN from (select msg.id, msg.appid, app.appname, msg.code, msg.name, msg.description, msg.creationtime, msg.creator, msg.updatedtime, msg.updater, msg.messagepath, msg.interfacepath, msg.status from t_tenant_message msg left join t_tenant_app app on msg.appid = app.appid where 1=1 "
    val whereSqlTmp = new StringBuffer

    whereSqlTmp.append(getWhere(bean))
    whereSqlTmp.append(" order by msg.creationtime desc")
    whereSqlTmp.append(s") t where rownum <= ${end}) where RN>= ${began}")

    val sql = selectSqlTmp + whereSqlTmp
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }
  /**
   * 获取总记录数
   */
  def getTotal(bean: TenantMessage): List[Map[String, String]] = {
    log.info(s"Start getTotal")

    val selectSqlTmp = "select count(1)  total from t_tenant_message msg where 1=1 "
    val whereSqlTmp = new StringBuffer
    whereSqlTmp.append(getWhere(bean))
    val sql = selectSqlTmp + whereSqlTmp
    query(sql, Seq())
  }
  
  /**
   * 更新报文
   */
  def updateMessage(bean : TenantMessage): Int = {
    log.info(s"Start updateMessage")
    
    val sql : String = "update t_tenant_message set appid=?, code=?, name=?, description=?,updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'), updater=?, messagepath=?, interfacepath=?, status=? where id=? "
    executeUpdate(sql, Seq(
        bean.appId,
        bean.code,
        bean.name,
        bean.description,
        HfbkUtil.getSecond(),
        bean.updater,
        bean.messagePath,
        bean.interfacePath,
        bean.status,
        bean.id))
  }
  /**
   * 生产查询条件
   */
  private[this] def getWhere(tenantMessage: TenantMessage): String = {
    val whereSqlTmp = new StringBuffer

    if (StringUtils.isNotBlank(tenantMessage.appId)) whereSqlTmp.append(s" and msg.appId='${tenantMessage.appId}'")
    if (StringUtils.isNotBlank(tenantMessage.code)) whereSqlTmp.append(s" and msg.code like '%${tenantMessage.code}%'")
    if (StringUtils.isNotBlank(tenantMessage.name)) whereSqlTmp.append(s" and msg.name like '%${tenantMessage.name}%'")
    if (StringUtils.isNotBlank(tenantMessage.status)) whereSqlTmp.append(s" and msg.status='${tenantMessage.status}'")

    whereSqlTmp.toString()
  }
  
}