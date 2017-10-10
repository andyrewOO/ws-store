package com.hfbank.action.business.source.queue

import com.hfbank.action.BaseAppAction
import xitrum.SkipCsrfCheck
import com.hfbank.mode.bean.SourceQueue
import com.hfbank.mode.AppQueryParam
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.SourceQueueAddBiz
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.UpdateResult

class QueueAddAction extends BaseAppAction with SkipCsrfCheck {

   def execute(){
    log.info("QueueAddAction.execute() start")
    var addParam:SourceQueue = null
    try{
      //addParam = getBean[SourceQueue](this.paramo("mq")).copy(creator = this.getCurrentUserName())
      addParam = getBean[SourceQueue](this.paramo("queue")).copy(creator = "tester")
      log.info(s"the addParam to QueueAddActor: ${addParam}")
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
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), SourceQueueAddBiz, addParam, HfbkUtil.getTime(), Some(self))
    context.become(this.rst)
  }
  
  def rst: Receive = {
    case tevent: TEvent[Int] => {
      tevent.content match {
//---------------------------增加更新操作值 返回类型 int  0 1 2，前台取值显示--------------------------------------
        case 1 => respondJson(UpdateResult(true, "添加成功"))
        case 0 => respondJson(UpdateResult(false, "添加失败"))
        case _ => this.respondJson(UpdateResult(false, "操作返回错误"))
      }
    }
  }
  
}