package com.hfbank.actor.business.source.db

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.DBDAO
import com.hfbank.mode.bean.SourceDB
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceDBUpdateBiz


class DBUpdateWorker extends BizActorWorker with DBDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start DBUpdateWorker")
    
    val getRes:Int = {
      event.content match {
        case updateObj:SourceDB => {
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
        
    Some(TEvent(HfbkUtil.getUUID(), SourceDBUpdateBiz, getRes, HfbkUtil.getTime(), Some(self)))
  }
}