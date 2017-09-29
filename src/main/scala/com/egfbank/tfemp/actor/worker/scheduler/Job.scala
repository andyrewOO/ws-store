package com.egfbank.tfemp.actor.worker.scheduler

import akka.actor.ActorRef

/**
 * @author XuHaibin
 */
trait Job {
  /**
   * Executes the job.
   */
  val jobExecuter:ActorRef
  val message:Any
  val jobSchedule:JobSchedule
}

object Job {
  /**
   * Creates a job using a lazy expression.
   */
  def apply(sender: ActorRef, msg: Any, schedule:JobSchedule) = new Job {
	  override val jobExecuter = sender
	  override val message = msg
    override val jobSchedule = schedule
  }
}