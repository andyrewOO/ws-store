package com.egfbank.tfemp.manager.model

/**
 * @author Administrator
 */
case class DoubtAccount(
  var id: String,
  var doubtType: String,
  var account: String,
  var accountName: String,
  var certType: String,
  var certNo: String,
  var upTime: String)