package com.hfbank.action.business.tenant.app

import scala.reflect.runtime.universe
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jvalue2extractable
import org.json4s.string2JsonInput
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.TEvent
import akka.actor.actorRef2Scala
import xitrum.SkipCsrfCheck
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.mode.UpdateResult
import com.hfbank.actor.TenantAPPAddBiz
import com.hfbank.mode.SQLResponse

/**
 * @author andy
 */
class AppAddAction extends BaseAppAction with SkipCsrfCheck {

  def execute(): Unit = {
    log.info(s"Start UserAddAction")
    val appBean = getBean[TenantAPP](paramo("app"))
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantAPPAddBiz, appBean, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }

  def receiveResponse: Receive = {
    case tevent: TEvent[_] => {
      tevent.content match {
        case response: SQLResponse if (response.count == 1) => respondJson(UpdateResult(true, "success"))
        case response: SQLResponse if (response.count == 0) => respondJson(UpdateResult(false, response.msg))
      }
    }
  }

}