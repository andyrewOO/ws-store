package com.egfbank.tfemp.constant

/**
 * ESB端的交易码
 */
object ESBTradeCode {
  /**
   * 查询客户信息
   */
  val queryCusInfo = "T63001"

  /**
   * 强制措施查询
   */
  val queryForceMeasure = "T63002"


  /**
   * 查询账户列表
   */
  val queryAccListInfo = "T63005"

  /**
   * 查询子账户列表
   */
  val querySubAccountList = "T63006"

  /**
   * 查询交易信息
   */
  val queryTranDetail = "T63007"

  /**
   * 冻结/支付维护
   */
  val freezeOrStop = "T63100"

  /**
   * 案件举报/可疑账户上报交易类型
   */
  val SERVID_REPORT_DOUBT_ACCOUNT: String = "12002001705"

  /**
   * 查询子账户列表
   */
  val SERVID_QUERY_SUBACCOUNTLIST = "12003000217"

}