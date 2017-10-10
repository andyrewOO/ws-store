package com.hfbank.actor.business.tenant.interfaces

import com.hfbank.mode.TEvent
import com.hfbank.actor.BizActorWorker
import com.hfbank.actor.dao.tenant.InterfaceDAO
import com.hfbank.actor.TenantInterfaceUpdateBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.bean.TenantInterface
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class InterfaceUpdateWorker extends BizActorWorker with InterfaceDAO{
  def execute(event: TEvent[_]): Option[TEvent[_]] = {
    log.info(s"Start InterfaceUpdateWorker")
    
    var rtnmsg = "数据库错误！"
    val response = event.content match {
      case interface: TenantInterface => {
        try {
        	updateInterface(interface)
        } catch {
          case ex: RepetitionException => rtnmsg = "数据重复，请检查代码或名称！"
          0
        }
      }
      case _ =>  0
    }
    Some(TEvent(HfbkUtil.getUUID(), TenantInterfaceUpdateBiz, SQLResponse(response,rtnmsg), HfbkUtil.getTime(), Some(self)))
  }
}