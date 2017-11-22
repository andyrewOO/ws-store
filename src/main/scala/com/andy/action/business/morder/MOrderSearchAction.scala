package com.andy.action.business.morder

import com.andy.action.AppAction
import xitrum.SkipCsrfCheck
import com.andy.mode.TEvent
import com.andy.actor.MOrderSearchBiz
import com.andy.mode.bean.MOrder
import com.andy.mode.AppQueryParam
import com.andy.mode.Page

/**
 * @author andy
 */
class MOrderSearchAction extends AppAction with SkipCsrfCheck {

  def handle() = {
    log.info(s"Start ${BIZ_SCENE}")

    val objBean = paramo("mgoods") match {
      case Some(jsonString) => getBean[MOrder](jsonString)
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

    }
  }
}