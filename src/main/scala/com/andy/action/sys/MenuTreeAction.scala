package com.andy.action.sys

import xitrum.annotation.GET
import com.andy.actor.sys.MenuTreeService
import com.andy.action.AppAction
import com.andy.mode.AppQueryResult
import com.andy.action.BaseAppAction

/**
 * @author xuhaibin
 */

/**
 * 获取有效的树形JSON
 */
@GET("menutree/menutreeAll")
class MenuTreeQueryTreeAllByFlagAction extends BaseAppAction {
  def execute() {
    val list = MenuTreeService.findMenuTreeAll()
    respondJson(AppQueryResult("0", "success", list.size, list))
  }
}