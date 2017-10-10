package com.hfbank.actor.dao.source

import com.hfbank.actor.dao.OracleService
import com.hfbank.mode.bean.SourceDB
import com.hfbank.mode.Page
import org.apache.commons.lang3.StringUtils
import com.hfbank.util.HfbkUtil


trait DBDAO extends OracleService {
  
  
  def search(sourceDB: SourceDB, page: Page): List[Map[String, String]] = {

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "SELECT Id, DBNAME, Type_, URL, USERNAME, PASSWORD, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status FROM(select Id, DBNAME, Type_, URL, USERNAME, PASSWORD, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status, Rownum rn FROM(SELECT Id, DBNAME, Type_, URL, USERNAME, PASSWORD, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status FROM t_Source_DB WHERE 1=1"
    
   /* val whereSqlTmp = new StringBuffer
    if (StringUtils.isNotBlank(sourceDB.status)) whereSqlTmp.append(s" and status in (${sourceDB.status})")
    if (StringUtils.isNotBlank(sourceDB.name)) whereSqlTmp.append(s" and name like '%${sourceDB.name}%'")
    if (StringUtils.isNotBlank(sourceDB.topic)) whereSqlTmp.append(s" and topic like '%${sourceDB.topic}%'")
    if (StringUtils.isNotBlank(sourceDB.hostList)) whereSqlTmp.append(s" and hostList like'%${sourceDB.hostList}%'")*/
    
    val whereSql = new StringBuffer
    
    whereSql.append(getWhere(sourceDB))
    whereSql.append(s" ORDER BY Creationtime DESC) ) t")
    whereSql.append(s" WHERE t.rn >= ${began} and t.rn <= ${end}")

    val sql = selectSqlTmp + whereSql
    
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }
  

  def insert(sourceDB: SourceDB): Int = {
    
    val sql = "INSERT INTO t_Source_DB(Id,TYPE_,DBNAME,URL,USERNAME,PASSWORD,Center,Description,Creationtime,Creator,Status) VALUES (Seq_DB.Nextval,?,?,?,?,?,?,?,To_Date(?,'yyyy-MM-ddHH24:mi:ss'),?,?)"
    
    log.info(s"get into insert DAO: ${sourceDB}")
    
    executeUpdate(sql, Seq(
      sourceDB.dbType,
      sourceDB.dbName,
      sourceDB.url,
      sourceDB.dbUserName,
      sourceDB.password,
      sourceDB.center,
      sourceDB.description,
      HfbkUtil.getSecond(),
      sourceDB.creator,
      sourceDB.status))
  }
  
  def update(sourceDB: SourceDB): Int = {
    
    val sql = """UPDATE t_Source_DB
                    SET URL = ?,
                    USERNAME = ?,
                    PASSWORD = ?,
                    Center = ?,
                    Description = ?,
                    Updatedtime = To_Date(?, 'yyyy-MM-ddHH24:mi:ss'),
                    Updater = ?,
                    Status = ?
                 WHERE Id = ?"""
    
    //val sql = "UPDATE t_Source_DB SET (Type_ = ?, Hostlist = ?, Topic = ?, Center = ?, Description = ?, Updatedtime = To_Date(?, 'yyyy-MM-ddHH24:mi:ss'), Updater = ?, Status = ?) WHERE Id = ?"
    
    
    log.info(s"get into update DAO: ${sourceDB},  SQL: ${sql}")
    
    executeUpdate(sql, Seq(
      sourceDB.url,
      sourceDB.dbUserName,
      sourceDB.password,
      sourceDB.center,
      sourceDB.description,
      HfbkUtil.getSecond(),
      sourceDB.updater,
      sourceDB.status,
      sourceDB.id))
  }
  
  
  /**
   * 获取总记录数
   */
  def getTotal(sourceDB: SourceDB): List[Map[String, String]] = {

    val selectSqlTmp = "select count(1) total from t_Source_DB where 1=1 "
    
    val whereSql = new StringBuffer
    whereSql.append(getWhere(sourceDB))
    
    val sql = selectSqlTmp + whereSql
    query(sql, Seq())
  }
  
  
  def getWhere(sourceDB: SourceDB): String = {
    
    val str = new StringBuffer
    
    if (StringUtils.isNotBlank(sourceDB.status)) str.append(s" and status in (${sourceDB.status})")
    if (StringUtils.isNotBlank(sourceDB.dbName)) str.append(s" and DBNAME like '%${sourceDB.dbName}%'")
    if (StringUtils.isNotBlank(sourceDB.url)) str.append(s" and URL like '%${sourceDB.url}%'")
    if (StringUtils.isNotBlank(sourceDB.dbType)) str.append(s" and Type_ in (${sourceDB.dbType})")
    
    str.toString()
  }
  
  
}
