package com.egfbank.tfemp.actor

import akka.actor.Props
import akka.routing.RoundRobinPool
import scala.io.Source
import java.io.File

/**
 * @author huxp
 */
object ActorHolder {
  lazy val proxyActor = xitrum.Config.actorSystem.actorOf(Props[ProxyActor].withRouter(RoundRobinPool(30)), "proxyActor")
  val file: File = null
  Source.fromFile(file).bufferedReader()
}