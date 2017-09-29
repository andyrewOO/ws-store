package com.egfbank.tfemp.constant

/**
 *
 * 可疑名单上报时使用的事件特征码
 * @author andy
 */
object CFCAFeatureCode {
  /**
   * 1001 频繁开户（可实施）
   * 1002 累计开户（可实施）
   */
  val abnormalOpenCardR = "100[12]".r

  /**
   * 涉案账户（可实施）
   */
  val involvedAccount = "200[1]".r

  /**
   * 3001 诈骗未遂（可实施）
   * 3002 受害人举报疑似诈骗（可实施）
   * 3003 银行举报疑似诈骗（可实施）
   * 3004 收方涉案（可实施）
   * 3005 付方涉案（可实施）
   * 3006 分散入集中出
   * 3007 集中入分散出
   * 3008 连续多笔交易
   */
  val abnormalEvent = "300[12345678]".r

  /**
   * 其余可疑交易
   */
  val other = "9999"
}