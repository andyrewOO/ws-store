package com.hfbank.actor.business.tenant.partion

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.PartionDAO
import com.hfbank.actor.TenantPartionUpdateBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantPartion
import com.hfbank.constant.BeanStatus

/**
 * @author andy
 */
class PartionUpdateWorker extends BizActorWorker with PartionDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start PartionUpdateWorker")
    
    val updateResult = event.content match {
      case tenantpartion: TenantPartion => {
        update(tenantpartion)
      }
      case _ => 0
    }

    Some(TEvent(HfbkUtil.getUUID(), TenantPartionUpdateBiz, updateResult, HfbkUtil.getTime(), Some(self)))
  }
}