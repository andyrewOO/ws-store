package com.hfbank.actor.business.tenant.source

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.SourceDAO
import com.hfbank.actor.TenantSourceAddBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantSource
import com.hfbank.constant.BeanStatus
import com.hfbank.constant.SourceType
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class SourceAddWorker extends BizActorWorker with SourceDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start SourceAddWorker")
    
    var rtnmsg = "数据库错误！"
    val response = event.content match {
      case list: List[TenantSource] => {
        try {
          insertSource(list)
        } catch {
          case ex: RepetitionException => {
            val sourceId = ex.returnT(1)
            val sourceType = ex.returnT(2)
            sourceType match {
              case SourceType.ENGINEE => rtnmsg = "引擎（ID:" + sourceId + "）插入重复！"
              case SourceType.DB      => rtnmsg = "数据库（ID:" + sourceId + "）插入重复！"
              case SourceType.QUEUE   => rtnmsg = "队列（ID:" + sourceId + "）插入重复！"
            }
          }
          0
        }
      }
      case _ =>  0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantSourceAddBiz, SQLResponse(response,rtnmsg), HfbkUtil.getTime(), Some(self)))
  }
}