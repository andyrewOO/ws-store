package com.hfbank.actor.business.source.queue

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.QueueDAO
import com.hfbank.mode.AppQueryParam
import com.hfbank.mode.bean.SourceQueue
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceQueueSearchBiz


class QueueSearchWorker extends BizActorWorker with QueueDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start QueueSearchWorker")

    val pageResult = event.content match {
      case AppQueryParam(queryBean, page) => {
        
        val (pageContent,totalCounts) = queryBean match {
          case sourceQueue: SourceQueue => (this.search(sourceQueue, page),this.getTotal(sourceQueue))
        }
        log.info(s"Queue Query Result Size:${pageContent.size}, totals: ${totalCounts.head}")

        page.copy(result = pageContent, sum = totalCounts.head.getOrElse("TOTAL", "0").toInt)
      }
    }

    Some(TEvent(HfbkUtil.getUUID(), SourceQueueSearchBiz, pageResult, HfbkUtil.getTime(), Some(self)))
  }
}