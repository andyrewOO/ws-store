package com.andy.actor.sys

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.WrappedArray
import scala.collection.immutable.Map
import scala.beans.BeanProperty
import xitrum.util.Loader
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * @author
 */
class MenuTreeService {

}

object MenuTreeService {
  /**
   * 按JSON树结构查询所有有效的菜单
   */
  def findMenuTreeAll(): List[_] = {
    val sourceList = getTreeMenu()
    genTree(sourceList)
  }

  /**
   * 得到所有的菜单列表
   * @return
   */
  private def getTreeMenu(): List[MenuInfo] = {
    implicit val formats = DefaultFormats
    val jsonStr = Loader.stringFromClasspath("sys/menu2.json")
    val json = parse(jsonStr)
    val list = json.extract[List[MenuInfo]]
    //初始化子节点为空
    list.map { x =>
      x.copy(
        children = Some(ArrayBuffer[MenuInfo]()))
    }
  }

  /**
   *
   * @param sourceList
   * @return
   */
  private def genTree(sourceList: List[MenuInfo]): List[MenuInfo] = {
    //取出一级菜单
    val toplevelMenu = sourceList.filter { m => m.parent.equals("#") }.sortWith(MenuSort)
    //采用递归的方式为一级菜单组装树形结构
    toplevelMenu.foreach { x => addSubTree(sourceList, x) }
    toplevelMenu
  }

  private def MenuSort(e1: MenuInfo, e2: MenuInfo) = e1.sort <= e2.sort

  /**
   * 为指定菜单添加子菜单,递归调用,直至无子菜单为止
   * @param newlist
   * @param upMenu
   */
  private def addSubTree(newlist: List[MenuInfo], upMenu: MenuInfo): Unit = {
    //过滤子菜单并排序
    val subList: List[MenuInfo] = newlist.filter { m => m.parent.equals(upMenu.id) }.sortWith(MenuSort)
    subList.foreach { x =>
      //递归调用,继续添加子菜单
      addSubTree(newlist, x)
      upMenu.children.map {
        xm => xm.append(x)
      }
    }
  }
}

/**
 * 查询菜单JSON返回值封装类
 * 返回：data:数据; children:子集
 */
case class MenuInfo(
  @BeanProperty id: String,
  @BeanProperty label: String,
  @BeanProperty icon: String,
  @BeanProperty parent: String,
  @BeanProperty sort: Int,
  @BeanProperty templateUrl: String,
  @BeanProperty loadfiles: String,
  @BeanProperty var children: Option[ArrayBuffer[MenuInfo]])