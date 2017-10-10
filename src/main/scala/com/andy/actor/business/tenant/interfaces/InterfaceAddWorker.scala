package com.hfbank.actor.business.tenant.interfaces

import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.TenantInterfaceAddBiz
import com.hfbank.actor.dao.tenant.InterfaceDAO
import com.hfbank.mode.TEvent
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantInterface
import com.hfbank.constant.BeanStatus
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class InterfaceAddWorker extends BizActorWorker with InterfaceDAO {
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start InterfaceAddWorker")
    
    var rtnmsg = "数据库错误！"
    val response = event.content match {
      case interface: TenantInterface => {
        try {
        	insertInterface(interface.copy(status = BeanStatus.INI))
        } catch {
          case ex: RepetitionException => rtnmsg = "数据重复，请检查代码或名称！"
        	0
        }
      }
      case _ =>  0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantInterfaceAddBiz, SQLResponse(response,rtnmsg), HfbkUtil.getTime(), Some(self)))
  }
}