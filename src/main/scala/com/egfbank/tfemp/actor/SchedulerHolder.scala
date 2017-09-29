package com.egfbank.tfemp.actor

import akka.actor.Props
import com.egfbank.tfemp.actor.worker.scheduler.job.CFCARequestTask
import com.egfbank.tfemp.actor.worker.scheduler.job.CFCAExecuteTask
import com.egfbank.tfemp.actor.worker.scheduler.job.TFScanDynamicTask
import com.egfbank.tfemp.actor.worker.scheduler.job.TFScanDynamicFreeTask
import com.egfbank.tfemp.actor.worker.scheduler.MasterScheduler
import com.egfbank.tfemp.actor.worker.scheduler.Job
import com.egfbank.tfemp.actor.worker.Start
import com.egfbank.tfemp.actor.worker.scheduler.CronParser
import com.egfbank.tfemp.actor.worker.scheduler.JobSchedule
import xitrum.util.Loader
import com.egfbank.tfemp.actor.worker.scheduler.job.CFCAExecuteErrorTask
import com.egfbank.tfemp.actor.worker.scheduler.job.TFScanUpQueue4Err
import com.egfbank.tfemp.actor.worker.scheduler.job.TFScanVerFaild
import com.egfbank.tfemp.actor.worker.scheduler.job.CFCAReportExceptionTask
import com.egfbank.tfemp.actor.worker.scheduler.job.CFCAReportOpenCardTask
import com.egfbank.tfemp.actor.worker.scheduler.job.CFCAReportDoubtAccTask
import xitrum.Log

/**
 * @author huxp
 */
object SchedulerHander extends Log{

  //定时任务时间参数
  val scanDownQueue = Loader.propertiesFromClasspath("tfemp.properties").getProperty("scanDownQueue")
  val scanFrontEnd = Loader.propertiesFromClasspath("tfemp.properties").getProperty("scanFrontEnd")
  val scanDynamicTask = Loader.propertiesFromClasspath("tfemp.properties").getProperty("scanDynamicTask")
  val scanDynamicFreeTask = Loader.propertiesFromClasspath("tfemp.properties").getProperty("scanDynamicFreeTask")
  val cfcaExecuteErrorTaskTime = Loader.propertiesFromClasspath("tfemp.properties").getProperty("cfcaExecuteErrorTask")
  val scanUpQueue4Err = Loader.propertiesFromClasspath("tfemp.properties").getProperty("scanUpQueue4Err")
  val scanVerFaild = Loader.propertiesFromClasspath("tfemp.properties").getProperty("scanVerFaild")
  val reportExceptionTask = Loader.propertiesFromClasspath("tfemp.properties").getProperty("reportExceptionTask")
  val reportDoubtAccTask = Loader.propertiesFromClasspath("tfemp.properties").getProperty("reportDoubtAccTask")
  val reportOpenCardTask = Loader.propertiesFromClasspath("tfemp.properties").getProperty("reportOpenCardTask")

  //定时任务
  val masterSchedulerActor = xitrum.Config.actorSystem.actorOf(Props[MasterScheduler], "MasterScheduler")
  val cfcaRequestTask = xitrum.Config.actorSystem.actorOf(Props[CFCARequestTask], "cfcaRequestTask")
  val cfcaExecuteTask = xitrum.Config.actorSystem.actorOf(Props[CFCAExecuteTask], "cfcaExecuteTask")
  val TFScanDynamicTask = xitrum.Config.actorSystem.actorOf(Props[TFScanDynamicTask], "TFScanDynamicTask")
  val TFScanDynamicFreeTask = xitrum.Config.actorSystem.actorOf(Props[TFScanDynamicFreeTask], "TFScanDynamicFreeTask")
  val cfcaExecuteErrorTask = xitrum.Config.actorSystem.actorOf(Props[CFCAExecuteErrorTask], "cfcaExecuteErrorTask")
  val TFScanUpQueue4ErrTask = xitrum.Config.actorSystem.actorOf(Props[TFScanUpQueue4Err], "TFScanUpQueue4Err")

  val TFScanVerFaild = xitrum.Config.actorSystem.actorOf(Props[TFScanVerFaild], "TFScanVerFaild")
  val CFCAReportExceptionTask = xitrum.Config.actorSystem.actorOf(Props[CFCAReportExceptionTask], "CFCAReportExceptionTask")
  val CFCAReportDoubtAccTask = xitrum.Config.actorSystem.actorOf(Props[CFCAReportDoubtAccTask], "CFCAReportDoubtAccTask")
  val CFCAReportOpenCardTask = xitrum.Config.actorSystem.actorOf(Props[CFCAReportOpenCardTask], "CFCAReportOpenCardTask")

  // 定时扫描前置机
  if (scanFrontEnd != null) {
    log.info("add cfcaRequestTask")
    masterSchedulerActor ! Job(cfcaRequestTask, Start, CronParser.parseCronExpression(scanFrontEnd))
  }

  // 定时扫描下行指令表
  if (scanDownQueue != null) {
    log.info("add cfcaExecuteTask")
    masterSchedulerActor ! Job(cfcaExecuteTask, Start, CronParser.parseCronExpression(scanDownQueue))
  }

  // 定时扫描上行指令表-查找反馈失败的报文
  if (scanUpQueue4Err != null) {
    log.info("add TFScanUpQueue4ErrTask")
    masterSchedulerActor ! Job(TFScanUpQueue4ErrTask, Start, CronParser.parseCronExpression(scanUpQueue4Err))
  }

  // 定时扫描动态查询任务表
  if (scanDynamicTask != null) {
    log.info("add TFScanDynamicTask")
    masterSchedulerActor ! Job(TFScanDynamicTask, Start, CronParser.parseCronExpression(scanDynamicTask))
  }

  // 定时解除动态查询任务
  if (scanDynamicFreeTask != null) {
    log.info("add TFScanDynamicFreeTask")
    masterSchedulerActor ! Job(TFScanDynamicFreeTask, Start, CronParser.parseCronExpression(scanDynamicFreeTask))
  }

  // 定时到下行指令表中找因为网络原因失败的任务 
  if (scanUpQueue4Err != null) {
    log.info("add cfcaExecuteErrorTask")
    masterSchedulerActor ! Job(cfcaExecuteErrorTask, Start, CronParser.parseCronExpression(cfcaExecuteErrorTaskTime))
  }

  // 定时扫描校验失败表，把数据插入到TDH对应表中
  if (scanVerFaild != null) {
    log.info("add TFScanVerFaild")
    masterSchedulerActor ! Job(TFScanVerFaild, Start, CronParser.parseCronExpression(scanVerFaild))
  }

  // 定时上报可疑事件
  if (reportExceptionTask != null) {
    log.info("add CFCAReportExceptionTask")
    masterSchedulerActor ! Job(CFCAReportExceptionTask, Start, CronParser.parseCronExpression(reportExceptionTask))
  }

  // 定时上报可疑账户
  if (reportDoubtAccTask != null) {
    log.info("add CFCAReportDoubtAccTask")
    masterSchedulerActor ! Job(CFCAReportDoubtAccTask, Start, CronParser.parseCronExpression(reportDoubtAccTask))
  }

  // 定时上报可疑开卡
  if (reportOpenCardTask != null) {
    log.info("add CFCAReportOpenCardTask")
    masterSchedulerActor ! Job(CFCAReportOpenCardTask, Start, CronParser.parseCronExpression(reportOpenCardTask))
  }
}