package com.hfbank.action.business.tenant.source

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.Page
import com.hfbank.mode.AppQueryResult
import com.hfbank.mode.bean.TenantSourceV
import com.hfbank.actor.TenantSourceVSearchBiz

/**
 * @author win7
 */
class SourceVSearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start SourceVSearchAction")
    val sourceBean = getBean[TenantSourceV](paramo("sourcev"))
    val page = getBean[Page](paramo("page"))
    val queryParem = AppQueryParam(sourceBean, page)
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantSourceVSearchBiz, queryParem, HfbkUtil.getTime(), Some(self))
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