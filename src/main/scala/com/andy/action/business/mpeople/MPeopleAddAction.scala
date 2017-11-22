package com.andy.action.business.mpeople

import com.andy.action.AppAction
import xitrum.SkipCsrfCheck
import com.andy.mode.TEvent

/**
 * @author andy
 */
class MPeopleAddAction extends AppAction with SkipCsrfCheck {
  def handle() = {
    ???
  }
  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {

    }
  }
}