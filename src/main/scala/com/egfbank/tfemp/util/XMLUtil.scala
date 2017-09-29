package com.egfbank.tfemp.util

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.xml.NodeSeq
import scala.xml.XML

import org.json4s._
import org.json4s.Xml._
import org.json4s.jackson.JsonMethods._

import com.egfbank.tfemp.constant.CFCATradeCode

import cfca.safeguard.api.bank.Constants
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100101
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100103
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100105
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100201
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100203
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100205
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100301
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100303
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100305
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100307
import cfca.safeguard.api.bank.bean.tx.downstream.Tx100309
import cfca.safeguard.api.bank.util.Base64
import cfca.safeguard.api.bank.util.ResultUtil
import cfca.safeguard.tx.TxBase
import xitrum.Log

/**
 * @author andy
 */
object XMLUtil extends Log {
  /**
   * 获取 Send 对ESB请求接口交易
   */
  def getFV(field: String, xml: String): String = {
    (XML.loadString(xml) \\ field).text.toString
  }

  /**
   * 当XMl报文中有多个相同的Field时，可以拿到这个Field的List
   */
  def getFVList(field: String, xml: String): List[String] = {
    val vListNode = XML.loadString(xml) \\ field
    val vList = vListNode.map { _.text }
    vList.toList
  }

  /**
   * xml转换为map,先转换为json,再从json到map
   */
  def xml2Map(xml: String): Map[String, Any] = {
    implicit val formats = DefaultFormats
    val xmle = XML.loadString(xml)
    log.debug(xmle.toString())
    val json = toJson(xmle)
    log.debug(json.toString())
    json.extract[Map[String, Any]]
  }

  /**
   * 从XML中获取指定节点(body/head/.....)下的指定字段的List
   */
  def getFVListMap(field: String, xml: String, zone: String): List[Map[String, String]] = {
    val listMap = new ListBuffer[Map[String, String]]
    val fList: NodeSeq = (XML.loadString(xml) \ zone \ field)
    val cList = fList.map { _.child }
    val childList = cList.head
    for (item <- childList) {
      val beanMap: HashMap[String, String] = new HashMap[String, String]
      val childList = item.child
      for (child <- childList) beanMap += child.label -> child.text
      listMap += beanMap.toMap
    }
    log.debug("getFVListMap, listMap:" + listMap)
    listMap.toList
  }
  
  /**
   * @param field
   * @param xml
   * @return
   */
  def getListMap(field: String, xml: String): List[Map[String, String]] = {
    val list = new ListBuffer[Map[String, String]]
    val res = XML.loadString(xml) \\ field
    res.map { item =>
      val beanMap: HashMap[String, String] = new HashMap[String, String]
      val subNode = item.child.map { node =>
        if (!node.label.equals("#PCDATA")) {
          beanMap += node.label -> node.text
        }
      }
      list += beanMap.toMap
    }
    list.toList
  }

  /**
   * 把前置机返回来的消息转换成 bean，然后拿到 bean 的Map数据
   */
  def getDataMapFromMessage(xml: String): Map[String, Any] = {
    val tx: TxBase = ResultUtil.convertTxFromMessageXML(xml)
    var beanMap: Map[String, Any] = null

    tx.getTxCode() match {
      case CFCATradeCode.stopPayment =>
        // 转换为止付封装类
        val tx100101: Tx100101 = tx.asInstanceOf[Tx100101]
        val list = tx100101.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100101)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        beanMap += ("Attachments" -> attachmentMapList)
        log.info("止付.applicationID=" + tx100101.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.stopPaymentLift =>
        // 转换为止付解除封装类
        val tx100103: Tx100103 = tx.asInstanceOf[Tx100103]
        val list = tx100103.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100103)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        beanMap += ("attachments" -> attachmentMapList)
        log.info("止付解除.applicationID=" + tx100103.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.stopPaymentDelay =>
        // 转换为止付延期封装类
        val tx100105: Tx100105 = tx.asInstanceOf[Tx100105]
        val list = tx100105.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100105)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        beanMap += ("attachments" -> attachmentMapList)
        log.info("止付延期.applicationID=" + tx100105.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.freezePay =>
        // 转换为冻结封装类
        val tx100201: Tx100201 = tx.asInstanceOf[Tx100201]
        val list = tx100201.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100201)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("冻结.applicationID=" + tx100201.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.freezePayFree =>
        // 转换为冻结解除封装类
        val tx100203: Tx100203 = tx.asInstanceOf[Tx100203]
        val list = tx100203.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100203)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("冻结解除.applicationID=" + tx100203.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.freezePayDelay =>
        // 转换为冻结延期封装类
        val tx100205: Tx100205 = tx.asInstanceOf[Tx100205]
        val list = tx100205.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100205)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("冻结延期.applicationID=" + tx100205.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.queryAccountTransactionInfo =>
        // 转换为账户交易明细查询封装类
        val tx100301: Tx100301 = tx.asInstanceOf[Tx100301]
        val list = tx100301.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100301)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("账户交易明细查询.applicationID=" + tx100301.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.queryAccountMainO =>
        // 转换为账户持卡主体查询封装类
        val tx100303: Tx100303 = tx.asInstanceOf[Tx100303]
        val list = tx100303.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100303)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("账户持卡主体查询.applicationID=" + tx100303.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.queryAccountDynamic =>
        // 转换为账户动态查询封装类
        val tx100305: Tx100305 = tx.asInstanceOf[Tx100305]
        val list = tx100305.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100305)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("账户动态查询.applicationID=" + tx100305.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.queryAccountDynamicLift =>
        // 转换为账户动态查询解除封装类
        val tx100307: Tx100307 = tx.asInstanceOf[Tx100307]
        val list = tx100307.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100307)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("账户动态查询解除.applicationID=" + tx100307.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case CFCATradeCode.queryAllInfo =>
        // 转换为客户全账户查询封装类
        val tx100309: Tx100309 = tx.asInstanceOf[Tx100309]
        val list = tx100309.getAttachments
        beanMap = ToolUtils.BeanToMap(tx100309)
        val attachmentMapList = ToolUtils.JavaList2scalaList(list).map { x => ToolUtils.BeanToMap(x) }
        log.info("全账户查询.applicationID=" + tx100309.getApplicationID() + ".messageFrom=" + tx.getMessageFrom())
      case _ =>
        log.info("目前没有处理的TXCODE,请自己解析消息报文")
    }
//    log.info(" bMap:" + beanMap)
    beanMap
  }
}