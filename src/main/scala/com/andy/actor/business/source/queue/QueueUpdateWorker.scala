package com.hfbank.actor.business.source.queue

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.QueueDAO
import com.hfbank.mode.bean.SourceQueue
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceQueueUpdateBiz


class QueueUpdateWorker extends BizActorWorker with QueueDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start QueueUpdateWorker")
    
    val getRes:Int = {
      event.content match {
        case updateObj:SourceQueue => {
          log.info(s"the update obj: ${updateObj}")
          update(updateObj)
        }
        case _ => {
          log.info("not into updateObj")
          -1
          //return -1 error
        }
      }
    }
        
    Some(TEvent(HfbkUtil.getUUID(), SourceQueueUpdateBiz, getRes, HfbkUtil.getTime(), Some(self)))
  }
}