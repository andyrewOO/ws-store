package com.hfbank.actor.business.tenant.partion

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.tenant.PartionDAO
import com.hfbank.mode.bean.TenantPartion
import com.hfbank.actor.TenantPartionAddBiz
import com.hfbank.mode.AppQueryParam
import com.hfbank.util.HfbkUtil
import com.hfbank.constant.SearchOperater

/**
 * @author andy
 */
class PartionSearchWorker extends BizActorWorker with PartionDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start PartionSearchWorker")

    val pageResult = event.content match {
      case AppQueryParam(queryBean, page) => {
        val (total, queryResult) = queryBean match {
          case tenantuser: TenantPartion =>
            (getTotal(tenantuser, SearchOperater.FUZZY), search(tenantuser, page, SearchOperater.FUZZY))
        }

        log.info(s"Query Total:${total.head.getOrElse("TOTAL", "0").toInt}")
        log.info(s"Query Result Size:${queryResult.size}")

        page.copy(result = queryResult, sum = total.head.getOrElse("TOTAL", "0").toInt)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), TenantPartionAddBiz, pageResult, HfbkUtil.getTime(), Some(self)))
  }
}