package com.hfbank.actor

import akka.actor.Props
import akka.routing.RoundRobinPool

/**
 * @author andy
 */
object ActorHolder {
  lazy val proxyActor = xitrum.Config.actorSystem.actorOf(Props[ProxyActor].withRouter(RoundRobinPool(30)), "proxyActor")
}