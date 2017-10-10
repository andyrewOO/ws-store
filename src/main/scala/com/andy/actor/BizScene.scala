package com.hfbank.actor

/**
 * @author andy
 */
sealed trait BizScene

//租户-用户新增
case object TenantUserAddBiz extends BizScene
//租户-用户查询
case object TenantUserSearchBiz extends BizScene
//租户-用户更新
case object TenantUserUpdateBiz extends BizScene

//租户-应用新增
case object TenantAPPAddBiz extends BizScene
//租户-应用查询
case object TenantAPPSearchBiz extends BizScene
//租户-应用更新
case object TenantAPPUpdateBiz extends BizScene

//租户-分区新增
case object TenantPartionAddBiz extends BizScene
//租户-分区查询
case object TenantPartionSearchBiz extends BizScene
//租户-分区更新
case object TenantPartionUpdateBiz extends BizScene

//租户-报文新增
case object TenantMessageAddBiz extends BizScene
//租户-报文查询
case object TenantMessageSearchBiz extends BizScene
//租户-报文更新
case object TenantMessageUpdateBiz extends BizScene

//租户-接口新增
case object TenantInterfaceAddBiz extends BizScene
//租户-接口查询
case object TenantInterfaceSearchBiz extends BizScene
//租户-接口更新
case object TenantInterfaceUpdateBiz extends BizScene

//租户-资源新增
case object TenantSourceAddBiz extends BizScene
//租户-资源查询
case object TenantSourceSearchBiz extends BizScene
case object TenantSourceVSearchBiz extends BizScene
//租户-资源更新
case object TenantSourceUpdateBiz extends BizScene

//资源-数据库新增
case object SourceDBAddBiz extends BizScene
//资源-数据库查询
case object SourceDBSearchBiz extends BizScene
//资源-数据库更新
case object SourceDBUpdateBiz extends BizScene

//资源-队列新增
case object SourceQueueAddBiz extends BizScene
//资源-队列查询
case object SourceQueueSearchBiz extends BizScene
//资源-队列更新
case object SourceQueueUpdateBiz extends BizScene

//资源-引擎新增
case object SourceEngineAddBiz extends BizScene
//资源-引擎查询
case object SourceEngineSearchBiz extends BizScene
//资源-引擎更新
case object SourceEngineUpdateBiz extends BizScene