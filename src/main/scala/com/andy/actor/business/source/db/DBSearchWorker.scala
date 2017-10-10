package com.hfbank.actor.business.source.db

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.DBDAO
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.bean.SourceDB
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceDBSearchBiz


class DBSearchWorker extends BizActorWorker with DBDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start DBSearchWorker")

    val pageResult = event.content match {
      case AppQueryParam(queryBean, page) => {
        
        val (pageContent,totalCounts) = queryBean match {
          case sourceDB: SourceDB => (this.search(sourceDB, page),this.getTotal(sourceDB))
        }
        log.info(s"DB Query Result Size:${pageContent.size}, totals: ${totalCounts.head}")

        page.copy(result = pageContent, sum = totalCounts.head.getOrElse("TOTAL", "0").toInt)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), SourceDBSearchBiz, pageResult, HfbkUtil.getTime(), Some(self)))
  }
}