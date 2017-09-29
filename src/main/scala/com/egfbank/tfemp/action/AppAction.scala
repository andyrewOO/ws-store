package com.egfbank.tfemp.action

import xitrum.ActorAction

import org.json4s.JObject
import org.json4s.jackson.JsonMethods

import org.json4s._
import org.json4s.jackson.JsonMethods._

trait AppAction extends ActorAction {

  /**
   * 转换请求参数
   */
  def requestMapString(): Map[String, String] = {
    implicit val formats = DefaultFormats
    requestContentJValue.extract[Map[String, String]]
  }

}