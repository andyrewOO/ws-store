package com.hfbank.actor.business.tenant.user

import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.TenantUserDAO
import com.hfbank.mode.TEvent
import com.hfbank.mode.bean.TenantUser
import com.hfbank.constant.BeanStatus
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantUserAddBiz

/**
 * @author andy
 */
class TenantUserAddWorker extends BizActorWorker with TenantUserDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start TenantUserAddWorker")

    val insertResult = event.content match {
      case tenantuser: TenantUser => {
        insertInto(tenantuser.copy(status = BeanStatus.INI))
      }
      case _ => 0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantUserAddBiz, insertResult, HfbkUtil.getTime(), Some(self)))
  }
}