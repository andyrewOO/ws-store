package com.hfbank.actor.dao.source

import com.hfbank.actor.dao.OracleService
import com.hfbank.mode.bean.SourceQueue
import com.hfbank.mode.Page
import org.apache.commons.lang3.StringUtils
import com.hfbank.util.HfbkUtil


trait QueueDAO extends OracleService {
  
  
  def search(sourceQueue: SourceQueue, page: Page): List[Map[String, String]] = {

    // 分页参数
    val index = page.start
    val size = page.size
    val began = (index - 1) * size + 1
    val end = index * size

    val selectSqlTmp = "SELECT Id, NAME, Type_, Messageid, Hostlist, Topic, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status FROM( SELECT Id, NAME, Type_, Messageid, Hostlist, Topic, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status, Rownum rn FROM(SELECT Id, NAME, Type_, Messageid, Hostlist, Topic, Center, Description, Creationtime, Creator, Updatedtime, Updater, Status FROM t_Source_Queue WHERE 1=1"
    
   /* val whereSqlTmp = new StringBuffer
    if (StringUtils.isNotBlank(sourceQueue.status)) whereSqlTmp.append(s" and status in (${sourceQueue.status})")
    if (StringUtils.isNotBlank(sourceQueue.name)) whereSqlTmp.append(s" and name like '%${sourceQueue.name}%'")
    if (StringUtils.isNotBlank(sourceQueue.topic)) whereSqlTmp.append(s" and topic like '%${sourceQueue.topic}%'")
    if (StringUtils.isNotBlank(sourceQueue.hostList)) whereSqlTmp.append(s" and hostList like'%${sourceQueue.hostList}%'")*/
    
    val whereSql = new StringBuffer
    
    whereSql.append(getWhere(sourceQueue))
    whereSql.append(s" ORDER BY Creationtime DESC) ) t")
    whereSql.append(s" WHERE t.rn >= ${began} and t.rn <= ${end}")

    val sql = selectSqlTmp + whereSql
    
    log.info(s"sql:${sql}")

    query(sql, Seq())
  }
  

  def insert(sourceQueue: SourceQueue): Int = {
    
    val sql = "INSERT INTO t_Source_Queue(Id,NAME,Messageid,Type_,Hostlist,Topic,Center,Description,Creationtime,Creator,Status) VALUES (Seq_Queue.Nextval,?,?,?,?,?,?,?,To_Date(?,'yyyy-MM-ddHH24:mi:ss'),?,?)"
    
    log.info(s"get into insert DAO: ${sourceQueue}")
    
    executeUpdate(sql, Seq(
      sourceQueue.name,
      sourceQueue.messageid,
      sourceQueue.mqType,
      sourceQueue.hostList,
      sourceQueue.topic,
      sourceQueue.center,
      sourceQueue.description,
      HfbkUtil.getSecond(),
      sourceQueue.creator,
      sourceQueue.status))
  }
  
  def update(sourceQueue: SourceQueue): Int = {
    
    val sql = """UPDATE t_Source_Queue
                    SET MESSAGEID = ?,
                    Type_ = ?,
                    Hostlist = ?,
                    Topic = ?,
                    Center = ?,
                    Description = ?,
                    Updatedtime = To_Date(?, 'yyyy-MM-ddHH24:mi:ss'),
                    Updater = ?,
                    Status = ?
                 WHERE Id = ?"""
    
    //val sql = "UPDATE t_Source_Queue SET (Type_ = ?, Hostlist = ?, Topic = ?, Center = ?, Description = ?, Updatedtime = To_Date(?, 'yyyy-MM-ddHH24:mi:ss'), Updater = ?, Status = ?) WHERE Id = ?"
    
    
    log.info(s"get into update DAO: ${sourceQueue},  SQL: ${sql}")
    
    executeUpdate(sql, Seq(
      sourceQueue.messageid,
      sourceQueue.mqType,
      sourceQueue.hostList,
      sourceQueue.topic,
      sourceQueue.center,
      sourceQueue.description,
      HfbkUtil.getSecond(),
      sourceQueue.updater,
      sourceQueue.status,
      sourceQueue.id))
  }
  
  
  /**
   * 获取总记录数
   */
  def getTotal(sourceQueue: SourceQueue): List[Map[String, String]] = {

    val selectSqlTmp = "select count(1) total from t_Source_Queue where 1=1 "
    
    val whereSql = new StringBuffer
    whereSql.append(getWhere(sourceQueue))
    
    val sql = selectSqlTmp + whereSql
    query(sql, Seq())
  }
  
  
  def getWhere(sourceQueue: SourceQueue): String = {
    
    val str = new StringBuffer
    
    if (StringUtils.isNotBlank(sourceQueue.status)) str.append(s" and status in (${sourceQueue.status})")
    if (StringUtils.isNotBlank(sourceQueue.name)) str.append(s" and name like '%${sourceQueue.name}%'")
    if (StringUtils.isNotBlank(sourceQueue.topic)) str.append(s" and topic like '%${sourceQueue.topic}%'")
    if (StringUtils.isNotBlank(sourceQueue.hostList)) str.append(s" and hostList like'%${sourceQueue.hostList}%'")
    
    str.toString()
  }
  
  
}
