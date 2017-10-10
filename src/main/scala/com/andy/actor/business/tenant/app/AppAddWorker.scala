package com.hfbank.actor.business.tenant.app

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.AppDAO
import com.hfbank.actor.TenantAPPAddBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.constant.BeanStatus
import com.hfbank.mode.SQLResponse

/**
 * @author andy
 */
class AppAddWorker extends BizActorWorker with AppDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start AppAddWorker")

    var rtnMessage = ""
    val insertResult = event.content match {
      case tenantapp: TenantAPP => {
        try {
          insertInto(tenantapp.copy(status = BeanStatus.INI))
        } catch {
          case ex: RepetitionException =>
            log.info(s"RepetitionException:${ex}")
            rtnMessage = s"违反唯一约束:${ex.returnT}"
            0
        }
      }
      case _ => 0
    }

    val response = SQLResponse(insertResult, rtnMessage)
    Some(TEvent(HfbkUtil.getUUID(), TenantAPPAddBiz, response, HfbkUtil.getTime(), Some(self)))
  }
}