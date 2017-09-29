package com.egfbank.tfemp.constant

/**
 * 前置机的交易代号正则匹配
 * @author huxp
 */
object CFCATradeRegex {

  /**
   * 强制措施
   */
  val forceMeasure = """100[12]0[123456]""".r

  /**
   * 止付正则匹配
   */
  val paymentRegex = """10010[123456]""".r
  
  /**
   * 冻结正则匹配
   */
  val freezePayRegex = """10020[123456]""".r

  /**
   * 需要查询子账户的、止付或者冻结定期账户的applicationType类型
   */
  val applicationType_needSubAcc = """0[13]""".r

  /**
   * 冻结和止付的请求交易
   */
  val freeOrpaymentRegex = """100[12]0[135]""".r

  /**
   * 支付/冻结,查询类交易
   */
  val stopOrFreeOrQuery = """100[123]0[1357]""".r

  /**
   * 可疑名单查验
   */
  val suspiciousRegex = """10050[13]""".r

  /**
   * 案件举报
   */
  val caseReportRegex = """10040[1345]""".r
}