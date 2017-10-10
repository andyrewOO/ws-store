package com.hfbank.action.business.tenant.partion

import scala.reflect.runtime.universe
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jvalue2extractable
import org.json4s.string2JsonInput
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.TEvent
import com.hfbank.util.HfbkUtil
import akka.actor.actorRef2Scala
import xitrum.SkipCsrfCheck
import com.hfbank.mode.bean.TenantPartion
import com.hfbank.mode.UpdateResult
import com.hfbank.actor.TenantPartionAddBiz

/**
 * @author andy
 */
class PartionAddAction extends BaseAppAction with SkipCsrfCheck {

  def execute(): Unit = {
    log.info(s"Start UserAddAction")
    val partionBean = getBean[TenantPartion](paramo("partion"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantPartionAddBiz, partionBean, HfbkUtil.getTime(), Some(self))
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