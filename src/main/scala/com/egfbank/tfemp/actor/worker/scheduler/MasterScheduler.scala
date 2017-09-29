package com.egfbank.tfemp.actor.worker.scheduler

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import akka.actor.Actor
import akka.actor.Cancellable
import xitrum.Log

/**
 * @author XuHaibin
 */
class MasterScheduler extends Actor with Log{

  context.system.scheduler.schedule(0 seconds, 1 seconds, self, GrepJobs)

  def receive = {

    //定时扫描定时任务列表
    case GrepJobs => {
      val jobs = grepNextBatch()
      //如果有任务需要执行，发送给任务执行者
      if (!jobs.isEmpty) {
        jobs.map { job =>
          job.jobExecuter ! job.message
        }
      }
    }

    //收到job时向任务管理列表添加任务事项
    case job: Job => {
      addScheduleJob(job)
      println("添加了一个任务,目前的任务数为：" + MasterScheduler.allExecuteJobs.size)
      println("该任务计划执行时间：" + job.jobSchedule.nextRun(System.currentTimeMillis()) + "后")
    }

    case str: String => {
      println(str)
    }
  }

  /**
   * @param job
   * @return
   */
  private def addScheduleJob(job: Job) = {
    MasterScheduler.allExecuteJobs += job
  }

  /**
   * @return
   */
  private def grepNextBatch(): List[Job] = {
    MasterScheduler.allExecuteJobs.filter { job =>
      job.jobSchedule match {
        case cronJobSchedule: CronJobSchedule => {
          val nextExecuteTime = cronJobSchedule.nextRun(System.currentTimeMillis())
          nextExecuteTime match {
            case Some(time: Long) => {
              time < 1000
            }
            case _ => false
          }
        }
        //一次性任务
        case onceSchedule: OnceSchedule => {
          val t = onceSchedule.firstRun(System.currentTimeMillis())
          t < 1000 && t > 0
        }
        case _ => false
      }
    }.toList
  }
}

object MasterScheduler {
  val allExecuteJobs = new ListBuffer[Job]
  val nextExecuteJobs = new ListBuffer[Job]
  var cancel: Cancellable = _

}

case class GrepJobs()