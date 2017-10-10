package com.hfbank.action.base

import scala.reflect.runtime.universe
import com.hfbank.action.BaseAppAction
import com.hfbank.constant.OperationUnit
import xitrum.SkipCsrfCheck
import xitrum.annotation.POST
import com.hfbank.action.business.tenant.message.MessageSearchAction
import com.hfbank.action.business.tenant.source.SourceSearchAction
import com.hfbank.action.business.tenant.interfaces.InterfaceSearchAction
import com.hfbank.action.business.tenant.app.AppSearchAction
import com.hfbank.action.business.tenant.partion.PartionSearchAction
import com.hfbank.action.business.tenant.user.TenantUserSearchAction
import com.hfbank.action.business.source.queue.QueueSearchAction
import com.hfbank.action.business.source.engine.EngineSearchAction
import com.hfbank.action.business.tenant.source.SourceVSearchAction
import com.hfbank.action.business.source.queue.DBSearchAction

/**
 * @author andy
 */
@POST("search/:unit")
class SearchAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    val bizScence = param("unit")
    bizScence match {
      case OperationUnit.TENANTUSER => forwardTo(classOf[TenantUserSearchAction])
      case OperationUnit.APP        => forwardTo(classOf[AppSearchAction])
      case OperationUnit.PARTION    => forwardTo(classOf[PartionSearchAction])
      case OperationUnit.INTERFACE  => forwardTo(classOf[InterfaceSearchAction])
      case OperationUnit.MESSAGE    => forwardTo(classOf[MessageSearchAction])
      case OperationUnit.SOURCE     => forwardTo(classOf[SourceSearchAction])
      case OperationUnit.SOURCEV     => forwardTo(classOf[SourceVSearchAction])
      case OperationUnit.QUEUE      => forwardTo(classOf[QueueSearchAction])
      case OperationUnit.ENGINE     => forwardTo(classOf[EngineSearchAction])
      case OperationUnit.DB     => forwardTo(classOf[DBSearchAction])
    }
  }
}
