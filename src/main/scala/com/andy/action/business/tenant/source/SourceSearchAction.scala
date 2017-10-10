package com.hfbank.action.business.tenant.source

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.TenantSourceSearchBiz
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.Page
import com.hfbank.mode.AppQueryResult

/**
 * @author win7
 */
class SourceSearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start SourceSearchAction")
    val sourceBean = getBean[TenantSource](paramo("source"))
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantSourceSearchBiz, sourceBean, HfbkUtil.getTime(), Some(self))
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