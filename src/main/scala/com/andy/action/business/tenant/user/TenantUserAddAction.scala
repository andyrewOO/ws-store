package com.hfbank.action.business.tenant.user

import scala.reflect.runtime.universe
import org.json4s.jackson.JsonMethods._
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.ActorHolder
import com.hfbank.actor.TenantUserAddBiz
import com.hfbank.mode.TEvent
import com.hfbank.mode.bean.TenantUser
import com.hfbank.util.HfbkUtil
import akka.actor.actorRef2Scala
import xitrum.SkipCsrfCheck
import com.hfbank.mode.UpdateResult

/**
 * @author andy
 */
class TenantUserAddAction extends BaseAppAction with SkipCsrfCheck {

  def execute(): Unit = {
    log.info(s"Start UserAddAction")
    val userBean = getBean[TenantUser](paramo("user"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantUserAddBiz, userBean, HfbkUtil.getTime(), Some(self))
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