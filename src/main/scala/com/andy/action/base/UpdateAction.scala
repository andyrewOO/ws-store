package com.andy.action.base

import xitrum.SkipCsrfCheck
import com.andy.action.BaseAppAction
import xitrum.annotation.POST
import com.andy.constant.BeanUnit
import com.andy.action.business.mgoods.MGoodsUpdateAction
import com.andy.action.business.mpeople.MPeopleUpdateAction
import com.andy.action.business.morder.MOrderUpdateAction
import com.andy.action.business.mreaddr.MReAddrUpdateAction

/**
 * @author andy
 */
@POST("update/:unit")
class UpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val bizScence = param("unit")
    bizScence match {
      case BeanUnit.MGOODS  => forwardTo(classOf[MGoodsUpdateAction])
      case BeanUnit.MORDER  => forwardTo(classOf[MOrderUpdateAction])
      case BeanUnit.MPEOPLE => forwardTo(classOf[MPeopleUpdateAction])
      case BeanUnit.MREADDR => forwardTo(classOf[MReAddrUpdateAction])
    }
  }
}
