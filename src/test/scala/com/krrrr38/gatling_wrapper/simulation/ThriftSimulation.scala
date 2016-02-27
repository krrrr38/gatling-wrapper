package com.krrrr38.gatling_wrapper.simulation

import akka.actor.ActorRef
import com.example.thrift.HelloService
import com.krrrr38.gatling_wrapper.core.{ CustomSimulation, SimulationExecutor }
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.session.Session
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.THttpClient

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

object ThriftAction {
  private val CLIENT = {
    val cm = new PoolingHttpClientConnectionManager
    cm.setMaxTotal(500)
    cm.setDefaultMaxPerRoute(500)
    val httpClient = HttpClients.custom
      .setConnectionManager(cm)
      .build
    val tHttpClient = new THttpClient("localhost:8080", httpClient)
    tHttpClient.open()
    sys.addShutdownHook(tHttpClient.close())
    new HelloService.Client(new TBinaryProtocol(tHttpClient))
  }
}

class ThriftAction(val next: ActorRef) extends CustomSimulation[String] {
  override val buildAction = (session: Session) => {
    // param
    if (Random.nextInt() % 2 == 0)
      "foo"
    else
      "bar"
  }

  override val executeAction = (param: String) => {
    ThriftAction.CLIENT.hello(param)
    ()
  }
}
