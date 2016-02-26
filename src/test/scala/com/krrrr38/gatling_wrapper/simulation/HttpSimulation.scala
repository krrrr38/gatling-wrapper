package com.krrrr38.gatling_wrapper

import akka.actor.ActorRef
import com.krrrr38.gatling_wrapper.core.{CustomSimulation, SimulationExecutor}
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.session.Session
import org.apache.http.client.fluent.{Executor, Request}
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import scala.concurrent.duration._

class HttpSimulationExecutor extends SimulationExecutor {
  lazy val responseTime95 = 150
  lazy val action = new HttpAction(_)

  // request for 5 * 5 secs
  // 10 req/sec in first second
  // 20 req/sec in next second
  // ...
  lazy val iss: Iterable[InjectionStep] =
    List(10, 20, 30, 40, 50).map(reqPerSec => {
      constantUsersPerSec(reqPerSec) during (5 seconds)
    })
}

class HttpAction(val next: ActorRef) extends CustomSimulation {
  val request = {
    val cm = new PoolingHttpClientConnectionManager
    cm.setMaxTotal(500)
    cm.setDefaultMaxPerRoute(500)
    val client = HttpClients.custom
      .setConnectionManager(cm)
      .build
    Executor.newInstance(client)
  }

  override val executeAction = (session: Session) => {
    request.execute(Request.Get("http://localhost:8080/ping"))
      .discardContent()
  }
}
