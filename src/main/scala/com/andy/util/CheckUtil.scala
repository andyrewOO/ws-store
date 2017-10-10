package com.hfbank.util

import java.util.regex.Matcher
import java.nio.file.Files
import java.io.FileInputStream
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import scala.util.parsing._
import org.json4s._
import java.util.Properties
import scala.collection.convert.Wrappers.JPropertiesWrapper
import scala.collection.mutable.ArrayBuffer
import scala.util.parsing.json.JSON
import scala.collection.mutable.HashMap


/**
 * @harveyw win7
 */
object CheckUtil {
  
  def processpre(in:String, name:String):CheckRes = {
    
    println("initial in:",in,"     name: ",name)
    val formatMap = str2map(in)
    val (procheck,rulebase,verify) = this.getPropChecks()
    val i = formatMap.keysIterator
    var bRes = true
    var sRes = ""
    var rRes = ""
    var tem:String = ""
    var locate:StringBuffer = new StringBuffer
    var sResInfo:StringBuffer = new StringBuffer
    if(verify == "ok") {
      println("base[procheck][rule]: ",procheck, rulebase)
      val preRes = this.compareMap(procheck, rulebase) 
      if(!preRes.getPass) {
        bRes = false
        sRes = preRes.getlocatedProp
        rRes = preRes.getLocatedRule + " NOT DEFINED"
      }

      while(i.hasNext && bRes==true) {
        tem = i.next()
        //var locate:StringBuffer = new StringBuffer
        //var sResInfo:StringBuffer = new StringBuffer
        sResInfo.append(name).append(".").append(tem.toString())
        locate.append(name).append(".").append(tem.toString().toLowerCase())
        if(procheck.contains(locate.toString())){
          val gogo = process(procheck(locate.toString()),formatMap(tem.toString()),locate,rulebase)
          bRes = gogo._1
          rRes = gogo._2
          if(bRes == false) {sRes = sResInfo.toString()}
        }
        locate.setLength(0)
        sResInfo.setLength(0)
      }
    }else{
      bRes = false
      sRes = verify
    }
    
    new CheckRes(bRes,sRes,rRes)
  }
  

  //校验方法
  private def process(plist:Seq[String], in:String, locate:StringBuffer, rule:Map[String,String]):(Boolean,String) = {

    var tem = plist
    var res:Boolean = true
    var sRes:String = ""
    var locateMin = new StringBuffer().append(locate).append("minlength")
    var locateMax = new StringBuffer().append(locate).append("maxlength")
    var locate9 = locate
    //locateMax.append("maxlength")
    //locateMin.append("minlength")
    if(tem.contains("notempty") || verifyLength(in,rule("notempty"),0)) {
      if(tem.contains("notempty")){
        res = verifyLength(in,rule("notempty"),0)
        tem = tem.filter(_ != "notempty")
        if(res==false){sRes = "noEmpty"}
      }
      if(res==true && tem.contains(locateMin.toString())) {
        res = verifyLength(in,rule(locateMin.toString()),1)
        tem = tem.filter(_ != locateMin.toString())
        if(res==false){sRes = "minLength"}
      }
      if(res==true && tem.contains(locateMax.toString())) {
        res = verifyLength(in,rule(locateMax.toString()),2)
        tem = tem.filter(_ != locateMax.toString())
        if(res==false){sRes = "maxLength"}
      }
      while(!tem.isEmpty && res==true) {
        println("!!! pattern: in, tem.head,propM(tem.head): ",in,tem.head,rule(tem.head))
        if(!in.matches(rule(tem.head.trim()))) {
          res = false
          sRes = tem.head
        }
        tem = tem.tail
      }
    }
    (res,sRes)
  }
  
  
  //正则校验
  private def verifyPattern(input:String, regex:String):Boolean = {
    input.matches(regex)
  }
  
  //长度、非空校验
  private def verifyLength(input:String, limits:String, status:Int):Boolean = {
    var res = true
    limits match {
      case m:String => {
        if ( "noNull" == m ) {
          if(null == input || "" == input.trim()){
            res = false
          }
        }else{
          if(status==1 && input.getBytes("UTF-8").length < limits.toInt ) {
            res = false
          }
          if(status==2 && input.getBytes("UTF-8").length > limits.toInt ) {
            res = false
          }
        }
      }
    }
    res
  }
  
  
  //解析成对应bean的相应属性配置规则，返回每个属性对应的校验名
  private def getBeanProp[T](in:Option[String])(implicit beanType:Manifest[T]) = {
    val r = in match {
      case Some(a) => {
        val jsonObj = parse(a)
        implicit val formats = DefaultFormats
        jsonObj.extract[T]
      }
    }
    println(r)
    r
  }
  
  
  //读取配置文件，属性对应的校验名，校验名对应的正则配置
  private def getPropChecks():(Map[String,Seq[String]],Map[String,String],String) = { //when replace with =
    var res1 = new HashMap[String,Seq[String]]
    var res2 = new HashMap[String,String]
    var note = "ok"
    val fileConfPath = "checkConf/a_sysFileIndex.properties"
    val ruleConfPath = "checkConf/a_sysGeneralRule.properties"
    
    val mfiles = this.getPropMapFromPath(fileConfPath)._1
    val defaultRFile = this.getPropMapFromPath(ruleConfPath)._1

    val ifiles = mfiles.keysIterator
    var t:String = ""
    var filePath:String = ""
    while(ifiles.hasNext && note=="ok") {
      t = ifiles.next().toString()
      filePath = "checkConf/"+t+".properties"
      println("[filePath]:", filePath)
        
      if(getPropMapFromPath(filePath)._2 != "ok") {
        note = getPropMapFromPath(filePath)._2
      }else{
        val propMap = this.getPropMapFromPath(filePath)._1
        val splitRes = this.splitSubFile(propMap, t)
        val mFileProp = splitRes._1
        var mFileRulePre = splitRes._2
      
        var iFileProp = mFileProp.keysIterator
        var iFileRulePre = mFileRulePre.keysIterator
        var tem = ""
        while(iFileProp.hasNext) {
          tem = iFileProp.next().toString()
          res1.put(tem, mFileProp(tem))
      }
      while(iFileRulePre.hasNext) {
        tem = iFileRulePre.next().toString()
        res2.put(tem, mFileRulePre(tem))
      }
      }
    }
    var i = defaultRFile.keysIterator
    var tem = ""
    while(i.hasNext) {
      tem = i.next()
      res2.put(tem.toLowerCase(), defaultRFile(tem))
    }

    (res1.toMap,res2.toMap,note)
  }
  
  private def splitSubFile(inPF:scala.collection.mutable.Map[String,String],msgName:String):(HashMap[String,Seq[String]],HashMap[String,String]) = {
    var t:String = ""
    var temk:String = ""
    var temv1:Seq[String] = null
    var temv2:String = null
    var res1 = new HashMap[String,Seq[String]]
    var res2 = new HashMap[String,String]
    val i = inPF.keysIterator
    while(i.hasNext) {
      t = i.next().toString()
      if(t.split("\\.").head.trim() == "prop"){
        temk = t.split("\\.").tail.head.toLowerCase().trim()
        temv1 = inPF(t).toLowerCase().split(",").toSeq
        temv1 = trimSeq(temv1)
        res1.put(msgName+"."+temk, temv1)
      }
      if(t.split("\\.").head.trim() == msgName){
        temk = t.toLowerCase().trim()
        temv2 = inPF(t).toLowerCase().trim()
        res2.put(temk, temv2)//需要检查属性对应的规则是否都有
      }
      
    }
    (res1,res2)
  }
  
  private def mapCopyAll(a:HashMap[String,Any],b:HashMap[String,Any]):HashMap[String,Any] = {
    var i =b.keysIterator
    var t = ""
    while(i.hasNext) {
      t = i.next()
      a.put(t.toString(), b(t.toString())) //形参传数顺序，前后随机？影响是否先next了
    }
    a
  }
  
  private def getPropMapFromPath(path:String): (scala.collection.mutable.Map[String,String], String) = {
    var msg:String = "ok"
    val stream = CheckUtil.getClass.getClassLoader.getResourceAsStream(path)
    val prop = new Properties
    if(null == stream) {
      msg = path+"  NOT EXIST!"
      println("stream null?",msg)
    }else{
      prop.load(stream)
      stream.close()
    }
    
    val map = prop match {
      case null => null
      case _ => new JPropertiesWrapper(prop)
    }
    
    (map,msg)
    
  }
  
  private def trimSeq(in:Seq[String]):Seq[String] = {
    var res:Seq[String] = Seq()
    var tem = in
    while(!tem.isEmpty){
      res = res.+:(tem.head.toString().trim())
      tem = tem.tail
    }
    res
  }
  
  private def compareMap(m:scala.collection.immutable.Map[String,Seq[String]],n:scala.collection.immutable.Map[String,String]):CheckRes = {
    var res:Boolean = true
    var prop = ""
    var rule = ""
    var i = m.keysIterator
    var t:String = ""
    while(i.hasNext) {
      t = i.next().toString()
      var tem = m(t)
      while(!tem.isEmpty && res==true) {
        if(!n.contains(tem.head.trim())) {
          res = false
          prop = t
          rule = tem.head
        }
        tem = tem.tail
      }
    }
    new CheckRes(res,prop,rule)
  }
  
  private def mapKeyTrans(m:scala.collection.mutable.Map[String,String]): scala.collection.mutable.Map[String,String] = {
    val i = m.keysIterator
    var n = ""
    var r = new scala.collection.mutable.HashMap[String,String]
    while(i.hasNext) {
      n = i.next()
      r.put(n.toLowerCase(), m(n))
    }
    r
  }
  
  private def reachMap(in:Map[String,Any]) = {
    val res = in match {
      case a:Map[String,Seq[String]] => {
        println("got in reach map match 1 [reachMap]:",a) 
      }
      case _ => {
        println("error match [reachMap]")
      }
    }
  }
  
  
  private def str2map(in:String):Map[String,String] = {
    var temMap = Map[String, String]()
    var temp:Any = null
    
		try {
		  temp = JSON.parseFull(in).get
		} catch {
		  case e:Exception => {
		    println("JSON-format-error: "+e.getMessage)
		  }
    }
		
	  temp match{
		    case s:Map[String, String] => {temMap = s; println("sssssssssssss: "+s)}
		    case _ => {println(111)}
		  }
	  
	  temMap
  }
  
}

class CheckRes(pass:Boolean,locatedProp:String,locatedRule:String){
  def getPass:Boolean = {
    return this.pass
  }
  def getlocatedProp:String = {
    return this.locatedProp
  }
  def getLocatedRule:String = {
    return this.locatedRule
  }

}