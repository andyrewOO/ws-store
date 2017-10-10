package com.hfbank.actor.business.tenant.source

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.SourceDAO
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantSourceUpdateBiz
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class SourceUpdateWorker extends BizActorWorker with SourceDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start SourceUpdateWorker")

    var rtnMessage = ""
    val response = event.content match {
      case list: Map[String, List[TenantSource]] => {
        try {
          sourceUpdate(list("update"), list("add"))
        } catch {
          case ex: RepetitionException =>
            log.info(s"RepetitionException:${ex}")
            rtnMessage = s"违反唯一约束:${ex.returnT}"
            0
        }
      }
      case _ => 0
    }

    log.info(s"Update Result:${response}")
    Some(TEvent(HfbkUtil.getUUID(), TenantSourceUpdateBiz, SQLResponse(response, rtnMessage), HfbkUtil.getTime(), Some(self)))
  }
}