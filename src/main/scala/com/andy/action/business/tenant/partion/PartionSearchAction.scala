package com.hfbank.action.business.tenant.partion

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.bean.TenantPartion
import com.hfbank.mode.TEvent
import com.hfbank.mode.AppQueryResult
import com.hfbank.mode.AppQueryParam
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.Page
import com.hfbank.actor.TenantPartionSearchBiz

/**
 * @author win7
 */
class PartionSearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info("Start AppSearchAction")
    val appBean = getBean[TenantPartion](paramo("partion"))
    val page = getBean[Page](paramo("page"))
    val queryParem = AppQueryParam(appBean, page)

    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantPartionSearchBiz, queryParem, HfbkUtil.getTime(), Some(self))
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