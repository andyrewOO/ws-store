package com.egfbank.tfemp.actor.worker

import akka.actor.ActorRef
import com.egfbank.tfemp.model.TEvent
import com.egfbank.tfemp.actor.services.CFCADbService

/**
 * @author huxp
 */
trait BizWorker extends DefaultWorker with CFCADbService {

  override def receive = {
    case event: TEvent[_] => {
      try {
        val resTEvent = execute(event)
        resTEvent.map { rt => event.tsender.map { x => x ! rt } }
      } catch {
        case t: Throwable => {
          log.error(s"An error occurred,${t}")
          log.error(s"An error occurred,${t.getStackTrace}")
          insertError(event, t)
        }
      }
      killself
    }

    case _ => log.warn("the worker param must be TEevnt")
  }

  def execute(event: TEvent[_]): Option[TEvent[_]]

  def killself = context stop self
}