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
import xitrum.SkipCsrfCheck

/**
 * @author andy
 */
class MGoodsSearchAction extends AppAction with SkipCsrfCheck {

  def handle() = {
    log.info(s"Start ${BIZ_SCENE}")

    val objBean = paramo("mgoods") match {
      case Some(jsonString) => getBean[MGoods](jsonString)
      case _                => null
    }
    val page = paramo("page") match {
      case Some(jsonString) => getBean[Page](jsonString)
      case _                => null
    }

    val queryParam = AppQueryParam(objBean, page)

    Some(queryParam)
  }

  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {
      content match {
        case page: Page => {
          respondJson(AppQueryResult("0", "success", page.sum, page))
        }
        case _ => {
          respondJson(AppQueryResult("-1", "actorResError", 0, ""))
        }
      }
    }
    case _ => {
      respondJson(AppQueryResult("-1", "actorResError", -1, ""))
    }
  }
}