package com.hfbank.mode

import com.hfbank.actor.BizScene

import akka.actor.ActorRef

/**
 * @author andy
 */
case class TEvent[T](
  id: String,
  bizScene: BizScene,
  content: T,
  time: String,
  tsender: Option[ActorRef])