package com.hfbank.action.business

import xitrum.annotation.POST
import xitrum.SkipCsrfCheck
import com.hfbank.mode.AppQueryResult
import com.hfbank.mode.TEvent
import com.hfbank.actor.dao.VeirfyIDService
import com.hfbank.action.BaseAppAction

/**
 * @author win7
 */
@POST("verify")
class VerifyIDAction extends BaseAppAction with SkipCsrfCheck with VeirfyIDService {
  def execute(): Unit = {
    log.info(s"Start VerifyIDAction")

    val table = params("table")
    val key = params("key")
    val value = params("value")

    val result = selectID(table, key, value)
    val size = result.head.getOrElse("SIZEOF", "0").toInt
    respondJson(AppQueryResult(true, "", size, null))
  }
}