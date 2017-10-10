package com.hfbank.actor.business.tenant.user

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.TenantUserDAO
import com.hfbank.mode.bean.TenantUser
import com.hfbank.constant.BeanStatus
import com.hfbank.actor.TenantUserUpdateBiz
import com.hfbank.util.HfbkUtil

/**
 * @author andy
 */
class TenantUserUpdateWorker extends BizActorWorker with TenantUserDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start TenantUserUpdateWorker")
    
    val updateResult = event.content match {
      case tenantuser: TenantUser => {
        update(tenantuser)
      }
      case _ => 0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantUserUpdateBiz, updateResult, HfbkUtil.getTime(), Some(self)))
  }
}