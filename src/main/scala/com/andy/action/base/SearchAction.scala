package com.andy.action.base

import scala.reflect.runtime.universe
import com.andy.action.BaseAppAction
import xitrum.SkipCsrfCheck
import xitrum.annotation.POST
import com.andy.constant.BeanUnit
import com.andy.action.business.morder.MOrderSearchAction
import com.andy.action.business.mreaddr.MReAddrSearchAction
import com.andy.action.business.mgoods.MGoodsSearchAction
import com.andy.action.business.mpeople.MPeopleSearchAction
import com.andy.actor.BizScene
import com.andy.actor.MReAddrAddBiz
import com.andy.actor.MOrderSearchBiz
import com.andy.actor.MPeopleSearchBiz
import com.andy.actor.MGoodsSearchBiz

/**
 * @author andy
 */
@POST("search/:unit")
class SearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val bizScence = param("unit")
    bizScence match {
      case BeanUnit.MGOODS  => forwardTo(classOf[MGoodsSearchAction])
      case BeanUnit.MORDER  => forwardTo(classOf[MOrderSearchAction])
      case BeanUnit.MPEOPLE => forwardTo(classOf[MPeopleSearchAction])
      case BeanUnit.MREADDR => forwardTo(classOf[MReAddrSearchAction])
    }
  }
}
