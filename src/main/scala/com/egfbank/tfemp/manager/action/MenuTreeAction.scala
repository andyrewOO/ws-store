package com.egfbank.tfemp.manager.action

import xitrum.annotation.GET
import com.egfbank.tfemp.action.AppAction
import com.egfbank.tfemp.action.AppQueryResult
import com.egfbank.tfemp.manager.services.MenuTreeService

/**
 * @author xuhaibin
 */

/**
 * 获取有效的树形JSON
 */
@GET("menutree/menutreeAll")
class MenuTreeQueryTreeAllByFlagAction extends AppAction {
  def execute() {
    val list = MenuTreeService.findMenuTreeAll()
    respondJson(AppQueryResult("0", "success", list.size, list))
  }
}