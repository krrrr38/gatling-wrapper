package com.krrrr38.gatling_wrapper.simulation

import akka.actor.ActorRef
import com.krrrr38.gatling_wrapper.core.{ CustomSimulation, SimulationExecutor }
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.session.Session

import scala.concurrent.duration._

class ScalaSimulationExecutor extends SimulationExecutor {
  lazy val responseTime95 = 10
  lazy val action = new ScalaAction(_)

  lazy val iss: Iterable[InjectionStep] = List(
    constantUsersPerSec(10) during (5 seconds)
  )
}

class ScalaAction(val next: ActorRef) extends CustomSimulation[Session] {
  override val buildAction = identity[Session] _

  override val executeAction = (session: Session) => {
    println("hello")
  }
}
