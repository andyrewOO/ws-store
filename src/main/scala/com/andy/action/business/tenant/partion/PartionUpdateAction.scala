package com.hfbank.action.business.tenant.partion

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.mode.bean.TenantPartion
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantPartionUpdateBiz
import com.hfbank.mode.UpdateResult

/**
 * @author win7
 */
class PartionUpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
     log.info(s"Start PartionUpdateAction")
    val partionBean = getBean[TenantPartion](paramo("partion"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantPartionUpdateBiz, partionBean, HfbkUtil.getTime(), Some(self))
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