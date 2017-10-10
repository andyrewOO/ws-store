package com.hfbank.actor.business.source.engine

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.EngineDAO
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.bean.SourceEngine
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceEngineSearchBiz


class EngineSearchWorker extends BizActorWorker with EngineDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start EngineSearchWorker")

    val pageResult = event.content match {
      case AppQueryParam(queryBean, page) => {
        
        val (pageContent,totalCounts) = queryBean match {
          case sourceEngine: SourceEngine => (this.search(sourceEngine, page),this.getTotal(sourceEngine))
        }
        log.info(s"Engine Query Result Size:${pageContent.size}, totals: ${totalCounts.head}")

        page.copy(result = pageContent, sum = totalCounts.head.getOrElse("TOTAL", "0").toInt)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), SourceEngineSearchBiz, pageResult, HfbkUtil.getTime(), Some(self)))
  }
}