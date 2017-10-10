package com.hfbank.action.business.tenant.source

import xitrum.SkipCsrfCheck
import com.hfbank.action.BaseAppAction
import com.hfbank.mode.TEvent
import com.hfbank.actor.ActorHolder
import com.hfbank.util.HfbkUtil
import com.hfbank.actor.TenantSourceAddBiz
import com.hfbank.mode.bean.TenantSource
import com.hfbank.mode.UpdateResult
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.util.Arrays.ArrayList
import com.hfbank.mode.SQLResponse

/**
 * @author win7
 */
class SourceAddAction extends BaseAppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info(s"Start SourceAddAction")

    val arrayObj = param("arrayObject")
    val jsonListStr = parse(arrayObj)
    val beanList = jsonListStr.children
    
    val list = for (item <- beanList) yield {
      val beanStr = item.asInstanceOf[JValue]
      getBean[TenantSource](beanStr)
    }
    
    ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), TenantSourceAddBiz, list, HfbkUtil.getTime(), Some(self))
    context.become(receiveResponse)
  }

  def receiveResponse: Receive = {
    case result: TEvent[_] => {
      log.info(s"response is:${result}")
      
      result.content match {
        case r: SQLResponse => {
          r.count match {
            case 1 => respondJson(UpdateResult(true, "success"))
            case 0 => respondJson(UpdateResult(false, r.msg))//后台拼接的错误信息，反馈提示行项目
          }
        }
        case _ => respondJson(UpdateResult(false, "fail"))
      }
    }
  }
}