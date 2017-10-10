package com.hfbank.actor.business.tenant.partion

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.PartionDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantPartionAddBiz
import com.hfbank.mode.bean.TenantPartion
import com.hfbank.constant.BeanStatus

/**
 * @author andy
 */
class PartionAddWorker extends BizActorWorker with PartionDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start PartionAddWorker")

    val insertResult = event.content match {
      case tenantpartion: TenantPartion => {
        insertInto(tenantpartion.copy(status = BeanStatus.INI))
      }
      case _ => 0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantPartionAddBiz, insertResult, HfbkUtil.getTime(), Some(self)))
  }
}