package com.hfbank.actor.business.tenant.app

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.AppDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantAPPUpdateBiz
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.constant.BeanStatus

/**
 * @author andy
 */
class AppUpdateWorker extends BizActorWorker with AppDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start AppUpdateWorker")
    
    val updateResult = event.content match {
      case tenantapp: TenantAPP => {
        update(tenantapp)
      }
      case _ => 0
    }

    Some(TEvent(HfbkUtil.getUUID(), TenantAPPUpdateBiz, updateResult, HfbkUtil.getTime(), Some(self)))
  }
}