package com.hfbank.actor.business.tenant.user

import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.TenantUserDAO
import com.hfbank.mode.TEvent
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.bean.TenantUser
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantUserSearchBiz
import com.hfbank.constant.SearchOperater

/**
 * @author andy
 */
class TenantUserSearchWorker extends BizActorWorker with TenantUserDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start TenantUserSearchWorker")

    val pageResult = event.content match {
      case AppQueryParam(queryBean, page) => {
        val (total, queryResult) = queryBean match {
          case tenantuser: TenantUser =>
            (getTotal(tenantuser, SearchOperater.FUZZY), search(tenantuser, page, SearchOperater.FUZZY))
        }

        log.info(s"Query Total:${total.head.getOrElse("TOTAL", "0").toInt}")
        log.info(s"Query Result Size:${queryResult.size}")

        page.copy(result = queryResult, sum = total.head.getOrElse("TOTAL", "0").toInt)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), TenantUserSearchBiz, pageResult, HfbkUtil.getTime(), Some(self)))
  }
}