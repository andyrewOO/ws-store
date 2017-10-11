package com.andy.actor

/**
 * @author andy
 */
sealed trait BizScene

//商品新增
case object MGoodsAddBiz extends BizScene
//商品查询
case object MGoodsSearchBiz extends BizScene
//商品更新
case object MGoodsUpdateBiz extends BizScene

//订单新增
case object MOrderAddBiz extends BizScene
//订单查询
case object MOrderSearchBiz extends BizScene
//订单更新
case object MOrderUpdateBiz extends BizScene

//客户新增
case object MPeopleAddBiz extends BizScene
//客户查询
case object MPeopleSearchBiz extends BizScene
//客户更新
case object MPeopleUpdateBiz extends BizScene

//收货地址新增
case object MReAddrAddBiz extends BizScene
//收货地址查询
case object MReAddrSearchBiz extends BizScene
//收货地址更新
case object MReAddrUpdateBiz extends BizScene