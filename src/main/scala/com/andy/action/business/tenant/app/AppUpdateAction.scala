package com.hfbank.action.business.tenant.app

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.actor.TenantAPPUpdateBiz
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.mode.UpdateResult

/**
 * @author win7
 */
class AppUpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start TenantUserUpdateAction")
    val appBean = getBean[TenantAPP](paramo("app"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantAPPUpdateBiz, appBean, HfbkUtil.getTime(), Some(self))
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