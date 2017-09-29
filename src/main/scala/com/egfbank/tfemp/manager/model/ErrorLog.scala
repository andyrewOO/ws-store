package com.egfbank.tfemp.manager.model

/**
 * @author Administrator
 */
case class ErrorLog (
    var id:String,
    var errorType:String,
    var upOrDown:String,
    var logId:String,
    var errorTime:String,
    var status:String,
    var transType:String
)