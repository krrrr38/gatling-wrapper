package com.krrrr38.gatling_wrapper.core

import akka.actor.{ ActorRef, Props }
import io.gatling.core.Predef._
import io.gatling.core.action.Chainable
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.scenario.Simulation

/**
 * simulation executor includes simulation config
 */
trait SimulationExecutor extends Simulation {
  /**
   * 95 percent tile threshold
   */
  val responseTime95: Int

  /**
   * action
   */
  val action: ActorRef => Chainable

  /**
   * execute steps
   * <p>
   * e.g.
   * // access $1 req/sec for $2 secs
   * constantUsersPerSec(10) during (20 seconds),
   * // access from $1 req/sec to $2 req/sec in $3 secs
   * rampUsersPerSec(1) to 20 during (10 seconds)
   */
  val iss: Iterable[InjectionStep]

  val customRequestAction = new ActionBuilder {
    override def build(next: ActorRef, protocols: Protocols) = {
      system.actorOf(Props(action(next)))
    }
  }
  val scn = scenario("Scenario Name")
    .exec(customRequestAction)

  setUp(
    scn.inject(iss)
  ).assertions(
      global.responseTime.percentile4.lessThan(responseTime95),
      global.successfulRequests.percent.greaterThan(95)
    )
}
