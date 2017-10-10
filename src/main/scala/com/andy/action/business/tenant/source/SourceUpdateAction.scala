package com.hfbank.action.business.tenant.source

import org.json4s._
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._
import com.hfbank.action.BaseAppAction
import com.hfbank.actor.ActorHolder
import com.hfbank.actor.TenantSourceUpdateBiz
import com.hfbank.mode.TEvent
import com.hfbank.mode.UpdateResult
import com.hfbank.mode.bean.TenantSource
import com.hfbank.util.HfbkUtil
import xitrum.SkipCsrfCheck
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class SourceUpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start SourceUpdateAction")

    val updateBeans = paramo("update") match {
      case Some(updates) => parse(updates)
      case x             => parse("{}")
    }

    val addBeans = paramo("add") match {
      case Some(adds) => parse(adds)
      case x          => parse("{}")
    }

    val updatelist = for (item <- updateBeans.children) yield {
      val beanStr = item.asInstanceOf[JValue]
      getBean[TenantSource](beanStr)
    }

    val addlist = for (item <- addBeans.children) yield {
      val beanStr = item.asInstanceOf[JValue]
      getBean[TenantSource](beanStr)
    }

    val param = Map(
      "update" -> updatelist,
      "add" -> addlist)

    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantSourceUpdateBiz, param, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }

  def receiveResponse: Receive = {
    case result: TEvent[_] => {
      result.content match {
        case response: SQLResponse if (response.count == 1) => respondJson(UpdateResult(true, "success"))
        case response: SQLResponse if (response.count == 0) => respondJson(UpdateResult(false, response.msg))
      }
    }
  }
}