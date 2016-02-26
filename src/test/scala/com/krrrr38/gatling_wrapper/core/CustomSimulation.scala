package com.krrrr38.gatling_wrapper.core

import io.gatling.core.Predef._
import io.gatling.core.action.Chainable
import io.gatling.core.result.message.{KO, OK}
import io.gatling.core.result.writer.{DataWriter, RequestMessage}
import io.gatling.core.session.Session

trait CustomSimulation extends Chainable {
  val executeAction: (Session) => Unit

  def execute(session: Session) {
    var status: Status = OK
    var start: Long = 0L
    var end: Long = 0L
    var errorMessage: Option[String] = None

    try {
      start = System.currentTimeMillis
      executeAction(session)
      end = System.currentTimeMillis;
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.warn(e.getMessage, e)
        status = KO
    } finally {
      val requestStartDate, requestEndDate = start
      val responseStartDate, responseEndDate = end
      val requestName = s"Request for ${session.userId} (${session.scenarioName})"
      val message = errorMessage
      val extraInfo = Nil
      DataWriter.dispatch(RequestMessage(
        session.scenarioName,
        session.userId,
        session.groupHierarchy,
        requestName,
        requestStartDate,
        requestEndDate,
        responseStartDate,
        responseEndDate,
        status,
        message,
        extraInfo))
      next ! session
    }
  }
}
