import sbt._
import Keys._
import _root_.play.sbt.Play.autoImport._

object Dependencies {

  object cassandra {
    val all  		= "org.apache.cassandra" % "cassandra-all" % "2.2.1"
    val core  		= "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.0"
    val mapping    	= "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.1.0"
    val test    	= "org.cassandraunit" % "cassandra-unit-shaded" % "2.1.9.2" % "test"
    val yaml		= "org.yaml" % "snakeyaml" % "1.15" % "test"
    val hector		= "org.hectorclient" % "hector-core" % "2.0-0" % "test"
  }
  
  object emju {
    val common		= "emju-common" % "emju-common_2.11" % "2.18.0"
    val allocation	= "emju-allocation" % "emju-allocation_2.11" % "2.19.1.1"
  }

  val json		= "com.fasterxml.jackson.core" % "jackson-core" % "2.6.1"
  val slf4j		= "org.slf4j" % "slf4j-api" % "1.7.5"	
  val mockito		= "org.mockito" % "mockito-core" % "1.10.19" % "test"
  var hamcrest 		= "org.hamcrest" % "hamcrest-all" % "1.3" % "test"		
  val test		= "com.lordofthejars" % "nosqlunit-cassandra" % "0.8.1" % "test"
  val jedis     	= "redis.clients" % "jedis" % "2.9.0"
  val qpidamqp 		= "org.apache.qpid" % "qpid-amqp-1-0-client-jms" % "0.32"
  val jms      		= "org.apache.geronimo.specs" % "geronimo-jms_1.1_spec" % "1.1.1"
  val gson 		= "com.google.code.gson" % "gson" % "2.3.1"
  val logbackCore = "ch.qos.logback" % "logback-core" % "1.2.1"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.1"

  val playDependencies: Seq[ModuleID] = Seq(
    cache,
    cassandra.test,
    cassandra.all,
    cassandra.core,
    cassandra.mapping,
    cassandra.hector,
    jedis,
    slf4j,
    mockito,
    hamcrest,
    test,
    filters,
    gson, 
    emju.common,
    emju.allocation,
    logbackCore,
    logbackClassic
  )

  val serviceDependencies: Seq[ModuleID] = playDependencies ++ Seq(
    javaWs,
    cache,
    json,
    slf4j,
    qpidamqp,
    jms
  )

  val webDependencies: Seq[ModuleID] = playDependencies ++ serviceDependencies

}
