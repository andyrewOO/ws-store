package com.hfbank.actor.business.tenant.source

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.SourceDAO
import com.hfbank.actor.TenantSourceSearchBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.AppQueryParam
import com.hfbank.constant.SearchOperater
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.Page

/**
 * @author win7
 */
class SourceSearchWorker extends BizActorWorker with SourceDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start SourceSearchWorker")

    val response = event.content match {
      case tenantSource: TenantSource => {
        val result = searchSource(tenantSource)

        log.info(s"Query Result Size:${result.size}")
        Page(1, result.size, result.size, result)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), TenantSourceSearchBiz, response, HfbkUtil.getTime(), Some(self)))
  }
}