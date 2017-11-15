package com.andy.action

import xitrum.ActorAction
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.util.parsing.json.JSON
import org.json4s.DefaultFormats
import scala.reflect.ClassTag

private[action] trait BaseAppAction extends ActorAction {
  var params: Map[String, String] = null
  //验证是否登录
  private[action] def checkLogin {}
  //验证是否有该功能权限
  private[action] def checkAuthorization {}
  //求求参数是否合法
  private[action] def checkParam {}
  //获取参数
  private[action] def getParam {
    if (this.requestContentString != null && this.requestContentString.length() > 0) {
      JSON.parseFull(this.requestContentString).get match {
        case params: Map[_, _] => {
          this.params = params.asInstanceOf[Map[String, String]]
        }
      }
      /*SeriDeseri.fromJson[Map[String,String]](this.requestContentString)*/
    }
  }
  /**
   * 前置过滤器
   */
  beforeFilter {
    log.debug("进入Action前置过滤器")
    checkLogin
    checkAuthorization
    checkParam
    getParam
    log.debug("Action前置过滤器结束。")
  }
  /**
   * 前后过滤器
   */
  aroundFilter {
    action =>
      try {
        action()
      } catch {
        case ex: xitrum.exception.MissingParam => {
          val msg = ex.getMessage
          respondJson("error" + msg)
        }
      }
  }
  protected def getCurrentUserName(): String = {
    var userId = ""
    val loginUser: Option[Any] = session.get("LoginUser")
    if (loginUser.nonEmpty && loginUser.get.isInstanceOf[String]) {
      var value = loginUser.asInstanceOf[Option[String]]
      value match {
        case Some(value) => userId = value.toString()
        case None        =>
      }
    }
    userId
  }
  protected def getCurrentUserRole(): String = {
    var userId = ""
    val loginUser: Option[Any] = session.get("user_role")
    if (loginUser.nonEmpty && loginUser.get.isInstanceOf[String]) {
      var value = loginUser.asInstanceOf[Option[String]]
      value match {
        case Some(value) => userId = value.toString()
        case None        =>
      }
    }
    userId
  }

  /**
   * 根据JSON,返回泛型所对应的对象
   */
  protected def getBean[A: Manifest](p: Option[String]): A = {
    val bean = p match {
      case Some(jsonString) => {
        val json = parse(jsonString)
        getBean[A](json)
      }
    }
    bean
  }

  protected def getBean[A: Manifest](jsonString: String): A = {
    val bean = jsonString match {
      case x: String =>
        val json = parse(x)
        getBean[A](json)
    }
    bean
  }

  /**
   * 根据JValue 获取泛型所对应的对象
   */
  protected def getBean[A: Manifest](json: JValue): A = {
    implicit val formats = DefaultFormats
    json.extract[A]
  }
}