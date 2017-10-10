package com.hfbank.actor

import akka.actor.Actor
import akka.actor.Props
import xitrum.Log
import com.hfbank.mode.TEvent
import akka.actor.actorRef2Scala
import com.hfbank.actor.business.tenant.user.TenantUserAddWorker
import com.hfbank.actor.business.tenant.user.TenantUserUpdateWorker
import com.hfbank.actor.business.tenant.user.TenantUserUpdateWorker
import com.hfbank.actor.business.tenant.user.TenantUserSearchWorker
import com.hfbank.actor.business.tenant.message.MessageAddWorker
import com.hfbank.actor.business.tenant.message.MessageSearchWorker
import com.hfbank.actor.business.tenant.message.MessageUpdateWorker
import com.hfbank.actor.business.tenant.interfaces.InterfaceAddWorker
import com.hfbank.actor.business.tenant.interfaces.InterfaceSearchWorker
import com.hfbank.actor.business.tenant.interfaces.InterfaceUpdateWorker
import com.hfbank.actor.business.tenant.source.SourceAddWorker
import com.hfbank.actor.business.tenant.source.SourceSearchWorker
import com.hfbank.actor.business.tenant.source.SourceUpdateWorker
import com.hfbank.actor.business.tenant.source.SourceVSearchWorker
import com.hfbank.actor.business.tenant.partion.PartionUpdateWorker
import com.hfbank.actor.business.tenant.app.AppUpdateWorker
import com.hfbank.actor.business.tenant.app.AppAddWorker
import com.hfbank.actor.business.tenant.partion.PartionAddWorker
import com.hfbank.actor.business.tenant.app.AppSearchWorker
import com.hfbank.actor.business.tenant.partion.PartionSearchWorker
import com.hfbank.actor.business.source.engine.EngineAddWorker
import com.hfbank.actor.business.source.db.DBSearchWorker
import com.hfbank.actor.business.source.queue.QueueAddWorker
import com.hfbank.actor.business.source.engine.EngineUpdateWorker
import com.hfbank.actor.business.source.engine.EngineSearchWorker
import com.hfbank.actor.business.source.queue.QueueSearchWorker
import com.hfbank.actor.business.source.queue.QueueUpdateWorker
import com.hfbank.actor.business.source.db.DBAddWorker
import com.hfbank.actor.business.source.db.DBUpdateWorker

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
        //租户-用户新增
        case TenantUserAddBiz => {
          context.actorOf(Props[TenantUserAddWorker]) ! event
        }
        //租户-用户查询
        case TenantUserSearchBiz => {
          context.actorOf(Props[TenantUserSearchWorker]) ! event
        }
        //租户-用户更新
        case TenantUserUpdateBiz => {
          context.actorOf(Props[TenantUserUpdateWorker]) ! event
        }
        //租户-应用新增
        case TenantAPPAddBiz => {
          context.actorOf(Props[AppAddWorker]) ! event
        }
        //租户-应用查询
        case TenantAPPSearchBiz => {
          context.actorOf(Props[AppSearchWorker]) ! event
        }
        //租户-应用更新
        case TenantAPPUpdateBiz => {
          context.actorOf(Props[AppUpdateWorker]) ! event
        }
        //租户-分区新增
        case TenantPartionAddBiz => {
          context.actorOf(Props[PartionAddWorker]) ! event
        }
        //租户-分区查询
        case TenantPartionSearchBiz => {
          context.actorOf(Props[PartionSearchWorker]) ! event
        }
        //租户-分区更新
        case TenantPartionUpdateBiz => {
          context.actorOf(Props[PartionUpdateWorker]) ! event
        }
        //租户-报文新增
        case TenantMessageAddBiz => {
          context.actorOf(Props[MessageAddWorker]) ! event
        }
        //租户-报文查询
        case TenantMessageSearchBiz => {
          context.actorOf(Props[MessageSearchWorker]) ! event
        }
        //租户-报文更新
        case TenantMessageUpdateBiz => {
          context.actorOf(Props[MessageUpdateWorker]) ! event
        }
        //租户-接口新增
        case TenantInterfaceAddBiz => {
          context.actorOf(Props[InterfaceAddWorker]) ! event
        }
        //租户-接口查询
        case TenantInterfaceSearchBiz => {
          context.actorOf(Props[InterfaceSearchWorker]) ! event
        }
        //租户-接口更新
        case TenantInterfaceUpdateBiz => {
          context.actorOf(Props[InterfaceUpdateWorker]) ! event
        }
        //租户-资源新增
        case TenantSourceAddBiz => {
          context.actorOf(Props[SourceAddWorker]) ! event
        }
        //租户-资源查询
        case TenantSourceSearchBiz => {
          context.actorOf(Props[SourceSearchWorker]) ! event
        }
        //租户-资源视图查询
        case TenantSourceVSearchBiz => {
        	context.actorOf(Props[SourceVSearchWorker]) ! event
        }
        //租户-资源更新
        case TenantSourceUpdateBiz => {
          context.actorOf(Props[SourceUpdateWorker]) ! event
        }
        //资源-数据库新增
        case SourceDBAddBiz => {
          context.actorOf(Props[DBAddWorker]) ! event
        }
        //资源-数据库查询
        case SourceDBSearchBiz => {
          context.actorOf(Props[DBSearchWorker]) ! event
        }
        //资源-数据库更新
        case SourceDBUpdateBiz => {
          context.actorOf(Props[DBUpdateWorker]) ! event
        }
        //资源-队列新增
        case SourceQueueAddBiz => {
          context.actorOf(Props[QueueAddWorker]) ! event
        }
        //资源-队列查询
        case SourceQueueSearchBiz => {
          context.actorOf(Props[QueueSearchWorker]) ! event
        }
        //资源-队列更新
        case SourceQueueUpdateBiz => {
          context.actorOf(Props[QueueUpdateWorker]) ! event
        }
        //资源-引擎新增
        case SourceEngineAddBiz => {
          context.actorOf(Props[EngineAddWorker]) ! event
        }
        //资源-引擎查询
        case SourceEngineSearchBiz => {
          context.actorOf(Props[EngineSearchWorker]) ! event
        }
        //资源-引擎更新
        case SourceEngineUpdateBiz => {
          context.actorOf(Props[EngineUpdateWorker]) ! event
        }
      }
    }
    case _ =>

  }

}
