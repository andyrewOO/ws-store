package com.hfbank.actor

import com.hfbank.mode.TEvent
import akka.actor.actorRef2Scala

trait BizActorWorker extends DefaultWorker {
  override def receive = {
    case request: TEvent[_] => {
      try {
        val result = execute(request)
        result.map { rt =>
          request.tsender.map { x =>
            x ! rt
          }
        }
      } catch {
        case t: Exception => {
          t.printStackTrace()
          request.tsender.map { x => x ! "服务器处理异常" }
        }
      }
    }
  }
  def execute(event: TEvent[_]): Option[TEvent[_]]
  def killSelf = context stop self
}