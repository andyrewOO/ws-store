package com.hfbank.actor.business.tenant.message

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.MessageDAO
import com.hfbank.actor.TenantMessageAddBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantMessage
import com.hfbank.constant.BeanStatus
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class MessageAddWorker extends BizActorWorker with MessageDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start MessageAddWorker")
    
    var rtnmsg = "数据库错误！"
    val response = event.content match {
      case message: TenantMessage => {
        try {
        	insertMessage(message.copy(status = BeanStatus.INI))
        } catch {
          case ex: RepetitionException => rtnmsg = "数据重复，请检查代码或名称！"
          0
        }
      }
      case _ =>  0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantMessageAddBiz, SQLResponse(response,rtnmsg), HfbkUtil.getTime(), Some(self)))
  }
}