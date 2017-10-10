package com.hfbank.actor.business.source.queue

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.QueueDAO
import com.hfbank.mode.bean.SourceQueue
import com.hfbank.actor.dao.source.QueueDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceQueueAddBiz


class QueueAddWorker extends BizActorWorker with QueueDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start QueueAddWorker")
    
    val getRes:Int = {
      event.content match {
        case insertObj:SourceQueue => {
          log.info(s"the insert obj: ${insertObj}")
          insert(insertObj)
        }
        case _ => {
          log.info("not into insertObj")
          -1
          //return -1 error
        }
      }
    }
        
    Some(TEvent(HfbkUtil.getUUID(), SourceQueueAddBiz, getRes, HfbkUtil.getTime(), Some(self)))
  }
}