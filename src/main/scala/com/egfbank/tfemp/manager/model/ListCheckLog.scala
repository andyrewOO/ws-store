package com.egfbank.tfemp.manager.model

/**
 * @author Administrator
 */
case class ListCheckLog(

  var id: String,
  var searchTime: String,
  var appName: String,
  var account: String,
  var accountName: String,
  var name: String,
  var certType: String,
  var certNo: String,
  var status: String,
  var desc: String)