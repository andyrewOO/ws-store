package com.hfbank.actor.business.tenant.interfaces

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.InterfaceDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantInterfaceSearchBiz
import com.hfbank.mode.bean.TenantInterface
import com.hfbank.mode.AppQueryParam

/**
 * @author win7
 */
class InterfaceSearchWorker extends BizActorWorker with InterfaceDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start InterfaceSearchWorker")
    
    val response = event.content match {
      case AppQueryParam(queryBean, page) => {
        val (total, result) = queryBean match {
          case tenantInterface: TenantInterface => {
            (getTotal(tenantInterface), searchInterface(tenantInterface, page))
          }
        }
        
        log.info(s"query result size is:${result.size}")
        
        page.copy(result = result, sum = total.head.getOrElse("TOTAL", "0").toInt)
      }
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantInterfaceSearchBiz, response, HfbkUtil.getTime(), Some(self)))
  }
}