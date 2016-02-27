package com.krrrr38.gatling_wrapper.simulation

import akka.actor.ActorRef
import com.example.thrift.HelloService
import com.krrrr38.gatling_wrapper.core.{ CustomSimulation, SimulationExecutor }
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.session.Session
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket

import scala.concurrent.duration._
import scala.util.Random

class ThriftSimulationExecutor extends SimulationExecutor {
  lazy val responseTime95 = 150
  lazy val action = new ThriftAction(_)

  // request for 5 * 5 secs
  // 10 req/sec in first 5 secs
  // 20 req/sec in next 5 secs
  // ...
  lazy val iss: Iterable[InjectionStep] =
    List(10, 20, 30, 40, 50).map(reqPerSec => {
      constantUsersPerSec(reqPerSec) during (5 seconds)
    })
}

class ThriftAction(val next: ActorRef) extends CustomSimulation[String] {
  var transport: TSocket = null
  var client: HelloService.Client = null

  override val buildAction = (session: Session) => {
    // initialize
    transport = new TSocket("localhost", 8080)
    client = new HelloService.Client(new TBinaryProtocol(transport))
    transport.open()
    // param
    if (Random.nextInt() % 2 == 0)
      "foo"
    else
      "bar"
  }

  override val executeAction = (param: String) => {
    try {
      client.hello(param)
      ()
    } finally {
      transport.close()
    }
  }
}
