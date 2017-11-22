package com.andy.action.business.mgoods

import com.andy.action.AppAction
import com.andy.actor.ActorHolder
import com.andy.mode.bean.MGoods
import xitrum.SkipCsrfCheck
import com.andy.util.HfbkUtil
import com.andy.actor.MGoodsAddBiz
import com.andy.mode.TEvent
import com.andy.mode.UpdateResult

/**
 * @author andy
 */
class MGoodsAddAction extends AppAction with SkipCsrfCheck {

  def handle() = {
    log.info("Start MGoodsAddAction")
    log.info(s"MGoods:${paramo("mgoods")}")

    val oBean = getBean[MGoods](paramo("mgoods"))
    Some(oBean)
  }

  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {
      content match {
        case u: UpdateResult =>
          log.info(s"Response to client")
          respondJson(u)
      }
    }
  }
}