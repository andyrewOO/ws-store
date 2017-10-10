package com.hfbank.actor.business.tenant.message

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.MessageDAO
import com.hfbank.actor.TenantMessageSearchBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantMessage
import com.hfbank.mode.AppQueryParam

/**
 * @author win7
 */
class MessageSearchWorker extends BizActorWorker with MessageDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start MessageSearchWorker")
    
    val response = event.content match {
       case AppQueryParam(queryBean, page) => {
         val (total, result) = queryBean match {
           case tenantMessage: TenantMessage => {
          	 ((getTotal(tenantMessage), searchMessage(tenantMessage, page)))
           }
         }
         
         log.info(s"query result size is:${result.size}")
         page.copy(result = result, sum = total.head.getOrElse("TOTAL", "0").toInt)
       }
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantMessageSearchBiz, response, HfbkUtil.getTime(), Some(self)))
  }
}