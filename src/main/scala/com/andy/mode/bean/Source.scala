package com.hfbank.mode.bean

/**
 * @author andy
 */
case class SourceDB(
    id: String,
    dbType: String,
    dbName: String,
    url: String,
    dbUserName: String,
    password: String,
    center: String,
    description: String,
    creationTime: String,
    updatedTime: String,
    creator: String,
    updater: String,
    status: String
)
    
case class SourceEngine(
    id: String,
    clusterName: String,
    url: String,
    hostList: String,
    standard: String,
    center: String,
    description: String,
    creationTime: String,
    updatedTime: String,
    creator: String,
    updater: String,
    status: String
)

case class SourceQueue(
  id:String,
  name:String,
  mqType:String,
  messageid:String,
  hostList:String,
  topic:String,
  center:String,
  description:String,
  creationtime:String,
  creator:String,
  updatedtime:String,
  updater:String,
  status:String    
)