package com.egfbank.tfemp.manager.model

/**
 * @author Administrator
 */
case class UpdownLog(
  var id: String,
  var upType: String,
  var upTime: String,
  var upContent: String,
  var downContent: String,
  var upResult: String)

case class ForceMeasureFile(
  val applicationID: String,
  val esbID: String,
  val transType: String,
  val account: String,
  val fileName: String,
  val fileContent: String,
  val executeTime: String)

case class ForceMeasure(
  val applicationID: String,
  val esbID: String,
  val transType: String,
  val account: String,
  val content: String,
  val executeTime: String,
  val flag: String,
  val msg: String)

case class QueryDynamic(
  val applicationID: String,
  val oriapplicationID: String,
  val transType: String,
  val account: String,
  val content: String,
  val startTime: String,
  val endTime: String)

case class QueryDynamicLift(
  val applicationID: String,
  val oriapplicationID: String,
  val transType: String,
  val account: String,
  val content: String,
  val endTime: String)

case class QueryDynamicFile(
  val applicationID: String,
  val transType: String,
  val fileName: String,
  val fileContent: String,
  val uptime: String)

case class Remark(
  val applicationID: String,
  val phone: String) 