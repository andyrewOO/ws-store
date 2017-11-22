package com.andy.actor

import com.andy.actor.business.mgoods.MGoodsAddWorker
import com.andy.actor.business.mgoods.MGoodsSearchWorker
import com.andy.actor.business.mgoods.MGoodsUpdateWorker
import com.andy.actor.business.morder.MOrderAddWorker
import com.andy.actor.business.morder.MOrderSearchWorker
import com.andy.actor.business.morder.MOrderUpdateWorker
import com.andy.actor.business.mpeople.MPeopleAddWorker
import com.andy.actor.business.mpeople.MPeopleSearchWorker
import com.andy.actor.business.mpeople.MPeopleUpdateWorker
import com.andy.actor.business.mreaddr.MReAddrAddWorker
import com.andy.actor.business.mreaddr.MReAddrSearchWorker
import com.andy.actor.business.mreaddr.MReAddrUpdateWorker
import com.andy.mode.TEvent

import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import xitrum.Log

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
      log.info("To match case,tradeType: " + event.bizScene)
      event.bizScene match {
        case BaseBiz          => log.error("Miss BizScene")
        // 商品
        case MGoodsSearchBiz  => context.actorOf(Props[MGoodsSearchWorker]) ! event
        case MGoodsAddBiz     => context.actorOf(Props[MGoodsAddWorker]) ! event
        case MGoodsUpdateBiz  => context.actorOf(Props[MGoodsUpdateWorker]) ! event
        // 订单
        case MOrderAddBiz     => context.actorOf(Props[MOrderAddWorker]) ! event
        case MOrderSearchBiz  => context.actorOf(Props[MOrderSearchWorker]) ! event
        case MOrderUpdateBiz  => context.actorOf(Props[MOrderUpdateWorker]) ! event
        // 客户
        case MPeopleAddBiz    => context.actorOf(Props[MPeopleAddWorker]) ! event
        case MPeopleSearchBiz => context.actorOf(Props[MPeopleSearchWorker]) ! event
        case MPeopleUpdateBiz => context.actorOf(Props[MPeopleUpdateWorker]) ! event
        // 收货地址
        case MReAddrAddBiz    => context.actorOf(Props[MReAddrAddWorker]) ! event
        case MReAddrSearchBiz => context.actorOf(Props[MReAddrSearchWorker]) ! event
        case MReAddrUpdateBiz => context.actorOf(Props[MReAddrUpdateWorker]) ! event
      }
    }
    case _ =>

  }

}
