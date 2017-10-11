package com.andy.actor.business.mgoods

import com.andy.actor.dao.MGoodsDao
import com.andy.actor.BizActorWorker
import com.andy.mode.TEvent

/**
 * @author andy
 */
class MGoodsUpdateWorker extends BizActorWorker with MGoodsDao {

  def execute(event: TEvent[_]): Option[TEvent[_]] = {

    None
  }
}