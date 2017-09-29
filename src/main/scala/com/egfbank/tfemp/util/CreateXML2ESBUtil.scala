package com.egfbank.tfemp.util

import scala.xml.Elem
import com.egfbank.tfemp.constant.CFCATradeRegex
import com.egfbank.tfemp.constant.ESBSvcId
import com.egfbank.tfemp.constant.ESBTradeCode
import xitrum.util.Loader
import com.egfbank.tfemp.constant.Constants
import com.egfbank.tfemp.constant.CFCATradeCode
/**
 * 生成对ESB接口请求的报文
 * @author andy
 */
object CreateXML2ESBUtil {

  private lazy val C_T63100 = Loader.propertiesFromClasspath("esb/T63100.properties")
  private lazy val C_T63001 = Loader.propertiesFromClasspath("esb/T63001.properties")
  private lazy val C_T63002 = Loader.propertiesFromClasspath("esb/T63002.properties")
  private lazy val C_T63005 = Loader.propertiesFromClasspath("esb/T63005.properties")
  private lazy val C_T63006 = Loader.propertiesFromClasspath("esb/T63006.properties")
  private lazy val C_T63007 = Loader.propertiesFromClasspath("esb/T63007.properties")

  def create(tranCode: String, dataMap: Map[String, Any]): Elem = {
    tranCode match {
      case ESBTradeCode.freezeOrStop         => T63100(dataMap)
      case ESBTradeCode.queryCusInfo         => T63001(dataMap)
      case ESBTradeCode.queryForceMeasure    => T63002(dataMap)
      case ESBTradeCode.queryAccListInfo     => T63005(dataMap)
      case ESBTradeCode.querySubAccountList  => T63006(dataMap)
      case ESBTradeCode.queryTranDetail      => T63007(dataMap)
      case CFCATradeRegex.caseReportRegex()  => caseReport(dataMap)
      case CFCATradeCode.suspiciousSingleVer => checkResponseS(dataMap)
      case CFCATradeCode.suspiciousMultiVer  => checkResponseM(dataMap)
      case ESBSvcId.QueryTfempForceMeasure   => queryForceMeasure(dataMap)
      case _                                 => null
    }
  }

  /**
   * 止付/冻结维护接口报文
   */
  private[this] def T63100(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <SvcId>{ C_T63100.getProperty("SvcId", "") }</SvcId>
        <SvcScnId>{ C_T63100.getProperty("SvcScnId", "") }</SvcScnId>
        <ChnlType>{ C_T63100.getProperty("ChnlType", "") }</ChnlType>
        <CnsmSysSeqNo>{ ToolUtils.createCnsmSysSeqNo() }</CnsmSysSeqNo>
        <CnsmSysId>{ C_T63100.getProperty("CnsmSysId", "") }</CnsmSysId>
        <TranMode>{ C_T63100.getProperty("TranMode", "") }</TranMode>
        <TranDate>{ HfbkUtil.getXmlToday() }</TranDate>
        <TranTime>{ HfbkUtil.getXmlTime() }</TranTime>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId>{
          dataMap.getOrElse("BankID", "") match {
            case Constants.efgOrgNum         => C_T63100.getProperty("TellerId", "")
            case Constants.YangZhongOrgNum   => C_T63100.getProperty("TellerIdOfYZ", "")
            case Constants.ChongQingJiangBeiOrgNum=> C_T63100.getProperty("TellerIdOfJB", "")
            case Constants.ChongQingYunYangOrgNum=> C_T63100.getProperty("TellerIdOfYY", "")
            case Constants.TongLuOrgNum      => C_T63100.getProperty("TellerIdOfTL", "")
            case Constants.GuangAnOrgNum     => C_T63100.getProperty("TellerIdOfGA", "")
            case _                           => ""
          }
        }</TellerId>
        <BranchId>{
          dataMap.getOrElse("BankID", "") match {
            case Constants.efgOrgNum         => C_T63100.getProperty("BranchId", "")
            case Constants.YangZhongOrgNum   => C_T63100.getProperty("BranchIdOfYZ", "")
            case Constants.ChongQingJiangBeiOrgNum=> C_T63100.getProperty("BranchIdOfJB", "")
            case Constants.ChongQingYunYangOrgNum=> C_T63100.getProperty("BranchIdOfYY", "")
            case Constants.TongLuOrgNum      => C_T63100.getProperty("BranchIdOfTL", "")
            case Constants.GuangAnOrgNum     => C_T63100.getProperty("BranchIdOfGA", "")
            case _                           => ""
          }
        }</BranchId>
      </APP_HEAD>
      <BODY>
        <TranCode>{ C_T63100.getProperty("TranCode", "") }</TranCode>
        <TranType>{ dataMap.getOrElse("TranType", "") }</TranType>
        <OrigFrzDt>{
          dataMap.getOrElse("OrigFrzDt", "") match {
            case ""=> ""
            case x=> x.toString.substring(0, 8)
          }
        }</OrigFrzDt>
        <OrigFrzSeqNo>{ dataMap.getOrElse("OrigFrzSeqNo", "") }</OrigFrzSeqNo>
        <AcctNo>{ dataMap.getOrElse("AcctNo", "") }</AcctNo>
        <SubAcctNo>{ dataMap.getOrElse("SubAcctNo", "") }</SubAcctNo>
        <FrzType>{ dataMap.getOrElse("FrzType", "") }</FrzType>
        <FrzMode>{ dataMap.getOrElse("FrzMode", "") }</FrzMode>
        <CtfType>{ dataMap.getOrElse("CtfType", "") }</CtfType>
        <CtfCode>{ dataMap.getOrElse("CtfCode", "") }</CtfCode>
        <OrgType>{ dataMap.getOrElse("OrgType", "") }</OrgType>
        <OrgName>{ dataMap.getOrElse("OrgName", "") }</OrgName>
        <TranAmt>{ dataMap.getOrElse("TranAmt", "") }</TranAmt>
        <CcyType>{
          CastTool.currencyCast(dataMap.getOrElse("CcyType", "").toString)
        }</CcyType>
        <ExprtDate>{
          dataMap.getOrElse("ExprtDate", "") match {
            case ""=> ""
            case x=> x.toString.substring(0, 8)
          }
        }</ExprtDate>
        <OprtName>{ dataMap.getOrElse("OprtName", "") }</OprtName>
        <OprtIdTp>{
          CastTool.certificateCast(dataMap.getOrElse("OprtIdTp", "").toString)
        }</OprtIdTp>
        <OprtIdCd>{ dataMap.getOrElse("OprtIdCd", "") }</OprtIdCd>
        <OprtName2>{ dataMap.getOrElse("OprtName2", "") }</OprtName2>
        <OprtIdTp2>{
          CastTool.certificateCast(dataMap.getOrElse("OprtIdTp2", "").toString)
        }</OprtIdTp2>
        <OprtIdCd2>{ dataMap.getOrElse("OprtIdCd2", "") }</OprtIdCd2>
        <TranDsc>{
          val dsc: String = dataMap.getOrElse("TranDsc", "Nothing").toString
          if (dsc.trim.equals("")) "Nothing" else dsc
        }</TranDsc>
        <CntnFrzExprtDt>{
          dataMap.getOrElse("CntnFrzExprtDt", "") match {
            case ""=> ""
            case x=> x.toString.substring(0, 8)
          }
        }</CntnFrzExprtDt>
      </BODY>
    </service>
  }

  /**
   * 查询账户列表接口报文
   */
  private[this] def T63005(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <SvcId>{ C_T63005.getProperty("SvcId", "") }</SvcId>
        <SvcScnId>{ C_T63005.getProperty("SvcScnId", "") }</SvcScnId>
        <ChnlType>{ C_T63005.getProperty("ChnlType", "") }</ChnlType>
        <CnsmSysSeqNo>{ ToolUtils.createCnsmSysSeqNo() }</CnsmSysSeqNo>
        <CnsmSysId>{ C_T63005.getProperty("CnsmSysId", "") }</CnsmSysId>
        <TranMode>{ C_T63005.getProperty("TranMode", "") }</TranMode>
        <TranDate>{ HfbkUtil.getXmlToday() }</TranDate>
        <TranTime>{ HfbkUtil.getXmlTime() }</TranTime>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId>{ C_T63005.getProperty("TellerId", "") }</TellerId>
        <BranchId>{ C_T63005.getProperty("BranchId", "") }</BranchId>
      </APP_HEAD>
      <BODY>
        <TranCode>{ C_T63005.getProperty("TranCode", "") }</TranCode>
        <ClientNo>{ dataMap.getOrElse("ClientNo", "") }</ClientNo>
      </BODY>
    </service>
  }

  /**
   * 客户开户信息查询接口报文
   */
  private[this] def T63001(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <SvcId>{ C_T63001.getProperty("SvcId", "") }</SvcId>
        <SvcScnId>{ C_T63001.getProperty("SvcScnId", "") }</SvcScnId>
        <ChnlType>{ C_T63001.getProperty("ChnlType", "") }</ChnlType>
        <CnsmSysSeqNo>{ ToolUtils.createCnsmSysSeqNo() }</CnsmSysSeqNo>
        <CnsmSysId>{ C_T63001.getProperty("CnsmSysId", "") }</CnsmSysId>
        <TranMode>{ C_T63001.getProperty("TranMode", "") }</TranMode>
        <TranDate>{ HfbkUtil.getXmlToday() }</TranDate>
        <TranTime>{ HfbkUtil.getXmlTime() }</TranTime>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId>{ C_T63001.getProperty("TellerId", "") }</TellerId>
        <BranchId>{ C_T63001.getProperty("BranchId", "") }</BranchId>
      </APP_HEAD>
      <BODY>
        <TranCode>{ C_T63001.getProperty("TranCode", "") }</TranCode>
        <QryMode>{ dataMap.getOrElse("QryMode", "") }</QryMode>
        <AcctNo>{ dataMap.getOrElse("AcctNo", "") }</AcctNo>
        <ClientNo>{ dataMap.getOrElse("ClientNo", "") }</ClientNo>
        <AcctType>{ dataMap.getOrElse("AcctType", "") }</AcctType>
        <IdType>{
          CastTool.certificateCast(dataMap.getOrElse("IdType", "").toString)
        }</IdType>
        <IdCode>{ dataMap.getOrElse("IdCode", "") }</IdCode>
        <ClientName>{ dataMap.getOrElse("ClientName", "") }</ClientName>
      </BODY>
    </service>
  }

  /**
   * 账户冻结信息查询接口报文
   */
  private[this] def T63002(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <SvcId>{ C_T63002.getProperty("SvcId", "") }</SvcId>
        <SvcScnId>{ C_T63002.getProperty("SvcScnId", "") }</SvcScnId>
        <ChnlType>{ C_T63002.getProperty("ChnlType", "") }</ChnlType>
        <CnsmSysSeqNo>{ ToolUtils.createCnsmSysSeqNo() }</CnsmSysSeqNo>
        <CnsmSysId>{ C_T63002.getProperty("CnsmSysId", "") }</CnsmSysId>
        <TranMode>{ C_T63002.getProperty("TranMode", "") }</TranMode>
        <TranDate>{ HfbkUtil.getXmlToday() }</TranDate>
        <TranTime>{ HfbkUtil.getXmlTime() }</TranTime>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId>{ C_T63002.getProperty("TellerId", "") }</TellerId>
        <BranchId>{ C_T63002.getProperty("BranchId", "") }</BranchId>
      </APP_HEAD>
      <BODY>
        <TranCode>{ C_T63002.getProperty("TranCode", "") }</TranCode>
        <AcctNo>{ dataMap.getOrElse("AcctNo", "") }</AcctNo>
      </BODY>
    </service>
  }

  /**
   * 子账户列表查询接口报文
   */
  private[this] def T63006(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <SvcId>{ C_T63006.getProperty("SvcId", "") }</SvcId>
        <SvcScnId>{ C_T63006.getProperty("SvcScnId", "") }</SvcScnId>
        <ChnlType>{ C_T63006.getProperty("ChnlType", "") }</ChnlType>
        <CnsmSysSeqNo>{ ToolUtils.createCnsmSysSeqNo() }</CnsmSysSeqNo>
        <CnsmSysId>{ C_T63006.getProperty("CnsmSysId", "") }</CnsmSysId>
        <TranMode>{ C_T63006.getProperty("TranMode", "") }</TranMode>
        <TranDate>{ HfbkUtil.getXmlToday() }</TranDate>
        <TranTime>{ HfbkUtil.getXmlTime() }</TranTime>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId>{ C_T63006.getProperty("TellerId", "") }</TellerId>
        <BranchId>{ C_T63006.getProperty("BranchId", "") }</BranchId>
      </APP_HEAD>
      <BODY>
        <TranCode>{ C_T63006.getProperty("TranCode", "") }</TranCode>
        <AcctNo>{ dataMap.getOrElse("AcctNo", "") }</AcctNo>
        <OffSet>{ C_T63006.getProperty("OffSet", "") }</OffSet>
        <QryNum>{ C_T63006.getProperty("QryNum", "") }</QryNum>
      </BODY>
    </service>
  }

  /**
   * 交易明细查询接口报文
   */
  private[this] def T63007(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <SvcId>{ C_T63007.getProperty("SvcId", "") }</SvcId>
        <SvcScnId>{ C_T63007.getProperty("SvcScnId", "") }</SvcScnId>
        <ChnlType>{ C_T63007.getProperty("ChnlType", "") }</ChnlType>
        <CnsmSysSeqNo>{ ToolUtils.createCnsmSysSeqNo() }</CnsmSysSeqNo>
        <CnsmSysId>{ C_T63007.getProperty("CnsmSysId", "") }</CnsmSysId>
        <TranMode>{ C_T63007.getProperty("TranMode", "") }</TranMode>
        <TranDate>{ HfbkUtil.getXmlToday() }</TranDate>
        <TranTime>{ HfbkUtil.getXmlTime() }</TranTime>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId>{ C_T63007.getProperty("TellerId", "") }</TellerId>
        <BranchId>{ C_T63007.getProperty("BranchId", "") }</BranchId>
      </APP_HEAD>
      <BODY>
        <TranCode>{ C_T63007.getProperty("TranCode", "") }</TranCode>
        <AcctNo>{ dataMap.getOrElse("AcctNo", "") }</AcctNo>
        <SubAcctNo>{ dataMap.getOrElse("SubAcctNo", "") }</SubAcctNo>
        <CcyType>{ dataMap.getOrElse("CcyType", "") }</CcyType>
        <DbtCrdtFlg>{ dataMap.getOrElse("DbtCrdtFlg", "") }</DbtCrdtFlg>
        <StartAmt>{ dataMap.getOrElse("StartAmt", "") }</StartAmt>
        <EndAmt>{ dataMap.getOrElse("EndAmt", "") }</EndAmt>
        <StartDate>{ dataMap.getOrElse("StartDate", "") }</StartDate>
        <EndDate>{ dataMap.getOrElse("EndDate", "") }</EndDate>
        <SortordMode>{ C_T63007.getProperty("SortordMode", "") }</SortordMode>
        <BeginNum>{ C_T63007.getProperty("BeginNum", "") }</BeginNum>
        <RqstNum>{ C_T63007.getProperty("RqstNum", "") }</RqstNum>
      </BODY>
    </service>
  }

  /**
   * 单要素查验
   */
  private[this] def checkResponseS(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <CnsmSysId/>
        <TranRetSt/>
        <TranDate/>
        <array>
          <RetInf>
            <RetMsg>{ dataMap.getOrElse("RetMsg", "") }</RetMsg>
            <RetCode>{
              dataMap.getOrElse("RetCode", "") match {
                case "100000"=> "000000000000"
                case x=> x
              }
            }</RetCode>
          </RetInf>
        </array>
        <Mac/>
        <MacOrgId/>
        <PrvdSysId/>
        <SvcScnId>{ dataMap.getOrElse("SvcScnId", "") }</SvcScnId>
        <TranTime/>
        <CnsmSysSeqNo/>
        <PrvdSysSeqNo/>
        <SvcId>{ dataMap.getOrElse("SvcId", "") }</SvcId>
      </SYS_HEAD>
      <APP_HEAD>
        <ChkFlag/>
        <AuthFlag/>
        <TlrPswd/>
        <AuthTlrId/>
        <TellerId/>
        <BranchId/>
        <TlrType/>
        <AuthBrchId/>
        <TlrLvl/>
      </APP_HEAD>
      <BODY>
        <TranSeqNo>{ dataMap.getOrElse("TranSeqNo", "") }</TranSeqNo>
        <TranSt>{ dataMap.getOrElse("TranSt", "") }</TranSt>
        <TranDsc>{ dataMap.getOrElse("TranDsc", "") }</TranDsc>
        <TranSrc>{ dataMap.getOrElse("TranSrc", "") }</TranSrc>
      </BODY>
    </service>
  }

  /**
   * 双要素查验
   */
  private[this] def checkResponseM(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <CnsmSysId/>
        <TranRetSt/>
        <TranDate/>
        <array>
          <RetInf>
            <RetMsg>{ dataMap.getOrElse("RetMsg", "") }</RetMsg>
            <RetCode>{
              dataMap.getOrElse("RetCode", "") match {
                case "100000"=> "000000000000"
                case x=> x
              }
            }</RetCode>
          </RetInf>
        </array>
        <Mac/>
        <MacOrgId/>
        <PrvdSysId/>
        <SvcScnId>{ dataMap.getOrElse("SvcScnId", "") }</SvcScnId>
        <TranTime/>
        <CnsmSysSeqNo/>
        <PrvdSysSeqNo/>
        <SvcId>{ dataMap.getOrElse("SvcId", "") }</SvcId>
      </SYS_HEAD>
      <APP_HEAD>
        <ChkFlag/>
        <AuthFlag/>
        <TlrPswd/>
        <AuthTlrId/>
        <TellerId/>
        <BranchId/>
        <TlrType/>
        <AuthBrchId/>
        <TlrLvl/>
      </APP_HEAD>
      <BODY>
        <TranSeqNo>{ dataMap.getOrElse("TranSeqNo", "") }</TranSeqNo>
        <TranSt>{ dataMap.getOrElse("TranSt", "") }</TranSt>
        <OutTranDsc>{ dataMap.getOrElse("OutTranDsc", "") }</OutTranDsc>
        <InTranDsc>{ dataMap.getOrElse("InTranDsc", "") }</InTranDsc>
        <TranSrc>{ dataMap.getOrElse("TranSrc", "") }</TranSrc>
      </BODY>
    </service>
  }

  /**
   *
   * @param dataMap
   * @return
   */
  private[this] def caseReport(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <CnsmSysId/>
        <TranRetSt/>
        <TranDate/>
        <array>
          <RetInf>
            <RetMsg>{ dataMap.getOrElse("RetMsg", "") }</RetMsg>
            <RetCode>{
              dataMap.getOrElse("RetCode", "") match {
                case "100000"=> "000000000000"
                case x=> x
              }
            }</RetCode>
          </RetInf>
        </array>
        <Mac/>
        <MacOrgId/>
        <PrvdSysId/>
        <SvcScnId>{ dataMap.getOrElse("SvcScnId", "") }</SvcScnId>
        <TranTime/>
        <CnsmSysSeqNo>{ dataMap.getOrElse("CnsmSysSeqNo", "") }</CnsmSysSeqNo>
        <PrvdSysSeqNo/>
        <SvcId>{ dataMap.getOrElse("SvcId", "") }</SvcId>
      </SYS_HEAD>
      <APP_HEAD>
        <ChkFlag/>
        <AuthFlag/>
        <TlrPswd/>
        <AuthTlrId/>
        <TellerId/>
        <BranchId/>
        <TlrType/>
        <AuthBrchId/>
        <TlrLvl/>
      </APP_HEAD>
      <BODY>
        <MsgId>{ dataMap.getOrElse("MsgId", "") }</MsgId>
      </BODY>
    </service>
  }

  /**
   * TFEMP强制措施查询反馈
   */
  private[this] def queryForceMeasure(dataMap: Map[String, Any]) = {
    <service>
      <SYS_HEAD>
        <CnsmSysId/>
        <TranRetSt/>
        <TranDate/>
        <array>
          <RetInf>
            <RetMsg>{ dataMap.getOrElse("RetMsg", "") }</RetMsg>
            <RetCode>{ dataMap.getOrElse("RetCode", "") }</RetCode>
          </RetInf>
        </array>
        <Mac/>
        <MacOrgId/>
        <PrvdSysId/>
        <SvcScnId>{ dataMap.getOrElse("SvcScnId", "") }</SvcScnId>
        <TranTime/>
        <CnsmSysSeqNo/>
        <PrvdSysSeqNo/>
        <SvcId>{ dataMap.getOrElse("SvcId", "") }</SvcId>
      </SYS_HEAD>
      <APP_HEAD>
        <TellerId/>
        <BranchId/>
      </APP_HEAD>
      <BODY>
        <array>
          {
            for (tranDtlRcrd <- dataMap("array").asInstanceOf[List[Map[String, String]]]) yield {
              <TranDtlRcrd>
                <TranOcrDt>{ tranDtlRcrd("TranOcrDt") }</TranOcrDt>
                <AcctNo>{ tranDtlRcrd("AcctNo") }</AcctNo>
                <SubAcctNo>{ tranDtlRcrd("SubAcctNo") }</SubAcctNo>
                <OprtType>{ tranDtlRcrd("OprtType") }</OprtType>
                <ExcsOrgNm>{ tranDtlRcrd("ExcsOrgNm") }</ExcsOrgNm>
                <FilePath>{ tranDtlRcrd("FilePath") }</FilePath>
              </TranDtlRcrd>
            }
          }
        </array>
      </BODY>
    </service>
  }
}