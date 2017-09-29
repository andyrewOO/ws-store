package com.egfbank.tfemp.constant

/**
 * 前置机的交易代码
 * @author huxp
 */
object CFCATradeCode {

  /**
   * 止付
   */
  val stopPayment: String = "100101"
  /**
   * 止付反馈
   */
  val stopPaymentR: String = "100102"
  /**
   * 止付解除
   */
  val stopPaymentLift: String = "100103"
  /**
   * 止付解除反馈
   */
  val stopPaymentLiftR: String = "100104"
  /**
   * 止付延期
   */
  val stopPaymentDelay: String = "100105"
  /**
   * 止付延期反馈
   */
  val stopPaymentDelayR: String = "100106"

  /**
   * 冻结
   */
  val freezePay: String = "100201"
  /**
   * 冻结反馈
   */
  val freezePayR: String = "100202"
  /**
   * 冻结解除
   */
  val freezePayFree: String = "100203"
  /**
   * 冻结解除反馈
   */
  val freezePayFreeR: String = "100204"
  /**
   * 冻结延期
   */
  val freezePayDelay: String = "100205"
  /**
   * 冻结延期反馈
   */
  val freezePayDelayR: String = "100206"

  /**
   * 账户交易明细查询
   */
  val queryAccountTransactionInfo: String = "100301"
  /**
   *  账户交易明细查询反馈
   */
  val queryAccountTransactionInfoR: String = "100302"

  /**
   * 账户持卡主体查询    100303
   */
  val queryAccountMainO: String = "100303"
  /**
   * 账户持卡主体查询反馈   100304
   */
  val queryAccountMainOR: String = "100304"

  /**
   * 账户动态查询      100305
   *
   */
  val queryAccountDynamic: String = "100305"
  /**
   * 账户动态查询反馈     100306
   */
  val queryAccountDynamicR: String = "100306"

  /**
   * 账户动态查询解除    100307
   *
   */
  val queryAccountDynamicLift: String = "100307"
  /**
   * 账户动态查询解除反馈   100308
   */
  val queryAccountDynamicLiftR: String = "100308"

  /**
   * 客户全账户查询       100309
   */
  val queryAllInfo: String = "100309"
  /**
   * 客户全账户查询反馈    100310
   */
  val queryAllInfoR: String = "100310"

  /**
   * 账号涉案/可疑名单单要素验证 100501
   */
  val suspiciousSingleVer: String = "100501"
  /**
   *  账号涉案/可疑名单双要素验证 100503
   */
  val suspiciousMultiVer: String = "100503"
  /**
   * 可疑名单上报-异常开卡
   */
  val abnormalOpenCardR: String = "100403"
  
  /**
   * 可疑名单上报-涉案账户
   */
  val involvedAccount: String = "100404"
  /**
   * 可疑名单上报-异常事件
   */
  val abnormalEvent: String = "100405"
  /**
   * 案件举报
   */
  val caseReport: String = "100401"
  /**
   * 案件举报反馈
   */
  val caseReportResult: String = "100402"

}