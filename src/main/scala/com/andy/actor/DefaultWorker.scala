package com.hfbank.actor

import akka.actor.Actor

trait DefaultWorker extends Actor{
  def receive = {
    case _=>
  }
}