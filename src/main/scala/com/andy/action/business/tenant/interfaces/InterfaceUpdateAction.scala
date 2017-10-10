package com.hfbank.action.business.tenant.interfaces

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantInterfaceUpdateBiz
import com.hfbank.mode.bean.TenantInterface
import com.hfbank.mode.UpdateResult
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class InterfaceUpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start InterfaceUpdateAction")
    
    val interfaceBean = getBean[TenantInterface](paramo("interfaces"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantInterfaceUpdateBiz, interfaceBean, HfbkUtil.getTime(), Some(self))
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