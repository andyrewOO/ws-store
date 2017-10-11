package com.andy.util

import java.net.URLEncoder
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import org.json4s.jackson.JsonMethods.mapper
import xitrum.Log.error
import xitrum.Log
import java.util.Properties
import scala.collection.JavaConversions.mapAsScalaMap
import com.typesafe.config.ConfigFactory

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

  def getTime(): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
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
  def getTimeStampStr(): String = {
    val dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    dateFormat.format(System.currentTimeMillis())
  }

  /**
   * 获取当前时间戳对象
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
   * 获取startTime,间隔intervalTime之后的某刻时间
   */
  def getIntervalTime(startTime: String, fmt: String, rtime: Long): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat(fmt);
    val dt = sdf.parse(startTime)
    val newDate = new Date(dt.getTime + rtime)
    sdf.format(newDate);
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

  /**
   * 获取上月的第一天和最后一天
   */
  def getLastDayofMonth(month: String) = {
    // 格式输出
    val dateStr = month + "-01";
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val cDate = sdf.parse(dateStr)
    val caldar: Calendar = Calendar.getInstance
    caldar.setTime(cDate)
    // 获取最后一天的索引
    val maxDays: Int = caldar.getActualMaximum(Calendar.DAY_OF_MONTH)
    // 设置到最后一天
    caldar.set(caldar.get(Calendar.YEAR), caldar.get(Calendar.MONTH), maxDays)

    val firstDay: String = sdf.format(caldar.getTime).substring(0, 8) + "01"
    val lastDay = sdf.format(caldar.getTime)
    (firstDay, lastDay)
  }

  /**
   * 获取指定日期范围内的所有的日期集合
   */
  def getArrayDate(start: String, end: String) = {
    val caldar = Calendar.getInstance
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val dStart = sdf.parse(start)
    val dEnd = sdf.parse(end)
    val al = ArrayBuffer[String]()
    al += start

    val cBegin = Calendar.getInstance
    cBegin.setTime(dStart)
    val cEnd = Calendar.getInstance
    cEnd.setTime(dEnd)
    while (dEnd.after(cBegin.getTime)) {
      cBegin.add(Calendar.DAY_OF_MONTH, 1)
      al += sdf.format(cBegin.getTime)
    }
    al
  }

  /**
   * 获取指定月份之间的所有月份
   */
  def getMonthBetween(start: String, end: String) = {
    val caldar = Calendar.getInstance
    val sdf = new SimpleDateFormat("yyyy-MM")
    val dStart = sdf.parse(start)
    val dEnd = sdf.parse(end)
    val a1 = ArrayBuffer[String]()
    a1 += start

    val cBegin = Calendar.getInstance
    cBegin.setTime(dStart)
    val cEnd = Calendar.getInstance
    cEnd.setTime(dEnd)
    while (cBegin.before(cEnd)) {
      cBegin.add(Calendar.MONTH, 1)
      a1 += sdf.format(cBegin.getTime)
    }
    a1
  }

  /*  def main(args: Array[String]): Unit = {
    println(this.getMonthBetween("2015-06","2016-02"))
  }*/

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
  
  //获取properties配置文件的信息
  def getPropInfo(path: String): Map[String, String] = {
    val in = HfbkUtil.getClass.getClassLoader.getResourceAsStream(path)
    val prop = new Properties
    prop.load(in)
    in.close()
    scala.collection.JavaConversions.propertiesAsScalaMap(prop)
  }
}

object PropertiesUtil {
  implicit val configLoader = ConfigFactory.defaultApplication()
  def getString(key: String): String = {
    configLoader.getString(key)
  }
  def getInt(key: String): Int = {
    configLoader.getInt(key)
  }
  def getIntList(key: String): java.util.List[Integer] = {
    configLoader.getIntList(key)
  }
  def getStringList(key: String): java.util.List[String] = {
    configLoader.getStringList(key)
  }
}
