package com.egfbank.tfemp.actor.worker

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Cancellable

/**
 * 定时类worker,暂时缺少timeout处理
 * @author huxp
 */
abstract class SchedulerWorker extends DefaultWorker {


  /**
   * 使用become的切换，来保证
   */
  override def receive = {
    case Start => {
      start()
    }
    case _ =>
  }

  def pause: Receive = {
    case Continue => context.become(receive)
    case _        =>
  }

  /**
   * 定时任务入口方法
   */
  def start()
}

case object Start

case object Continue