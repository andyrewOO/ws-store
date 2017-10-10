package com.hfbank.action.business.tenant.message

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantMessageSearchBiz
import com.hfbank.mode.bean.TenantMessage
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.Page
import com.hfbank.mode.AppQueryResult

/**
 * @author win7
 */
class MessageSearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start MessageSearchAction")
    val messageBean = getBean[TenantMessage](paramo("message"))
    val page = getBean[Page](paramo("page"))
    val queryParem = AppQueryParam(messageBean, page)
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantMessageSearchBiz, queryParem, HfbkUtil.getTime(), Some(self))
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