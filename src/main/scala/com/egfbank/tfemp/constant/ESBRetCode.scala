package com.egfbank.tfemp.constant

/**
 * @author andy
 */
object ESBRetCode {

  /**
   * 成功
   */
  val SUCCESSFUL = "000000000000"

  /**
   *  找不到记录
   */
  val TRADE_NORECORD = "0431"

  /**
   * 非法日期
   */
  val TRADE_ILLEGALDATE = "0421"

  /**
   * 信息输入错误
   */
  val TRADE_INPUTERROR = "0423"

  /**
   * 账户不存在
   */
  val ACCOUNT_NOTFOUND = "0604"

  /**
   * 账户已冻结
   */
  val ACCOUNT_KEEPFREEZE = "0612"

  /**
   * 执行机关与原事故机关不一致
   */
  val StopOrFreeze_ORGDIF = "1420"

}