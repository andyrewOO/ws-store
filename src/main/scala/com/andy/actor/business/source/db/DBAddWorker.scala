package com.hfbank.actor.business.source.db

import com.hfbank.actor.BizActorWorker
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.source.DBDAO
import com.hfbank.mode.bean.SourceDB
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceDBAddBiz


class DBAddWorker extends BizActorWorker with DBDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    
    log.info(s"Start DBAddWorker")
    
    val getRes:Int = {
      event.content match {
        case insertObj:SourceDB => {
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
        
    Some(TEvent(HfbkUtil.getUUID(), SourceDBAddBiz, getRes, HfbkUtil.getTime(), Some(self)))
  }
}