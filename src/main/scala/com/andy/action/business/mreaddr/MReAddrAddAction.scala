package com.andy.action.business.mreaddr

import com.andy.action.AppAction
import xitrum.SkipCsrfCheck
import com.andy.mode.TEvent

/**
 * @author andy
 */
class MReAddrAddAction extends AppAction with SkipCsrfCheck {
  def handle() = {
    ???
  }
  def receiveResponse: Receive = {
    case TEvent(_, _, content, _, _) => {

    }
  }
}