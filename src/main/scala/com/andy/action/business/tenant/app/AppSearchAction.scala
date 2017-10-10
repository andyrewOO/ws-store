package com.hfbank.action.business.tenant.app

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.mode.AppQueryResult
import com.hfbank.mode.Page
import com.hfbank.mode.AppQueryParam
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.actor.TenantUserSearchBiz
import com.hfbank.actor.TenantAPPSearchBiz

/**
 * @author win7
 */
class AppSearchAction extends BaseAppAction with SkipCsrfCheck  {
  def execute(): Unit = {
     log.info("Start AppSearchAction")
    val appBean = getBean[TenantAPP](paramo("app"))
    val page = getBean[Page](paramo("page"))
    val queryParem = AppQueryParam(appBean, page)

    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantAPPSearchBiz, queryParem, HfbkUtil.getTime(), Some(self))
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