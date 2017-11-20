package com.andy.action.business.mgoods

import com.andy.action.AppAction
import xitrum.SkipCsrfCheck
import com.andy.mode.bean.MGoods

/**
 * @author andy
 */
class MGoodsAddAction extends AppAction with SkipCsrfCheck {
  def execute(): Unit = {
    log.info("Start MGoodsAddAction")
    log.info(s"MGoods:${paramo("mgoods")}")
    val oBean = getBean[MGoods](paramo("mgoods"))
    println(oBean)
  }
}