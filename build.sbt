organization:="sjq.sbt"
name:="wStore"
version:="0.1-SNAPSHOT"
scalaVersion:="2.11.7"

scalacOptions ++= Seq("-deprecation","-feature","-unchecked")

// xitrum requires Java 8
javacOptions ++= Seq("-source", "1.8", "1.8")

// for xitrum
libraryDependencies += "tv.cntt" %% "xitrum" % "3.26.0"
libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.8"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.4"
libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "2.5"

// Xitrum uses SLF4J, an implementation of SLF4J is needed
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

// For writing condition in logback.xml
libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.8"

// for scalaj-http
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

// for json4s
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.3.0"

// https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2
libraryDependencies += "org.apache.commons" % "commons-dbcp2" % "2.0.1"

// https://mvnrepository.com/artifact/commons-dbutils/commons-dbutils
libraryDependencies += "commons-dbutils" % "commons-dbutils" % "1.6"

// https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils
libraryDependencies += "commons-beanutils" % "commons-beanutils" % "1.9.2"

// https://mvnrepository.com/artifact/com.jcraft/jsch
libraryDependencies += "com.jcraft" % "jsch" % "0.1.53"

// https://mvnrepository.com/artifact/com.esotericsoftware.kryo/kryo
libraryDependencies += "com.esotericsoftware.kryo" % "kryo" % "2.21"

// https://mvnrepository.com/artifact/com.esotericsoftware.minlog/minlog
libraryDependencies += "com.esotericsoftware.minlog" % "minlog" % "1.2"


// https://mvnrepository.com/artifact/org.objenesis/objenesis
libraryDependencies += "org.objenesis" % "objenesis" % "1.2" % "test"

// https://mvnrepository.com/artifact/com.esotericsoftware.reflectasm/reflectasm
libraryDependencies += "com.esotericsoftware.reflectasm" % "reflectasm" % "1.07"

// https://mvnrepository.com/artifact/org.ow2.asm/asm
libraryDependencies += "org.ow2.asm" % "asm" % "4.0"

// https://mvnrepository.com/artifact/oracle/ojdbc6
//libraryDependencies += "oracle" % "ojdbc6" % "11.2.0.3"

// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.44"


// Precompile Scalate templates
seq(scalateSettings:_*)

ScalateKeys.scalateTemplateConfig in Compile := Seq(TemplateConfig(
 baseDirectory.value / "src" / "main" / "scalate",
 Seq(),
 Seq(Binding("helper","xitrum.Action",true))
))

// 注释插件 xgettext i18n translation key string extractor is a compiler plugin
autoCompilerPlugins := true
addCompilerPlugin("tv.cntt" %% "xgettext" % "1.3")
scalacOptions += "-P:xgettext:xitrum.I18n"

// Put config directory in classpath for easier development

// For "sbt console"
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "config")}
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "module")}
// For "sbt run"
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "config")}

// Copy these to target/xitrum when sbt xitrum-package is run
XitrumPackage.copy("config", "public", "script")