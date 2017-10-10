package com.hfbank.action.business.source.queue

import xitrum.ActorAction
import xitrum.SkipCsrfCheck
import com.hfbank.action.AppAction
import com.hfbank.mode.AppQueryParam
import com.hfbank.actor.SourceDBSearchBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.AppQueryResult
import com.hfbank.mode.bean.SourceDB
import com.hfbank.mode.Page
//import xitrum.annotation.POST

//@POST("/search/db")
class DBSearchAction extends AppAction with SkipCsrfCheck{

  def execute(){
    log.info("DBSearchAction.execute() start")
    var queryParam:Any = null
    try{
      val queueBean = getBean[SourceDB](this.paramo("db"))
      val page = getBean[Page](paramo("page"))
      queryParam = AppQueryParam(queueBean, page)
      log.info(s"the query param: ${queryParam}")
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
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), SourceDBSearchBiz, queryParam, HfbkUtil.getTime(), Some(self))
    
    context.become(this.rst)
  }

  def rst: Receive = {
    case TEvent(_, _, content, _, _) => {
      content match {
        case page: Page => {
          respondJson(AppQueryResult(true, "success", page.sum, page))
        }
        case _ => {
          respondJson(AppQueryResult(false, "actorResError", 0, ""))
        }
      }
    }
    case _ => {
          respondJson(AppQueryResult(false, "actorResError", -1, ""))
        }
  }
  
}