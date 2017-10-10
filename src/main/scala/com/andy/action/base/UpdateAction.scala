package com.hfbank.action.base

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import xitrum.annotation.POST
import com.hfbank.constant.OperationUnit
import scala.reflect.runtime.universe
import com.hfbank.action.business.tenant.app.AppUpdateAction
import com.hfbank.action.business.tenant.source.SourceUpdateAction
import com.hfbank.action.business.tenant.interfaces.InterfaceUpdateAction
import com.hfbank.action.business.tenant.partion.PartionUpdateAction
import com.hfbank.action.business.tenant.message.MessageUpdateAction
import com.hfbank.action.business.tenant.user.TenantUserUpdateAction
import com.hfbank.action.business.source.queue.QueueUpdateAction
import com.hfbank.action.business.source.engine.EngineUpdateAction
import com.hfbank.action.business.source.queue.DBUpdateAction

/**
 * @author andy
 */
@POST("update/:unit")
class UpdateAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val bizScence = param("unit")
    bizScence match {
      case OperationUnit.TENANTUSER => forwardTo(classOf[TenantUserUpdateAction])
      case OperationUnit.APP        => forwardTo(classOf[AppUpdateAction])
      case OperationUnit.PARTION    => forwardTo(classOf[PartionUpdateAction])
      case OperationUnit.INTERFACE  => forwardTo(classOf[InterfaceUpdateAction])
      case OperationUnit.MESSAGE    => forwardTo(classOf[MessageUpdateAction])
      case OperationUnit.SOURCE     => forwardTo(classOf[SourceUpdateAction])
      case OperationUnit.QUEUE      => forwardTo(classOf[QueueUpdateAction])
      case OperationUnit.ENGINE     => forwardTo(classOf[EngineUpdateAction])
      case OperationUnit.DB         => forwardTo(classOf[DBUpdateAction])
    }
  }
}
