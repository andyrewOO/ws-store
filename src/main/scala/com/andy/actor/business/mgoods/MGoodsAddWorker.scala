package com.andy.actor.business.mgoods

import com.andy.actor.BizActorWorker
import com.andy.actor.dao.MGoodsDao
import com.andy.mode.TEvent
import com.andy.mode.bean.MGoods
import com.andy.util.HfbkUtil
import java.util.Date
import com.andy.mode.UpdateResult

/**
 * @author andy
 */
class MGoodsAddWorker extends BizActorWorker with MGoodsDao {

  import com.andy.util.MyImplicitTypeConversion.IntToBoolean
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start MGoodsAddWorker")

    val nowTime = new Date
    val mgoods = event.content match {
      case m: MGoods => m.copy(id = HfbkUtil.getUUID(), ctime = nowTime)
    }

    val exeRes = insert(mgoods)
    Some(TEvent(HfbkUtil.getUUID(), event.bizScene, UpdateResult(exeRes), HfbkUtil.getTime(), Some(self)))
  }
}