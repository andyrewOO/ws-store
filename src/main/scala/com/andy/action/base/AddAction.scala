package com.andy.action.base

import xitrum.SkipCsrfCheck
import com.andy.action.BaseAppAction
import xitrum.annotation.POST
import com.andy.constant.BeanUnit
import com.andy.action.business.mgoods.MGoodsAddAction
import com.andy.action.business.morder.MOrderAddAction
import com.andy.action.business.mpeople.MPeopleAddAction
import com.andy.action.business.mreaddr.MReAddrAddAction

/**
 * @author andy
 */
@POST("add/:unit")
class AddAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val bizScence = param("unit")
    bizScence match {
      case BeanUnit.MGOODS  => forwardTo(classOf[MGoodsAddAction])
      case BeanUnit.MORDER  => forwardTo(classOf[MOrderAddAction])
      case BeanUnit.MPEOPLE => forwardTo(classOf[MPeopleAddAction])
      case BeanUnit.MREADDR => forwardTo(classOf[MReAddrAddAction])
    }
  }
}