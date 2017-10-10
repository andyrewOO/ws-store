package com.hfbank.actor.business.source.engine

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.EngineDAO
import com.hfbank.mode.bean.SourceEngine
import com.hfbank.actor.dao.source.EngineDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceEngineAddBiz


class EngineAddWorker extends BizActorWorker with EngineDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start EngineAddWorker")
    
    val getRes:Int = {
      event.content match {
        case insertObj:SourceEngine => {
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
        
    Some(TEvent(HfbkUtil.getUUID(), SourceEngineAddBiz, getRes, HfbkUtil.getTime(), Some(self)))
  }
}