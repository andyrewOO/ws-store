package com.hfbank.action.business.tenant.user

import org.json4s._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.bean.TenantUser
import xitrum.SkipCsrfCheck
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.TEvent
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantUserSearchBiz
import com.hfbank.mode.Page
import com.hfbank.mode.AppQueryParam
import scala.xml.Elem
import com.hfbank.mode.AppQueryResult

/**
 * @author andy
 */
class TenantUserSearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info("Start TenantUserSearchAction")
    val userBean = getBean[TenantUser](paramo("user"))
    val page = getBean[Page](paramo("page"))
    val queryParem = AppQueryParam(userBean, page)

    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantUserSearchBiz, queryParem, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }

  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {
      content match {
        case page: Page => {
          respondJson(AppQueryResult(true, "success", page.sum, page))
        }
        case _ => {
          respondJson(AppQueryResult(false, "fail", -1, null))
        }
      }
    }
  }
}