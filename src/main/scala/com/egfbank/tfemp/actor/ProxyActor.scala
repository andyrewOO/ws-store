package com.egfbank.tfemp.actor

import akka.actor.Actor
import xitrum.Log
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.constant.CFCATradeCode
import akka.actor.Props
import com.egfbank.tfemp.actor.worker.module._

/**
 * Actor流程调用代理类
 */
class ProxyActor extends Actor with Log {

  /**
   * 将发送方改为自已
   */
  def changeSendertoSelf(event: TEvent[_]) = event.copy(tsender = Some(self))

  def receive = {
    case event: TEvent[_] => {
      log.info("To match case,tradeType: " + event.tradeType)
      event.tradeType match {
        //单要素查验
        case VerifyDoubtSingle => {
          context.actorOf(Props[VerifyDoubtSingleWorker]) ! event
        }
        //双要素查验
        case VerifyDoubtMulti => {
          context.actorOf(Props[VerifyDoubtMultiWorker]) ! event
        }
        //案件举报
        case ReportCase => {
          context.actorOf(Props[ReportCaseWorker]) ! event
        }
        //案件举报反馈
        case ReportCaseResult => {
          context.actorOf(Props[ReportCaseResultWorker]) ! event
        }
        //可疑名单上报(异常开卡)
        case ReportDoubtCard => {
          context.actorOf(Props[ReportDoubtCardWorker]) ! event
        }

        //可疑名单上报(涉案帐户)
        case ReportDoubtAcc => {
          context.actorOf(Props[ReportDoubtAccWorker]) ! event
        }

        // 可疑名单上报(异常事件)
        case ReportException =>
          {
            context.actorOf(Props[ReportExceptionWorker]) ! event
          }
        case ReportExceptionAction =>
          {
            context.actorOf(Props[ReportExceptionActionWorker]) ! event
          }
        //CFCA下发指令 如下
        //止付
        case StopPay =>
          { context.actorOf(Props[StopPayWorker]) ! event }

        // 止付延期
        case StopPayDelay =>
          { context.actorOf(Props[StopPayDelayWorker]) ! event }

        // 止付解除
        case StopPayFree =>
          { context.actorOf(Props[StopPayFreeWorker]) ! event }

        // 冻结
        case FreezePay =>
          { context.actorOf(Props[FreezePayWorker]) ! event }

        //冻结延期
        case FreezePayDelay =>
          { context.actorOf(Props[FreezePayDelayWorker]) ! event }

        // 冻结解除
        case FreezePayFree =>
          { context.actorOf(Props[FreezePayFreeWorker]) ! event }

        // 交易明细查询
        case QueryTransDetail =>
          { context.actorOf(Props[QueryTransDetailWorker]) ! event }

        // 持卡主体查询
        case QueryCardOwner =>
          { context.actorOf(Props[QueryCardOwnerWorker]) ! event }

        // 帐户动态查询
        case QueryAccMonitor =>
          { context.actorOf(Props[QueryAccMonitorWorker]) ! event }

        // 帐户动态查询解除
        case QueryAccMonitorFree =>
          { context.actorOf(Props[QueryAccMonitorFreeWorker]) ! event }

        // 全帐户查询
        case QueryAccAllInfo =>
          { context.actorOf(Props[QueryAccAllInfoWorker]) ! event }

        // 前置机接受反馈失败
        case CFCAResponseError =>
          { context.actorOf(Props[CFCAResponseErrorWorker]) ! event }
      }
    }
    case _ =>

  }

}
