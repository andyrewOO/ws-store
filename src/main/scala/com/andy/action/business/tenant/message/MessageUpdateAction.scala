package com.hfbank.action.business.tenant.message

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.actor.TenantMessageUpdateBiz
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantMessage
import com.hfbank.mode.UpdateResult
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class MessageUpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start MessageUpdateAction")
    
    val messageBean = getBean[TenantMessage](paramo("message"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantMessageUpdateBiz, messageBean, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }
  
  def receiveResponse: Receive = {
     case result: TEvent[_] => {
       result.content match {
        case r: SQLResponse => {
          r.count match {
            case 1 => respondJson(UpdateResult(true, "success"))
            case 0 => respondJson(UpdateResult(false, r.msg))//后台拼接的错误信息，反馈提示
          }
        }
        case _ => respondJson(UpdateResult(false, "fail"))
      }
     }
   }
}