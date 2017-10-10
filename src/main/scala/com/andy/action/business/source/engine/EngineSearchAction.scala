package com.hfbank.action.business.source.engine

import xitrum.ActorAction
import xitrum.SkipCsrfCheck
import com.hfbank.action.AppAction
import com.hfbank.mode.AppQueryParam
import com.hfbank.actor.SourceEngineSearchBiz
import com.hfbank.util.HfbkUtil
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.mode.AppQueryResult
import com.hfbank.mode.bean.SourceEngine
import com.hfbank.mode.Page
//import xitrum.annotation.POST

//@POST("/search/re")
class EngineSearchAction extends AppAction with SkipCsrfCheck{

  def execute(){
    log.info("EngineSearchAction.execute() start")
    var queryParam:Any = null
    try{
      val EngineBean = getBean[SourceEngine](this.paramo("engine"))
      val page = getBean[Page](paramo("page"))
      queryParam = AppQueryParam(EngineBean, page)
      log.info(s"the queryParam: ${queryParam}")
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
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), SourceEngineSearchBiz, queryParam, HfbkUtil.getTime(), Some(self))
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