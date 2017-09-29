package com.egfbank.tfemp.actor

import scalaj.http._

/**
 * @author huxp
 */

trait THttpClient {

  /**
   * Get请求
   */
  def get(url: String) = Http(url).timeout(10000, 60000).asString.body

  /**
   * Post请求
   */
  def post(url: String, values: Seq[(String, String)]):String = Http(url).timeout(10000, 60000).postForm(values).asString.body

  /**
   * Post请求
   */
  def post(url: String, values: String) = Http(url).timeout(10000, 60000).postData(values).asString

  /**
   * ssl请求
   */
  def sslget(url: String) = Http(url).timeout(10000, 60000).option(HttpOptions.allowUnsafeSSL).asString.body

}

