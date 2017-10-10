package com.hfbank.actor.business.tenant.source

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.SourceDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.AppQueryParam
import com.hfbank.constant.SearchOperater
import com.hfbank.actor.TenantSourceVSearchBiz
import com.hfbank.mode.bean.TenantSourceV

/**
 * @author win7
 */
class SourceVSearchWorker extends BizActorWorker with SourceDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start SourceVSearchWorker")

    val response = event.content match {
      case AppQueryParam(queryBean, page) => {
        val (total, result) = queryBean match {
          case tenantSourceV: TenantSourceV => {
            (getTotal(tenantSourceV), searchV(tenantSourceV, page, SearchOperater.FUZZY))
          }
        }

        log.info(s"Query Total:${total.head.getOrElse("TOTAL", "0").toInt}")
        log.info(s"Query Result Size:${result.size}")

        page.copy(result = result, sum = total.head.getOrElse("TOTAL", "0").toInt)
      }
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantSourceVSearchBiz, response, HfbkUtil.getTime(), Some(self)))
  }
}