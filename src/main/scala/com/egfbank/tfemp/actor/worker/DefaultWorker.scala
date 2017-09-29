package com.egfbank.tfemp.actor.worker

import com.egfbank.tfemp.actor._

import akka.actor.Actor
import akka.actor.ActorRef
import xitrum.Log

/**
 * @author huxp
 */
trait DefaultWorker extends Actor with Log {

  def receive = {
    case _ =>
  }

  def sendProxy(s: Any) = ActorHolder.proxyActor ! s

}
