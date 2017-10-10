package com.hfbank.actor.dao.tenant

import org.apache.commons.lang3.StringUtils

import com.hfbank.actor.dao.OracleService
import com.hfbank.constant.SearchOperater
import com.hfbank.mode.Page
import com.hfbank.mode.bean.TenantUser
import com.hfbank.util.HfbkUtil

/**
 * @author andy
 */
trait TenantUserDAO extends OracleService {

  /**
   * 新增
   * @param user
   * @return
   */
  def insertInto(user: TenantUser): Int = {
    log.info(s"Start insertInto")

    val sql = "insert into t_tenant_user(userid, username, password, phone, mail, creationtime, creator, status, remark) values(?,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?,?)"

    executeUpdate(sql, Seq(
      user.userId,
      user.userName,
      user.password,
      user.phone,
      user.mail,
      HfbkUtil.getSecond(),
      user.creator,
      user.status,
      user.remark))
  }

  /**
   *
   * 分页查询
   * @param tenantuser
   * @param page
   * @return
   */
  def search(tenantuser: TenantUser, page: Page = Page(1, 10, -1, null), operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start search")

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "select userid, username, password,phone,mail, creationtime,updatedtime,creator,updater,status,remark from (select t.userid, t.username, t.password, t.phone, t.mail, t.creationtime, t.updatedtime, t.creator, t.updater, t.status, t.remark, rownum RN from (select userid, username, password,phone,mail, creationtime,updatedtime,creator,updater,status,remark from t_tenant_user where 1=1 "
    val whereSqlTmp = new StringBuffer

    whereSqlTmp.append(getWhere(tenantuser, operater))
    whereSqlTmp.append(" order by creationtime desc")
    whereSqlTmp.append(s") t where rownum <= ${end}) where RN>= ${began}")

    val sql = selectSqlTmp + whereSqlTmp
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }

  /**
   * 获取总记录数
   */
  def getTotal(tenantuser: TenantUser, operater: String = SearchOperater.PRECISE): List[Map[String, String]] = {
    log.info(s"Start getTotal")

    val selectSqlTmp = "select count(1)  total from t_tenant_user where 1=1 "
    val whereSqlTmp = new StringBuffer
    whereSqlTmp.append(getWhere(tenantuser, operater))
    val sql = selectSqlTmp + whereSqlTmp
    query(sql, Seq())
  }

  def update(user: TenantUser): Int = {
    log.info(s"Start update")

    val sql = "update t_tenant_user set username=?, password=?, phone=?, mail=?,updatedtime=to_date(?,'yyyy-MM-dd HH24:mi:ss'),updater=?,remark=?,status=? where userid=?"

    executeUpdate(sql, Seq(
      user.userName,
      user.password,
      user.phone,
      user.mail,
      HfbkUtil.getSecond(),
      user.updater,
      user.remark,
      user.status,
      user.userId))
  }

  /**
   * 生产查询条件
   */
  private[this] def getWhere(tenantuser: TenantUser, operator: String): String = {
    val whereSqlTmp = new StringBuffer

    val flag = operator match {
      case SearchOperater.FUZZY   => "%"
      case SearchOperater.PRECISE => ""
    }

    if (StringUtils.isNotBlank(tenantuser.userId)) whereSqlTmp.append(s" and userid ${operator} '${flag}${tenantuser.userId}${flag}'")
    if (StringUtils.isNotBlank(tenantuser.userName)) whereSqlTmp.append(s" and userName ${operator} '${flag}${tenantuser.userName}${flag}'")
    if (StringUtils.isNotBlank(tenantuser.password)) whereSqlTmp.append(s" and password ${operator} '${tenantuser.password}${flag}'")
    if (StringUtils.isNotBlank(tenantuser.phone)) whereSqlTmp.append(s" and phone ${operator} '${flag}${tenantuser.phone}${flag}'")
    if (StringUtils.isNotBlank(tenantuser.mail)) whereSqlTmp.append(s" and mail ${operator} '${flag}${tenantuser.mail}${flag}'")
    if (StringUtils.isNotBlank(tenantuser.status)) whereSqlTmp.append(s" and status = '${tenantuser.status}'")

    whereSqlTmp.toString()
  }
}