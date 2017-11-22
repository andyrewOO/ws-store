package com.andy.action

import com.andy.actor.ActorHolder
import com.andy.mode.TEvent
import com.andy.util.HfbkUtil
import com.andy.actor.BaseBiz
import com.andy.actor.BizScene
import com.andy.constant.BeanUnit
import com.andy.actor.MReAddrAddBiz
import com.andy.constant.OperType
import com.andy.actor.MGoodsAddBiz
import com.andy.actor.MPeopleUpdateBiz
import com.andy.actor.MOrderSearchBiz
import com.andy.actor.MGoodsSearchBiz
import com.andy.actor.MGoodsUpdateBiz
import com.andy.actor.MOrderAddBiz
import com.andy.actor.MReAddrUpdateBiz
import com.andy.actor.MPeopleAddBiz
import com.andy.actor.MReAddrSearchBiz
import com.andy.actor.MOrderUpdateBiz
import com.andy.actor.MPeopleSearchBiz

private[action] trait AppAction extends BaseAppAction {

  //业务场景
  var BIZ_SCENE: BizScene = BaseBiz
  def execute(): Unit = {
    try {
      //获取请求对应的业务场景
      val operType = request.getUri.split("/")(1)
      BIZ_SCENE = judgeBiz(param("unit"), operType)
      //处理请求入参，转变为内置对象
      val oBean = handle
      //将消息组装成业务处理消息，发送给业务模块
      ActorHolder.proxyActor ! TEvent(HfbkUtil.getUUID(), BIZ_SCENE, oBean.get, HfbkUtil.getTime(), Some(self))
      context.become(receiveResponse)
    } catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    }

  }

  /**
   * 返回对应的业务场景
   */
  protected def judgeBiz(unit: String, operTye: String) = {
    (unit, operTye) match {
      case (BeanUnit.MGOODS, OperType.SEARCH)  => MGoodsSearchBiz
      case (BeanUnit.MGOODS, OperType.UPDATE)  => MGoodsUpdateBiz
      case (BeanUnit.MGOODS, OperType.ADD)     => MGoodsAddBiz

      case (BeanUnit.MORDER, OperType.SEARCH)  => MOrderSearchBiz
      case (BeanUnit.MORDER, OperType.UPDATE)  => MOrderUpdateBiz
      case (BeanUnit.MORDER, OperType.ADD)     => MOrderAddBiz

      case (BeanUnit.MPEOPLE, OperType.SEARCH) => MPeopleSearchBiz
      case (BeanUnit.MPEOPLE, OperType.UPDATE) => MPeopleUpdateBiz
      case (BeanUnit.MPEOPLE, OperType.ADD)    => MPeopleAddBiz

      case (BeanUnit.MREADDR, OperType.SEARCH) => MReAddrSearchBiz
      case (BeanUnit.MREADDR, OperType.UPDATE) => MReAddrUpdateBiz
      case (BeanUnit.MREADDR, OperType.ADD)    => MReAddrAddBiz
    }
  }

  def handle(): Option[_]
  def receiveResponse: Receive
}