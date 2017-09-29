package com.egfbank.tfemp.actor

import scalaj.http._
import xitrum.Log
import xitrum.util.Loader

/**
 * @author huxp
 */

trait THttpTelClient extends THttpClient with Log {
  /**
   * @param url
   * @param values
   * @return
   */
  def try3TimesPost(httpErrorEvent: HttpErrorEvent): String = {
    try {
      val response = post(httpErrorEvent.url, httpErrorEvent.values)
      if (!response.isError) response.body else throw HttpErrorException
    } catch {
      case t: Throwable =>
        if (httpErrorEvent.times <= 3) {
          log.error("post请求失败:" + httpErrorEvent.times + ",重试！")
          try3TimesPost(HttpErrorEvent(httpErrorEvent.url, httpErrorEvent.values, httpErrorEvent.times + 1))
        } else {
          throw t
        }
    }
  }
  
  /**
   * @param txCode
   * @return
   */
  def readPretendESBResponse(txCode:String) = {
    Loader.stringFromClasspath("testfile/"+txCode+".txt")
  }
}

trait ErrorEvent
object HttpErrorException extends Exception
case class HttpErrorEvent(val url: String, val values: String, val times: Int) extends ErrorEvent


trait ErrorJob
case class HttpPostJob(val url:String, val v:String) extends ErrorJob

trait ErrorExecutor { def execute[T <: ErrorJob](job: T)}

trait ErrorHoldBox {
  self :ErrorExecutor => 
    
    /**
     * @param job
     */
    def try3TimesJob(job: ErrorJob) = {
      self.execute(job)
    }
}


