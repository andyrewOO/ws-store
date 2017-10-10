package com.hfbank.action.business.tenant.interfaces

import scala.reflect.runtime.universe
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.ActorHolder
import com.hfbank.actor.TenantInterfaceSearchBiz
import com.hfbank.mode.TEvent
import com.hfbank.mode.bean.TenantInterface
import com.hfbank.util.HfbkUtil
import akka.actor.actorRef2Scala
import xitrum.SkipCsrfCheck
import com.hfbank.mode.Page
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.AppQueryResult

/**
 * @author win7
 */
class InterfaceSearchAction extends BaseAppAction with SkipCsrfCheck   {
  def execute(): Unit = {
    log.info(s"Start InterfaceSearchAction")
    val interfaceBean = getBean[TenantInterface](paramo("interfaces"))
    val page = getBean[Page](paramo("page"))
    val queryParem = AppQueryParam(interfaceBean, page)
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantInterfaceSearchBiz, queryParem, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }
  
  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {
      content match {
        case page: Page => {
          respondJson(AppQueryResult(true, "success", page.sum, page))
        }
      }
    }
  }
}