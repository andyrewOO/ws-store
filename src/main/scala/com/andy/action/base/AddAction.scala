package com.hfbank.action.base

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import xitrum.annotation.POST
import com.hfbank.constant.OperationUnit
import scala.reflect.runtime.universe
import com.hfbank.action.business.tenant.app.AppAddAction
import com.hfbank.action.business.tenant.partion.PartionAddAction
import com.hfbank.action.business.tenant.interfaces.InterfaceAddAction
import com.hfbank.action.business.tenant.message.MessageAddAction
import com.hfbank.action.business.tenant.source.SourceAddAction
import com.hfbank.action.business.tenant.user.TenantUserAddAction
import com.hfbank.action.business.source.queue.QueueAddAction
import com.hfbank.action.business.source.engine.EngineAddAction
import com.hfbank.action.business.source.queue.DBAddAction


/**
 * @author andy
 */
@POST("add/:unit")
class AddAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val bizScence = param("unit")
    bizScence match {
      case OperationUnit.TENANTUSER => forwardTo(classOf[TenantUserAddAction])
      case OperationUnit.APP        => forwardTo(classOf[AppAddAction])
      case OperationUnit.PARTION    => forwardTo(classOf[PartionAddAction])
      case OperationUnit.INTERFACE  => forwardTo(classOf[InterfaceAddAction])
      case OperationUnit.MESSAGE    => forwardTo(classOf[MessageAddAction])
      case OperationUnit.SOURCE     => forwardTo(classOf[SourceAddAction])
      case OperationUnit.QUEUE      => forwardTo(classOf[QueueAddAction])
      case OperationUnit.ENGINE     => forwardTo(classOf[EngineAddAction])
      case OperationUnit.DB         => forwardTo(classOf[DBAddAction])
    }
  }
}