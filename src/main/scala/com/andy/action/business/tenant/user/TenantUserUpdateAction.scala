package com.hfbank.action.business.tenant.user

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.bean.TenantUser
import org.json4s.DefaultFormats
import com.hfbank.actor.TenantUserUpdateBiz
import com.hfbank.mode.TEvent
import com.hfbank.mode.AppQueryResult
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.UpdateResult

/**
 * @author andy
 */
class TenantUserUpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start TenantUserUpdateAction")
    val userBean = getBean[TenantUser](paramo("user"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantUserUpdateBiz, userBean, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }

  def receiveResponse: Receive = {
    case tevent: TEvent[Int] => {
      tevent.content match {
        case 1 => respondJson(UpdateResult(true, "success"))
        case 0 => respondJson(UpdateResult(false, "fail"))
      }
    }
  }
}