package com.krrrr38.gatling_wrapper

import akka.actor.ActorRef
import com.krrrr38.gatling_wrapper.core.{ CustomSimulation, SimulationExecutor }
import io.gatling.core.Predef
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.session.Session
import org.apache.http.client.fluent.{ Executor, Request }
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import scala.concurrent.duration._
import scala.util.Random

class HttpSimulationExecutor extends SimulationExecutor {
  lazy val responseTime95 = 150
  lazy val action = new HttpAction(_)

  // request for 5 * 5 secs
  // 10 req/sec in first 5 secs
  // 20 req/sec in next 5 secs
  // ...
  lazy val iss: Iterable[InjectionStep] =
    List(10, 20, 30, 40, 50).map(reqPerSec => {
      constantUsersPerSec(reqPerSec) during (5 seconds)
    })
}

class HttpAction(val next: ActorRef) extends CustomSimulation[String] {
  val request = {
    val cm = new PoolingHttpClientConnectionManager
    cm.setMaxTotal(500)
    cm.setDefaultMaxPerRoute(500)
    val client = HttpClients.custom
      .setConnectionManager(cm)
      .build
    Executor.newInstance(client)
  }

  override val buildAction = (session: Session) =>
    if (Random.nextInt() % 2 == 0)
      "foo"
    else
      "bar"

  override val executeAction = (param: String) => {
    request.execute(Request.Get(s"http://localhost:8080/ping?param=$param"))
      .discardContent()
  }
}
