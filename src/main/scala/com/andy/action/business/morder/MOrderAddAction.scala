package com.andy.action.business.morder

import com.andy.action.AppAction
import xitrum.SkipCsrfCheck
import com.andy.mode.TEvent

/**
 * @author andy
 */
class MOrderAddAction extends AppAction with SkipCsrfCheck {
  def handle() = {
    ???
  }
  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {

    }
  }
}