package com.andy.actor.business.mgoods

import com.andy.actor.BizActorWorker
import com.andy.mode.TEvent
import com.andy.mode.AppQueryParam
import com.andy.mode.bean.MGoods
import com.andy.actor.dao.MGoodsDao
import com.andy.util.HfbkUtil
import com.andy.actor.MGoodsSearchBiz

/**
 * @author andy
 */
class MGoodsSearchWorker extends BizActorWorker with MGoodsDao {

  def execute(event: TEvent[_]): Option[TEvent[_]] = {

    val paageRes = event.content match {
      case AppQueryParam(queryBean, page) => {
        val (queryRes, total) = queryBean match {
          case mgoods: MGoods => (search(mgoods, page), getTotal(mgoods))
        }

        log.info(s"Query total:${total.head.getOrElse("total", "")}")
        log.info(s"Query result size:${queryRes.size}")

        page.copy(result = queryRes, sum = total.head.getOrElse("total", "0").toInt)
      }
    }
    Some(TEvent(HfbkUtil.getUUID(), MGoodsSearchBiz, paageRes, HfbkUtil.getTime(), Some(self)))
  }
}