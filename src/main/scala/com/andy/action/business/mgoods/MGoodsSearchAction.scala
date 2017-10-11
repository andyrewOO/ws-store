package com.andy.action.business.mgoods

import com.andy.action.AppAction
import com.andy.mode.bean.MGoods
import com.andy.mode.Page
import com.andy.mode.AppQueryParam
import com.andy.actor.ActorHolder
import com.andy.mode.TEvent
import com.andy.util.HfbkUtil
import com.andy.mode.AppQueryResult
import com.andy.actor.MGoodsSearchBiz

/**
 * @author andy
 */
class MGoodsSearchAction extends AppAction {
  def execute(): Unit = {
    log.info(s"Start MGoodsSearchAction")

    val objBean = paramo("mgoods") match {
      case Some(jsonString) => getBean[MGoods](jsonString)
      case _                => null
    }
    val page = paramo("page") match {
      case Some(jsonString) => getBean[Page](jsonString)
      case _                => null
    }

    val queryParam = AppQueryParam(objBean, page)

    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), MGoodsSearchBiz, queryParam, HfbkUtil.getTime(), Some(self))
    context.become(this.rst)
  }

  def rst: Receive = {
    case TEvent(_, _, content, _, _) => {
      content match {
        case page: Page => {
          respondJson(AppQueryResult(true, "success", page.sum, page))
        }
        case _ => {
          respondJson(AppQueryResult(false, "actorResError", 0, ""))
        }
      }
    }
    case _ => {
      respondJson(AppQueryResult(false, "actorResError", -1, ""))
    }
  }
}