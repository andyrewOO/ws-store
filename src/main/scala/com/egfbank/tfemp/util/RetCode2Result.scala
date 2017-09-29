package com.egfbank.tfemp.util

import com.egfbank.tfemp.constant.ESBRetCode
import com.egfbank.tfemp.constant.CFCAResult

/**
 * @author andy
 */
object RetCode2Result {
  def cast(retCode: String) = {
    retCode match {
      case ESBRetCode.SUCCESSFUL         => CFCAResult.SUCCESSFUL
      case ESBRetCode.TRADE_INPUTERROR   => CFCAResult.STOPFREE_FAIL_EXPIRE
      case ESBRetCode.TRADE_ILLEGALDATE  => CFCAResult.STOPDELAY_FAIL_OTHER
      case ESBRetCode.ACCOUNT_KEEPFREEZE => CFCAResult.STOP_FAIL_KEEPSTOP
      case ESBRetCode.ACCOUNT_NOTFOUND   => CFCAResult.INVALID_CARD_ACCOUNT
      case x                             => x
    }
  }
}