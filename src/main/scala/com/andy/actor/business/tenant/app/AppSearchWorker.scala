package com.hfbank.actor.business.tenant.app

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.AppDAO
import com.hfbank.mode.AppQueryParam
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantUserSearchBiz
import com.hfbank.mode.bean.TenantAPP
import com.hfbank.actor.TenantAPPSearchBiz
import com.hfbank.constant.SearchOperater

/**
 * @author andy
 */
class AppSearchWorker extends BizActorWorker with AppDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start AppSearchWorker")

    val pageResult = event.content match {
      case AppQueryParam(queryBean, page) => {
        val (total, queryResult) = queryBean match {
          case tenantapp: TenantAPP => 
            (getTotal(tenantapp,  SearchOperater.FUZZY), search(tenantapp, page,  SearchOperater.FUZZY))
        }

        log.info(s"Query Total:${total.head.getOrElse("TOTAL", "0").toInt}")
        log.info(s"Query Result Size:${queryResult.size}")

        page.copy(result = queryResult, sum = total.head.getOrElse("TOTAL", "0").toInt)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), TenantAPPSearchBiz, pageResult, HfbkUtil.getTime(), Some(self)))
  }
}