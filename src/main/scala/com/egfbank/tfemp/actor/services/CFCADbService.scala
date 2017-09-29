package com.egfbank.tfemp.actor.services

import java.sql.Timestamp

import scala.collection.mutable.ListBuffer
import scala.xml.Elem
import scala.xml.XML

import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.util.HfbkUtil
import com.egfbank.tfemp.util.XMLUtil

import sun.misc.BASE64Decoder
import xitrum.util.Loader

/**
 * CFCA相关数据库操作
 * @author huxp
 */
trait CFCADbService extends OracleService {

  val tfempInfo = Loader.propertiesFromClasspath("tfemp.properties")

  /**
   * 将CFCA的下行报文，写入下行数据表中
   */
  def insertIntoDownQueue(xml: Elem): Int = {
    log.info(s"Start insertIntoDownQueue, TransSerialNumber:${(xml \\ "TransSerialNumber").text.toString}")

    val TransSerialNumber = (xml \\ "TransSerialNumber").text.toString()
    val ApplicationID = (xml \\ "ApplicationID").text.toString()
    val TxCode = (xml \\ "TxCode").text.toString()
    val MessageFrom = (xml \\ "MessageFrom").text.toString()

    //判断数据库中是否存在
    if (selectDownByTransSerialNumber(TransSerialNumber).isEmpty) {
      val sql: String = "insert into TFEMP_CFCA_DOWN(ID,TransSerialNumber,ApplicationID,TxCode,MessageFrom,XmlContent,CreateTime,PlanTime,ExeTime,Status) values (?,?,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),to_date(?,'yyyy-MM-dd HH24:mi:ss'),to_date(?,'yyyy-MM-dd HH24:mi:ss'),?)"
      executeUpdate(sql, Seq(
        HfbkUtil.getUUID(),
        TransSerialNumber,
        ApplicationID,
        TxCode,
        MessageFrom,
        xml.mkString,
        HfbkUtil.getSecond(),
        HfbkUtil.getSecond(),
        HfbkUtil.getSecond(),
        "0"))
    } else {
      log.debug(s"交易流水号已存在:${TransSerialNumber}")
      0
    }
  }

  /**
   * 更新下行指令表，当业务失败的时候
   */
  def updateDownQueueError(applicationid: String): Int = {
    log.info(s"Start updateDownQueueError,application:${applicationid}")

    val sql: String = "update tfemp_cfca_down set status = ? where applicationid = ?"
    executeUpdate(sql, Seq(CFCADbService.Task_ERR, applicationid))
  }

  /**
   * 更新下行指令表，当业务失败的时候
   */
  def updateDownQueueHttpError(applicationid: String): Int = {
    log.info(s"Start updateDownQueueHttpError,applicationid:${applicationid}")

    val sql: String = "update tfemp_cfca_down set status = ? where applicationid = ?"
    executeUpdate(sql, Seq(CFCADbService.Task_HTTP_ERR, applicationid))
  }

  /**
   * 将反馈报文写入上行数据表中
   * 当flag不为空时，不需要检查上行指令表中是否存在重复的applicationid-
   * 目前只有在“动态查询业务”中才会出现对一个applicationid进行多次回馈
   */
  def insertIntoUpQueue(dataMap: Map[String, String], flag: String = null): Int = {
    log.info(s"Start insertIntoUpQueue,ApplicationID:${dataMap("applicationID")}")

    val sql = "insert into TFEMP_CFCA_UP(ID, TransSerialNumber, ApplicationID, TxCode, ToSend, CreateTime, RespTime, Status) values(?, ?, ?, ?, ?, ?, ?, ?)"
    if (selectUpQueue(dataMap("applicationID")).isEmpty || flag != null) {
      executeUpdate(sql, Seq(
        HfbkUtil.getUUID(),
        dataMap("transSerialNumber"),
        dataMap("applicationID"),
        dataMap("txCode"),
        dataMap("to"),
        HfbkUtil.getSecond(),
        HfbkUtil.getSecond(),
        "0"))
    } else {
      log.debug(s"Already response,AlApplicationID:${dataMap("applicationID")}")
      0
    }
  }

  /**
   * 向异常记录表中插入异常记录
   */
  def insertError(event: TEvent[_], t: Throwable) = {
    log.info("Start insertError with event")

    val sql = "insert into tfemp_error_record(id, applicationid, tradetype, seqno, svcid, svcscnid, info, createtime) values(?, ?, ?, ?, ?, ?, ?, to_date(?,'yyyy-MM-dd HH24:mi:ss'))"
    event.content match {
      case sourceData: Elem => {
        val appid = (sourceData \\ "ApplicationID").text.toString
        val tradetype = event.tradeType.toString
        val seqno = ""
        val svcid = ""
        val svcscnid = ""
        val info = t.getMessage

        executeUpdate(sql, Seq(
          HfbkUtil.getUUID(),
          appid,
          tradetype,
          seqno,
          svcid,
          svcscnid,
          info,
          HfbkUtil.getSecond()))
      }
      case data => {
        log.info(s"have error,data:${data}")
      }
    }
  }
  /**
   * 向异常记录表中插入异常记录
   */
  def insertError(applicationid: String, t: Throwable) = {
    log.info(s"Start insertError with applicationid:${applicationid}")

    val sql = "insert into tfemp_error_record(id, applicationid, info, createtime) values(?, ?, ?, to_date(?,'yyyy-MM-dd HH24:mi:ss'))"
    executeUpdate(sql, Seq(
      HfbkUtil.getUUID(),
      applicationid,
      t.getMessage,
      HfbkUtil.getSecond()))
  }

  /**
   *
   * @param applicationid
   * @param t
   * @return
   */
  def insertCaseReport(xml: Elem): Int = {
    log.info(s"Start insertCaseReport")

    val sql = "insert into TFEMP_CFCA_CASEREPORT(ID, APPLICATIONID, TRANSSERIALNUMBER, RESULT, XMLCONTENT, CREATETIME) values(?, ?, ?, ?, ?, to_date(?,'yyyy-MM-dd HH24:mi:ss'))"
    executeUpdate(sql, Seq(
      HfbkUtil.getUUID(),
      (xml \\ "ApplicationID").text.toString(),
      (xml \\ "TransSerialNumber").text.toString(),
      (xml \\ "Result").text.toString(),
      xml.toString(),
      HfbkUtil.getSecond()))
  }

  /**
   * @param xml
   * @return
   */
  def insertCaseReport(xmlContent: String): Int = {
    log.info(s"Start insertCaseReport")

    val xml = XML.loadString(xmlContent)
    insertCaseReport(xml)
  }

  /**
   * 查询反馈报文上行数据表
   */
  def selectUpQueue(applicationID: String): List[Map[String, String]] = {
    log.info(s"Start selectUpQueue,applicationID:${applicationID}")

    val sql: String = "select * from  TFEMP_CFCA_UP where ApplicationID = ? "
    query(sql, Seq(applicationID))
  }

  /**
   * 更新下行指令表
   */
  def updateDownQueue(transSerialNumber: String, dataMap: Map[String, String]) = {
    log.info(s"Start updateDownQueue,transSerialNumber:${transSerialNumber}")

    val sqlTemp = new StringBuffer("update TFEMP_CFCA_DOWN set ")
    dataMap.map { field => sqlTemp.append(field._1 + " = ?,") }
    val sql = new StringBuffer(sqlTemp.substring(0, sqlTemp.length - 1)).append(" where transSerialNumber = ?").toString
    val parameter = new ListBuffer[String]
    for (kv <- dataMap) parameter += kv._2
    parameter += transSerialNumber
    executeUpdate(sql, parameter.toSeq)
  }

  /**
   *  查询下行报文(按交易流水号)
   */
  def selectDownByTransSerialNumber(transSerialNumber: String): List[Map[String, String]] = {
    log.info(s"Start selectDownByTransSerialNumber, TransSerialNumber:${transSerialNumber}")

    val sql: String = "select * from  TFEMP_CFCA_DOWN where TransSerialNumber = ? "
    query(sql, Seq(transSerialNumber))
  }

  /**
   * 查询下行报文
   */
  def selectCFCADown(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectCFCADown, queryMap:${queryMap}")

    val selectHead = new StringBuffer("select ID,TRANSSERIALNUMBER,APPLICATIONID,TXCODE,MESSAGEFROM,CREATETIME,STATUS from (select ID,TRANSSERIALNUMBER,APPLICATIONID,TXCODE,MESSAGEFROM,CREATETIME,STATUS from TFEMP_CFCA_DOWN order by createtime desc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCADown,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 查询下行报文明细
   * @param queryMap
   * @return
   */
  def selectCFCADownView(id: String): List[Map[String, String]] = {
    log.info(s"Start selectCFCADownView, id:${id}")
    val sql = s"select XMLCONTENT from TFEMP_CFCA_DOWN where ID=?"
    query(sql, List(id))
  }

  /**
   * 查询动态查询表
   */
  def selectDynamicQuery(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectDynamicQuery, queryMap:${queryMap}")

    val selectHead = new StringBuffer("select id, applicationid, oriapplicationid, transtype, account, starttime, endtime, flag, phone from(select id, applicationid, oriapplicationid, transtype, account, starttime, endtime, flag, phone from tfemp_query_dynamic order by flag asc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectDynamicQuery,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 查询案件举报表
   */
  def selectCaseReport(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectCaseReport, queryMap:${queryMap}")

    val selectHead = new StringBuffer("select id, applicationid, transserialnumber, result, createtime from (select id, applicationid, transserialnumber, result, createtime from tfemp_cfca_casereport order by createtime desc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectDynamicQuery,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 查询下行报文明细
   * @param queryMap
   * @return
   */
  def selectCaseReportView(id: String): List[Map[String, String]] = {
    log.info(s"Start selectCaseReportView, id:${id}")
    val sql = s"select XMLCONTENT from tfemp_cfca_casereport where ID=?"
    query(sql, List(id))
  }

  /**
   * 记录案件举报交易
   */
  def insertIntoCaseReport(dataMap: Map[String, String]): Int = {
    log.info(s"Start insertIntoCaseReport")

    val sql = "insert into tfemp_cfca_casereport(ID, APPLICATIONID, TRANSSERIALNUMBER, XMLCONTENT, CREATETIME) values(?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'))"

    executeUpdate(sql, Seq(
      HfbkUtil.getUUID(),
      dataMap("APPLICATIONID"),
      dataMap("TRANSSERIALNUMBER"),
      dataMap("XMLCONTENT"),
      HfbkUtil.getSecond()))
  }

  /**
   * 查询上行报文明细
   * @param id
   * @return
   */
  def selectCFCAUpView(id: String): List[Map[String, String]] = {
    log.info(s"Start selectCFCAUpView, id:${id}")
    val sql = s"select XMLCONTENT from TFEMP_CFCA_UP where ID=?"
    query(sql, List(id))
  }

  /**
   * 查询esb报文明细
   * @param id
   * @return
   */
  def selectEsbXmlView(id: String): List[Map[String, String]] = {
    log.info(s"Start selectEsbXmlView, id:${id}")
    val sql = s"select REQUEST from TFEMP_ESB_TASK where ID=?"
    query(sql, List(id))
  }

  /**
   * 查询esb报文反馈明细
   * @param id
   * @return
   */
  def selectEsbXmlViewResult(id: String): List[Map[String, String]] = {
    log.info(s"Start selectEsbXmlViewResult, id:${id}")
    val sql = s"select RESPONSE from TFEMP_ESB_TASK where ID=?"
    query(sql, List(id))
  }

  /**
   * 查询上行报文反馈明细
   * @param id
   * @return
   */
  def selectCFCAUpViewResult(id: String): List[Map[String, String]] = {
    log.info(s"Start selectCFCAUpViewResult, id:${id}")
    val sql = s"select RESPONSE from TFEMP_CFCA_UP where ID=?"
    query(sql, List(id))
  }

  /**
   * 查询上行报文
   */
  def selectCFCAUp(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectCFCAUp, queryMap:${queryMap}")

    val selectHead = new StringBuffer("select ID,APPLICATIONID,TXCODE,TOSEND,CREATETIME,RESPTIME,STATUS from (select ID,APPLICATIONID,TXCODE,TOSEND,CREATETIME,RESPTIME,STATUS from TFEMP_CFCA_UP order by createtime desc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 将CFCA的强制措施命令写入强制措施表中
   */
  def insertIntoForceMeasure(xml: Elem): Int = {
    log.info(s"Start insertIntoForceMeasure,ApplicationID:${(xml \\ "ApplicationID").text.toString}")

    val ApplicationID = (xml \\ "ApplicationID").text.toString
    val OriApplicationID = (xml \\ "OriginalApplicationID").text.toString
    val TxCode = (xml \\ "TxCode").text.toString()

    // 申请机关
    val ApplicationOrgID = (xml \\ "ApplicationOrgID").text.toString
    val ApplicationOrgName = (xml \\ "ApplicationOrgName").text.toString
    // 币种，解除时需要
    val Currency = (xml \\ "Currency").text.toString
    // 账户
    val AccountNumber = (xml \\ "AccountNumber").text.toString
    val AcctNo = if (AccountNumber.contains("_")) AccountNumber.split("_")(0) else AccountNumber
    val SubAcctNo = if (AccountNumber.contains("_")) AccountNumber.split("_")(1) else ""

    // 冻结类型和冻结金额
    val freezeType = (xml \\ "FreezeType").text.toString
    val balance = (xml \\ "Balance").text.toString

    if (selectForceMeasure(ApplicationID).isEmpty) {
      log.info("In ForceMeasurce do not have the same applicaitonId")
      val sql = "insert into TFEMP_FORCE_MEASURE(ID, ApplicationID, OriApplicationID, TxCode, Account, SubAccount, EXECUTE_TIME, ApplicationOrgID, ApplicationOrgName, Status, Extend1, FREEZE_TYPE, BALANCE) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
      log.debug(s"Insert Into ForceMeasure,ApplicationID:${ApplicationID}")
      executeUpdate(sql, Seq(
        HfbkUtil.getUUID(),
        ApplicationID,
        OriApplicationID,
        TxCode,
        AcctNo,
        SubAcctNo,
        new Timestamp(System.currentTimeMillis()),
        ApplicationOrgID,
        ApplicationOrgName,
        CFCADbService.ForceMeasure_EXE,
        Currency,
        freezeType,
        balance))
    } else {
      log.debug(s"ApplicationID already exist:${ApplicationID}")
      0
    }
  }

  /**
   * 将CFCA的强制措施法律文书写入强制措施法律文书表
   */
  def insertIntoForceMeasureFile(xml: Elem) = {
    log.info(s"Start insertIntoForceMeasureFile, ApplicationID:${(xml \\ "ApplicationID").text.toString}")

    val ApplicationID = (xml \\ "ApplicationID").text.toString

    val sql = "insert into TFEMP_FORCE_MEASURE_FILE(ID, ApplicationID, FILENAME, FILECONTENT) values(?, ?, ?, ?)"
    // 拿到报文中的法律文书List
    val Attachments = XMLUtil.getFVListMap("Attachments", xml.toString, "Body")

    Attachments.map { attachment =>
      log.debug(s"${sql},parameter:${attachment}")
      executeUpdate(sql, Seq(
        HfbkUtil.getUUID(),
        ApplicationID,
        attachment("Filename"),
        attachment("Content")))
    }
  }

  /**
   * 查询法律文书表
   */
  def selectForceMeasureFile(applicationID: String) = {
    log.info(s"Start selectForceMeasureFile,applicationID:${applicationID}")

    val sql = "select * from TFEMP_FORCE_MEASURE_FILE where ApplicationID = ?"
    query(sql, Seq(applicationID))
  }

  /**
   * 查询强制措施命令记录表
   */
  def selectForceMeasure(applicationID: String): List[Map[String, String]] = {
    log.info(s"Start selectForceMeasure,applicationID:${applicationID}")

    val sql: String = "select * from TFEMP_FORCE_MEASURE where ApplicationID = ? and ESBID is not null"
    query(sql, Seq(applicationID))
  }

  /**
   * 查询强制措施命令记录表
   */
  def selectForceMeasureByAccount(ACCOUNT: String, SUBACCOUNT: String): List[Map[String, String]] = {
    log.info(s"Start selectForceMeasureByAccount,ACCOUNT:${ACCOUNT},SUBACCOUNT:${SUBACCOUNT}")

    val sql = "select * from TFEMP_FORCE_MEASURE where account = ? and subaccount = ?"
    query(sql, Seq(ACCOUNT, SUBACCOUNT))
  }

  /**
   * 查询强制措施命令记录表
   */
  def selectForceMeasureByTime(StartDate: String, EndDate: String): List[Map[String, String]] = {
    log.info(s"Start selectForceMeasureByTime,StartDate:${StartDate},EndDate:${EndDate}")

    val sql = "select * from TFEMP_FORCE_MEASURE where EXECUTE_TIME > to_date(?,'YYYYMMDD') and EXECUTE_TIME < to_date(?,'YYYYMMDD') "
    query(sql, Seq(StartDate, EndDate))
  }

  /**
   * 查询强制措施命令记录表
   */
  def selectForceMeasureByAccount(ACCOUNT: String, SUBACCOUNT: String, StartDate: String, EndDate: String): List[Map[String, String]] = {
    log.info(s"Start selectForceMeasureByAccount,ACCOUNT:${ACCOUNT},SUBACCOUNT:${SUBACCOUNT}")

    val sql = "select * from TFEMP_FORCE_MEASURE where account = ? and subaccount = ? and EXECUTE_TIME > to_date(?,'YYYYMMDD') and EXECUTE_TIME < to_date(?,'YYYYMMDD')"
    query(sql, Seq(ACCOUNT, SUBACCOUNT, StartDate, EndDate))
  }

  /**
   * 更新强制措施命令记录表
   */
  def updateForceMeasure(applicationId: String, dataMap: Map[String, String]) = {
    log.info(s"Start updateForceMeasure,applicationID:${applicationId}")

    val sqlTemp = new StringBuffer("update TFEMP_FORCE_MEASURE set ")
    dataMap.map { field => sqlTemp.append(field._1 + " = ?,") }
    val sql = new StringBuffer(sqlTemp.substring(0, sqlTemp.length - 1)).append(" where applicationId = ?").toString
    val parameter = new ListBuffer[String]
    for (kv <- dataMap) parameter += kv._2
    parameter += applicationId
    executeUpdate(sql, parameter.toSeq)
  }

  /**
   * 从CFCA下行指令表中获取要执行的命令
   */
  def selectDownQueue4Exe() = {
    log.info("Start selectDownQueue4Exe")

    val sql = "select * from TFEMP_CFCA_DOWN where plantime < to_date(?, 'yyyy-MM-dd HH24:mi:ss') and status = ? and rownum between 1 and 10"
    query(sql, Seq(HfbkUtil.getSecond(), CFCADbService.Task_CKS))
  }

  /**
   * 从CFCA下行指令表中获取要执行的命令
   */
  def selectDownQueueError4Exe() = {
    log.info("Start selectDownQueueError4Exe")

    val sql = "select * from TFEMP_CFCA_DOWN where status = ? and rownum between 1 and 10"
    query(sql, Seq(CFCADbService.Task_HTTP_ERR))
  }

  /**
   * 从动态指令表中获取要执行的查询命令
   */
  def selectQueryDynamic() = {
    log.info("Start selectQueryDynamic")
    // 执行的时间
    val sql = "select * from tfemp_query_dynamic where starttime < to_date(?, 'yyyy-MM-dd HH24:mi:ss') and endtime > to_date(?, 'yyyy-MM-dd HH24:mi:ss') and Flag = ? and TRANSTYPE=?"
    query(sql, Seq(HfbkUtil.getSecond(), HfbkUtil.getSecond(), "0", "100305"))
  }
  /**
   * 从动态指令表中获取要执行的查询命令
   */
  def selectQueryDynamicFree() = {
    log.info("Start selectQueryDynamicFree")

    val sql = "select * from tfemp_query_dynamic where endtime < to_date(?, 'yyyy-MM-dd HH24:mi:ss') and Flag = ?"
    query(sql, Seq(HfbkUtil.getSecond(), "0"))
  }

  /**
   * 将报文写入esbxml数据表中
   */
  def insertIntoEsbXml(dataMap: Map[String, String]): Int = {
    log.info(s"Start insertIntoEsbXml")

    val sql = "insert into TFEMP_ESB_TASK(ID, APPLICATIONID, SEQNO, SVCID, SVCSCNID, REQUEST, RESPONSE, CREATETIME, STATUS) values(?, ?, ?, ?, ?, ?, ?, ?, ?)"
    executeUpdate(sql, Seq(
      dataMap("ID"),
      dataMap("APPLICATIONID"),
      dataMap.getOrElse("SEQNO", ""),
      dataMap.getOrElse("SVCID", ""),
      dataMap.getOrElse("SVCSCNID", ""),
      dataMap.getOrElse("REQUEST", ""),
      dataMap.getOrElse("RESPONSE", ""),
      new Timestamp(System.currentTimeMillis()),
      CFCADbService.ESB_EXE))
  }

  /**
   * 更新下行指令表
   */
  def updateEsbXml(id: String, dataMap: Map[String, String]) = {
    log.info("Start updateEsbXml")

    val sqlTemp = new StringBuffer("update TFEMP_ESB_TASK set ")
    dataMap.map { field => sqlTemp.append(field._1 + " = ?,") }
    val sql = new StringBuffer(sqlTemp.substring(0, sqlTemp.length - 1)).append(" where ID = ?").toString
    val parameter = new ListBuffer[String]
    for (kv <- dataMap) parameter += kv._2
    parameter += id
    executeUpdate(sql, parameter.toSeq)
  }

  /**
   * @param id
   * @param dataMap
   * @return
   */
  def updateEsbXmlError(id: String) = {
    log.info("Start updateEsbXmlError")

    val sql = "update TFEMP_ESB_TASK set STATUS=? where id=?"
    executeUpdate(sql, Seq(CFCADbService.ESB_ERR, id))
  }

  /**
   * 查询ESB报文
   */
  def selectEsbXml(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectEsbXml, queryMap:${queryMap}")

    val selectHead = new StringBuffer("select ID,APPLICATIONID,SEQNO,SVCID,SVCSCNID,CREATETIME,STATUS from (select ID,APPLICATIONID,SEQNO,SVCID,SVCSCNID,CREATETIME,STATUS from TFEMP_ESB_TASK order by createtime desc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 将动态查询指令保存到动态查询指令表中
   */
  def insertDynamic(xml: Elem) = {
    log.info(s"Start insertDynamic")

    // 执行的时间
    val Applicationid = (xml \\ "ApplicationID").text.toString
    val Oriapplicationid = (xml \\ "OriginalApplicationID").text.toString
    val TxCode = (xml \\ "TxCode").text.toString
    val Account = (xml \\ "CardNumber").text.toString
    val StartTime = (xml \\ "InterceptionStartTime").text.toString
    val EndTime = (xml \\ "InterceptionEndTime").text.toString

    val sql = """insert into TFEMP_QUERY_DYNAMIC(id, Applicationid, Oriapplicationid, TRANSTYPE, Account, Content, StartTime, EndTime, Flag, PHONE) values(?,?,?,?,?,?,to_date(?,'yyyyMMddHH24miss'), to_date(?,'yyyyMMddHH24miss'), 0, ?)"""

    if (selectDynamicByAppId(Applicationid).isEmpty) {
      executeUpdate(sql, Seq(
        HfbkUtil.getUUID(),
        Applicationid,
        Oriapplicationid,
        TxCode,
        Account,
        xml.mkString,
        StartTime,
        EndTime,
        ""))
    } else {
      log.debug(s"动态查询任务存在:${Applicationid}")
      0
    }
  }

  /**
   * 查询动态查询指令
   */
  def selectDynamicByAppId(applicationid: String): List[Map[String, String]] = {
    log.info(s"Start selectDynamicByAppId")

    val sql = """select * from TFEMP_QUERY_DYNAMIC where Applicationid = ?"""
    query(sql, Seq(applicationid))
  }

  /**
   * 将电话号码更新到动态查询指令表中
   */
  def updateDynamic(applicationid: String, phone: String) = {
    log.info(s"Start updateDynamic")

    val sql = """update TFEMP_QUERY_DYNAMIC set phone = ? where applicationid = ?"""
    executeUpdate(sql, Seq(phone, applicationid))
  }

  /**
   * 更新动态查询任务状态为解除状态
   */
  def updateDynamicFree(applicationid: String) = {
    //修改原动态查询任务为已完成
    log.info(s"Start updateDynamicFree")

    val updateDynamicSql = "update TFEMP_QUERY_DYNAMIC set flag = ? where APPLICATIONID = ?"
    executeUpdate(updateDynamicSql, Seq("1", applicationid))
  }

  /**
   * 更新下行指令表状态位为已完成状态
   */
  def updateDownQueueStatus(applicationid: String, status: String) = {
    log.info(s"Start updateDownQueueStatus,applicationid:${applicationid}")

    val sql = "update tfemp_cfca_down set status = ? where applicationid = ?"
    executeUpdate(sql, Seq(status, applicationid))
  }
  /**
   * 从上行指令表中找到没有反馈成功的记录
   */
  def findFeedbackError() = {
    log.info(s"Start findFeedbackError")

    val sql = "select * from tfemp_cfca_up where status = ?"
    query(sql, Seq(CFCADbService.CFCA_RESP_ERROR))
  }

  /**
   * 查询校验失败表（tfemp_verify_failed）
   */
  def getVerFaild() = {
    log.info(s"Start getVerFaild")

    val sql: String = "select * from tfemp_verify_failed where status=?"
    query(sql, Seq(CFCADbService.INSERT_TDH_VER_NO))
  }

  /**
   * 更新校验失败表（tfemp_verify_failed）为已入TDH库
   */
  def updateVerFaild(id: String) = {
    log.info(s"Start updateVerFaild")

    val sql: String = "update tfemp_verify_failed set status=1 where id=?"
    executeUpdate(sql, Seq(id))
  }

  /**
   * 查询错误
   */
  def selectError(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectEsbXml, queryMap:${queryMap}")
    val selectHead = new StringBuffer("select * from (select * from TFEMP_ERROR_RECORD order by CREATETIME desc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      //      sql = selectHead.toString.substring(0, selectHead.lastIndexOf("and"))
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 查询强制措施
   */
  def selectForce(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectCFCAUp, queryMap:${queryMap}")
    val selectHead = new StringBuffer("select * from (select * from TFEMP_FORCE_MEASURE order by EXECUTE_TIME desc) ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 50").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 50").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    query(sql, parameter.toSeq)
  }

  /**
   * 查询强制措施文件
   */
  def selectForce2(queryMap: Map[String, String]): List[Map[String, String]] = {
    log.info(s"Start selectCFCAUp, queryMap:${queryMap}")
    val selectHead = new StringBuffer("select * from TFEMP_FORCE_MEASURE_FILE ")
    var sql: String = ""
    if (queryMap.size > 0) {
      selectHead.append("where ")
      queryMap.map { field => selectHead.append(field._1 + "=? and ") }
      sql = selectHead.append("rownum between 0 and 30").toString()
    } else {
      sql = selectHead.append("where rownum between 0 and 30").toString()
    }
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    query(sql, parameter.toSeq)
    //    var file = query(sql, parameter.toSeq).toString()
    //    val path = "/tfemp/app"
    //    val name:String = "a"
    //    import com.egfbank.tfemp.util.DecodeUtils
    //    DecodeUtils.decodeBase64ToImage(file, path, name).toString()
  }

  /**
   * 查询文件内容
   */
  def getFileContent(queryMap: Map[String, String]): Array[Byte] = {
    log.info(s"Start selectCFCAUp, queryMap:${queryMap}")
    val selectHead = new StringBuffer("select * from TFEMP_FORCE_MEASURE_FILE ")
    var sql: String = ""
    //    if (queryMap.size > 0) {
    //      selectHead.append("where ")
    //      queryMap.map { field => selectHead.append("=?") }
    ////      sql = selectHead.toString.substring(0, selectHead.lastIndexOf("and"))
    //    } else {
    //      val fileNames = queryMap("fileName")
    //      sql = selectHead.append("where FILENAME =?")
    //    }
    val fileNames = queryMap("FILENAME")
    sql = selectHead.append("where FILENAME =?").toString()
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    val file = query(sql, parameter.toSeq)(0).get("FILECONTENT").get
    val decoder = new BASE64Decoder()
    val decoderBytes = decoder.decodeBuffer(file.replace(" ", ""))
    decoderBytes
  }

  /**
   * @param queryMap
   * @return
   */
  def getFileContent2(fileName: String): Array[Byte] = {
    val selectHead = new StringBuffer("select * from TFEMP_FORCE_MEASURE_FILE ")
    var sql: String = ""
    //    if (queryMap.size > 0) {
    //      selectHead.append("where ")
    //      queryMap.map { field => selectHead.append("=?") }
    ////      sql = selectHead.toString.substring(0, selectHead.lastIndexOf("and"))
    //    } else {
    //      val fileNames = queryMap("fileName")
    //      sql = selectHead.append("where FILENAME =?")
    //    }
    sql = selectHead.append("where FILENAME =?").toString()
    val parameter = new ListBuffer[String]
    parameter.+=(fileName)
    log.info(s"selectCFCAUp,sql:${sql}")
    val file = query(sql, parameter.toSeq)(0)

    val decoder = new BASE64Decoder()
    val decoderBytes = decoder.decodeBuffer(file("FILECONTENT"))
    decoderBytes
  }

  /**
   * 查询文件内容不解码
   */
  def getFileContent3(queryMap: Map[String, String]): String = {
    log.info(s"Start selectCFCAUp, queryMap:${queryMap}")
    val selectHead = new StringBuffer("select * from TFEMP_FORCE_MEASURE_FILE ")
    var sql: String = ""
    val fileNames = queryMap("FILENAME")
    sql = selectHead.append("where FILENAME =?").toString()
    val parameter = new ListBuffer[String]
    for (kv <- queryMap) parameter += kv._2
    log.info(s"selectCFCAUp,sql:${sql}")
    query(sql, parameter.toSeq)
    val file = query(sql, parameter.toSeq)(0)
    file("FILECONTENT")
  }

  /**
   * 向tfemp_verify_failed表中插入数据
   * @param channel
   * @param transSerialNumber
   * @param verifyFailedStatus
   * @param status
   * @return
   */
  def insertIntoVerifyFailed(dataType1: String, data1: String, dataType2: String, data2: String, channel: String, transSerialNumber: String, verifyFailedStatus: String, status: String): Int = {
    //判断数据库中是否存在
    val sql: String = "insert into TFEMP_VERIFY_FAILED(id, trans_serial_number, channel, outidtype, outidcode, inidtype, inidcode, trans_time, verify_failed_status, status) values (?,?,?,?,?,?,?,?,?,?)"
    executeUpdate(sql, Seq(
      HfbkUtil.getUUID(),
      transSerialNumber,
      channel,
      dataType1,
      data1,
      dataType2,
      data2,
      HfbkUtil.getSecond(),
      verifyFailedStatus,
      "0"))
  }
}

object CFCADbService {
  /**
   * 强制措施执行状态
   */
  val ForceMeasure_EXE = "0"
  /**
   * 强制措施执行失败状态
   */
  val ForceMeasure_FAIL = "9"
  /**
   * 强制措施执行成功状态
   */
  val ForceMeasure_SUCC = "1"
  /**
   * 未知
   */
  val UNKOWN = "11"

  /**
   * 指令创建状态 task_create
   */
  val Task_CRE = "0"
  /**
   * 报文校验成功task_checkSuccess
   */
  val Task_CKS = "1"
  /**
   * 报文校验失败task_checkFailed
   */
  val Task_CKF = "2"
  /**
   * 指令执行中状态task_execute
   */
  val Task_EXE = "3"
  /**
   * 指令执行执行完成状态task_finish
   */
  val Task_FIN = "4"
  /**
   * 指令执行失败
   */
  val Task_ERR = "5"
  /**
   * 指令执行网络连接失败
   */
  val Task_HTTP_ERR = "6"
  /**
   * 指令执行的反馈结果因前置机未能接受而失败
   */
  val Task_Receive_ERR = "7"
  /**
   * ESB接口调用开始
   */
  val ESB_EXE = "1"
  /**
   * ESB接口调用成功
   */
  val ESB_SUC = "2"
  /**
   * ESB接口调用失败
   */
  val ESB_ERR = "3"

  /**
   * CFCA接受反馈成功
   */
  val CFCA_RESP_SUCC = "1"
  /**
   * CFCA接受反馈失败
   */
  val CFCA_RESP_ERROR = "2"

  /**
   * 已入TDH库
   */
  val INSERT_TDH_VER_FIN = "1"
  /**
   * 未入TDH库
   */
  val INSERT_TDH_VER_NO = "0"
}



