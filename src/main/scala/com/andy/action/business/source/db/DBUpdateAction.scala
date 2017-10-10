package com.hfbank.action.business.source.queue

import com.hfbank.action.BaseAppAction
import xitrum.SkipCsrfCheck
import com.hfbank.mode.bean.SourceDB
import com.hfbank.mode.AppQueryParam
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.UpdateResult
import com.hfbank.actor.SourceDBUpdateBiz

class DBUpdateAction extends BaseAppAction with SkipCsrfCheck {

   def execute(){
    log.info("DBUpdateAction.execute() start")
    var updateParam:Any = null
    try{
      updateParam = getBean[SourceDB](this.paramo("db")).copy(updater = this.getCurrentUserName())
    }catch{
      case e: MatchError => {
        log.info("match error!!!")
        e.printStackTrace()
      }
      case e:Exception => {
        log.info("not match error!!!")
        e.printStackTrace()
      }
      }
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), SourceDBUpdateBiz, updateParam, HfbkUtil.getTime(), Some(self))
    context.become(this.rst)
  }
  
  def rst: Receive = {
    case tevent: TEvent[Int] => {
      tevent.content match {
//---------------------------增加更新操作值 返回类型 int  0 1 2，前台取值显示--------------------------------------
        case 1 => respondJson(UpdateResult(true, "更新成功"))
        case 0 => respondJson(UpdateResult(false, "更新失败"))
        case _ => this.respondJson(UpdateResult(false, "操作返回错误"))
      }
    }
  }
  
}