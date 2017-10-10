package com.hfbank.actor.business.source.engine

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.EngineDAO
import com.hfbank.mode.bean.SourceEngine
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceEngineUpdateBiz


class EngineUpdateWorker extends BizActorWorker with EngineDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start EngineUpdateWorker")
    
    val getRes:Int = {
      event.content match {
        case updateObj:SourceEngine => {
          log.info(s"the update obj: ${updateObj}")
          update(updateObj)
        }
        case _ => {
          log.info("not into insertObj")
          -1
          //return -1 error
        }
      }
    }
        
    Some(TEvent(HfbkUtil.getUUID(), SourceEngineUpdateBiz, getRes, HfbkUtil.getTime(), Some(self)))
  }
}