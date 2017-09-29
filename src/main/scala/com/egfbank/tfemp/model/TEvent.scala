package com.egfbank.tfemp.model

import akka.actor.ActorRef
import com.egfbank.tfemp.actor.BizScene

/**
 * @author andy
 */
case class TEvent[T](
  id: String,
  tradeType: BizScene,
  content: T,
  time: String,
  tsender: Option[ActorRef])
  
case class CFCACheckEvent(
  isPassed: Boolean,
  result: String,
  failureCause: String,    //技术原因
  feedbackRemark: String    //业务原因
    )