package com.egfbank.tfemp.constant

/**
 * @author XJW
 */
object Constants {

  /**
   * 恒丰银行机构编码
   */
  val efgOrgNum = "315456000105"
  
  /**
   * 扬中恒丰村镇银行
   */
  val YangZhongOrgNum = "320314300022"
  
  /**
   * 重庆江北恒丰村镇银行
   */
  val ChongQingJiangBeiOrgNum = "320653000090"
  
  /**
   * 重庆云阳恒丰村镇银行
   */
  val ChongQingYunYangOrgNum = "320667600013"
  
  /**
   * 广安恒丰村镇银行
   */
  val GuangAnOrgNum = "320673700019"
  
  /**
   * 桐庐恒丰村镇银行
   */
  val TongLuOrgNum = "320331001057"

  /**
   * 默认托管机构
   */
  val efgDefaultTrustOrg = "000000000000"

  /**
   * ESB交易风险维护（120020017）
   */
  val ServiceId = "120020017"

  /**
   * 服务场景名称 : 账户涉案/可疑名单验证（02）
   */
  val suspiciousAccountVer = "02"

  /**
   *  服务场景名称 : 身份涉案/可疑名单验证（03）
   */
  val suspiciousIDVer = "03"

  /**
   * 服务场景名称 : 账户涉案/可疑名单验证（04）
   */
  val suspiciousDoubleVer = "04"

  /**
   * 服务场景名称: 电信诈骗可疑事件举报（05）
   */
  val caseReport = "05"
  
  /**
   * 服务场景名称：可疑名单上报-异常事件
   */
  val reportException = "06"

  val caseReport_case = "01"
  val caseReport_abnormalAccount = "02"
}