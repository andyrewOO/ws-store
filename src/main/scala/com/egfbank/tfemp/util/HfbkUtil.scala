package com.egfbank.tfemp.util

import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.net.URLEncoder
import scala.collection.mutable.Map
import xitrum.Log
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.sql.Timestamp
import scala.util.Random
import com.egfbank.tfemp.actor.BizScene
import com.egfbank.tfemp.actor._

/**
 * @author lzh
 */
object HfbkUtil {

  /**
   * UUID
   */
  def getUUID(): String = {
    val id: java.util.UUID = java.util.UUID.randomUUID();
    id.toString().replace("-", "");
  }

  /**
   * 当天
   * yyyy-MM-dd
   */
  def getToday(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  /**
   * 当天
   * yyyyMMdd
   */
  def getXmlToday(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  /**
   * 获取昨天
   */
  def getYesterday(): String = {
    var dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    var cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.DATE, -1)
    var yesterday = dateFormat.format(cal.getTime())
    yesterday
  }

  /**
   * 获取明天
   */
  def getTomorrow(): String = {
    var dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    var cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.DATE, 1)
    var tomorrow = dateFormat.format(cal.getTime())
    tomorrow
  }

  /**
   * @return
   */
  def getTime(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }
  /**
   * @return
   */
  def getXmlTime(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("HHmmss");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  /**
   * 当前日期
   * yyyy-MM-dd HH:mm:ss
   */
  def getSecond(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  /**
   * 当前日期
   * yyyyMMddHHmmss
   */
  def getXMLSecond(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  /**
   * 获取当前时间戳
   */
  def getTimeStamp(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    val calendar: Calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  /**
   * 获取当前时间戳
   */
  def getTimeStampInstance(): Timestamp = {
    new Timestamp(System.currentTimeMillis())
  }

  /**
   * 获取之前某刻时间戳
   */
  def getBeforeTimeStamp(rtime: Long): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    return sdf.format(new java.util.Date(System.currentTimeMillis() - rtime));
  }

  /**
   * @param assignTime HH:mm:ss
   * @return
   */
  def intervalToAssignTime(assignTime: String): Long = {
    val currentTime = System.currentTimeMillis()
    val assignDate = getToday + " " + assignTime //yyyy-MM-dd HH:mm:ss
    var dateFormatHour: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val millTime = dateFormatHour.parse(assignDate).getTime
    if (millTime > currentTime) millTime - currentTime else 0
  }
  /**
   * 根据串 获得 date
   */
  def getDate(s: String, fmt: String): Date = {
    val format: SimpleDateFormat = new SimpleDateFormat(fmt)
    val date = format.parse(s);
    date
  }

  /**
   * 获得指定的 date串
   */
  def getDateStr(date: Date, fmt: String): String = {
    val format: SimpleDateFormat = new SimpleDateFormat(fmt)
    format.format(date)
  }

  /**
   * 获得指定的 date串
   */
  def getDateStr(date: Date, n: Long, fmt: String): String = {
    val newDate = new Date(date.getTime + n)
    val format: SimpleDateFormat = new SimpleDateFormat(fmt)
    format.format(newDate)
  }

  def urlCode(s: String): String = {
    try {
      val res: String = URLEncoder.encode(s, "UTF-8");
      res
    } catch {
      case t: Throwable =>
        Log.error("", t);
        null
    }
  }

  def checkData(str: String, fmt: String): Boolean = {
    val format: SimpleDateFormat = new SimpleDateFormat(fmt)
    format.setLenient(false);
    try {
      format.parse(str);
      return true
    } catch {
      case t: Throwable => {
        return false
      }
    }

  }

  /**
   * 类反射,传入class,返回fields列表
   */
  def extractFieldNames[T <: Any: Manifest] = {
    implicitly[Manifest[T]].runtimeClass.getDeclaredFields.map(_.getName)
  }

  // 驼峰命名
  def toCamelCase(value: String): String = {
    val b = new StringBuilder
    var toUpperPos = -1
    for (i <- 0.to(value.length - 1)) {
      if (!value(i).isLetterOrDigit)
        toUpperPos = i + 1
      else
        b.append(if (toUpperPos == i) value(i).toUpper else value(i))
    }
    val first = b.charAt(0)

    b.setCharAt(0, first.toLower)
    b.toString()
  }

  def toUnCamcelCase(value: String): String = {
    val b = new StringBuilder
    var pos = -1
    value.foreach { char =>
      pos += 1
      if (char.isLetterOrDigit) {
        if (char.isUpper)
          (if (pos == 0) b else b.append('_')).append(char.toLower)
        else
          b.append(char)
      }
    }
    b.toString()
  }

  def addEntry(target: Map[String, Any], targetKey: String, src: java.util.Map[String, String], srcKey: String, flag: String): Map[String, Any] = {
    if (null != src.get(srcKey)) {
      if ("String".equals(flag)) {
        target += (targetKey -> src.get(srcKey))
      } else if ("Json".equals(flag)) {
        try {
          val jsonStr = mapper.writeValueAsString(src.get(srcKey))
          target += (targetKey -> jsonStr)
        } catch {
          case t: Throwable => Log.error("", t)
        }
      }
    }
    target
  }

  def isInt(intStr: String): Boolean = {
    try {
      if (intStr.isEmpty() == false) {
        intStr.toInt
      }
      return true
    } catch {
      case _: Throwable => return false
    }
  }

  /**
   * 获取Option类型的值
   */
  def getOptionValue(value: Option[Any]) = {
    value match {
      case Some(x) => x
      case None    => "None"
    }
  }

  /**
   * 根据机构id生成传输报文流水号
   * @param dbResultMap
   */
  def genTransSerialNumber(organizationId: String) = {
    organizationId + "_" + "000000000000" + genRandomNumber(28)
  }

  /**
   * 根据TXCode生成ApplicationID
   * @param sence
   * @return
   */
  def genApplicationID(sence: BizScene) = {
    sence match {
      case ReportCase      => "0401" + genRandomNumber(32)
      case ReportDoubtCard => "0403" + genRandomNumber(32)
      case ReportDoubtAcc  => "0404" + genRandomNumber(32)
      case ReportException => "0405" + genRandomNumber(32)
    }
  }
  /**
   * 获取指定位数随机数
   * @param n
   * @return
   */
  def genRandomNumber(n: Int): String = {
    val sb = new StringBuffer();
    val random = new Random();
    for (i <- 1 to n) {
      val x = random.nextInt(9)
      sb.append(x.toString());
    }
    sb.toString()
  }
}