package com.hfbank.actor.dao.source

import com.hfbank.actor.dao.OracleService
import com.hfbank.mode.bean.SourceEngine
import com.hfbank.mode.Page
import org.apache.commons.lang3.StringUtils
import com.hfbank.util.HfbkUtil


trait EngineDAO extends OracleService {
  
  
  def search(sourceEngine: SourceEngine, page: Page): List[Map[String, String]] = {

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "SELECT Id, CLUSTERNAME, URL, Hostlist, HostList, Standard, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status FROM( SELECT Id, CLUSTERNAME, URL, Hostlist, STANDARD, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status, Rownum rn FROM(SELECT Id, CLUSTERNAME, URL, Hostlist, STANDARD, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status FROM t_Source_Engine WHERE 1=1"
    
   /* val whereSqlTmp = new StringBuffer
    if (StringUtils.isNotBlank(sourceEngine.status)) whereSqlTmp.append(s" and status in (${sourceEngine.status})")
    if (StringUtils.isNotBlank(sourceEngine.name)) whereSqlTmp.append(s" and name like '%${sourceEngine.name}%'")
    if (StringUtils.isNotBlank(sourceEngine.topic)) whereSqlTmp.append(s" and topic like '%${sourceEngine.topic}%'")
    if (StringUtils.isNotBlank(sourceEngine.hostList)) whereSqlTmp.append(s" and hostList like'%${sourceEngine.hostList}%'")*/
    
    val whereSql = new StringBuffer
    
    whereSql.append(getWhere(sourceEngine))
    whereSql.append(s" ORDER BY Creationtime DESC) ) t")
    whereSql.append(s" WHERE t.rn >= ${began} and t.rn <= ${end}")

    val sql = selectSqlTmp + whereSql
    
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }
  

  def insert(sourceEngine: SourceEngine): Int = {
    
    val sql = "INSERT INTO t_Source_Engine(Id,CLUSTERNAME,URL,Hostlist,STANDARD,Center,Description,Creationtime,Creator,Status) VALUES (Seq_Engine.Nextval,?,?,?,?,?,?,To_Date(?,'yyyy-MM-ddHH24:mi:ss'),?,?)"
    
    log.info(s"get into insert DAO: ${sourceEngine}")
    
    executeUpdate(sql, Seq(
      sourceEngine.clusterName,
      sourceEngine.url,
      sourceEngine.hostList,
      sourceEngine.standard,
      sourceEngine.center,
      sourceEngine.description,
      HfbkUtil.getSecond(),
      sourceEngine.creator,
      sourceEngine.status))
  }
  
  def update(sourceEngine: SourceEngine): Int = {
    
    val sql = """UPDATE t_Source_Engine
                    SET URL = ?,
                    Hostlist = ?,
                    STANDARD = ?,
                    Center = ?,
                    Description = ?,
                    Updatedtime = To_Date(?, 'yyyy-MM-ddHH24:mi:ss'),
                    Updater = ?,
                    Status = ?
                 WHERE Id = ?"""
    
    //val sql = "UPDATE t_Source_Engine SET (Type_ = ?, Hostlist = ?, Topic = ?, Center = ?, Description = ?, Updatedtime = To_Date(?, 'yyyy-MM-ddHH24:mi:ss'), Updater = ?, Status = ?) WHERE Id = ?"
    
    
    log.info(s"get into update DAO: ${sourceEngine},  SQL: ${sql}")
    
    executeUpdate(sql, Seq(
      sourceEngine.url,
      sourceEngine.hostList,
      sourceEngine.standard,
      sourceEngine.center,
      sourceEngine.description,
      HfbkUtil.getSecond(),
      sourceEngine.updater,
      sourceEngine.status,
      sourceEngine.id))
  }
  
  
  /**
   * 获取总记录数
   */
  def getTotal(sourceEngine: SourceEngine): List[Map[String, String]] = {

    val selectSqlTmp = "select count(1) total from t_Source_Engine where 1=1 "
    
    val whereSql = new StringBuffer
    whereSql.append(getWhere(sourceEngine))
    
    val sql = selectSqlTmp + whereSql
    query(sql, Seq())
  }
  
  
  def getWhere(sourceEngine: SourceEngine): String = {
    
    val str = new StringBuffer
    
    if (StringUtils.isNotBlank(sourceEngine.status)) str.append(s" and status in (${sourceEngine.status})")
    if (StringUtils.isNotBlank(sourceEngine.clusterName)) str.append(s" and CLUSTERNAME like '%${sourceEngine.clusterName}%'")
    if (StringUtils.isNotBlank(sourceEngine.url)) str.append(s" and URL like '%${sourceEngine.url}%'")
    if (StringUtils.isNotBlank(sourceEngine.hostList)) str.append(s" and hostList like'%${sourceEngine.hostList}%'")
    
    str.toString()
  }
  
  
}
