package build.unstable.tylog

import org.slf4j.event.Level

object Example extends App with TypedLogging {
  type TraceID = String

  sealed trait CallType

  case object A extends CallType

  case object B extends CallType

  val traceId = "1"

  // this adds callType/variation/traceID to MDC
  log.tylog(Level.TRACE, traceId, A, Variation.Attempt, "let's see..")

  // normal log statements between attempt and resolution will have MDC set
  log.debug("a message with context")

  // won't compile, ERROR is not a valid Level for tylog method
  // log.tylog(Level.ERROR, traceId, A, Variation.Success, "yay!")

  // logging Success/Failure will clear MDC
  log.tylog(Level.INFO, traceId, A, Variation.Success, "yay!")

  // placeholders and arguments are checked at compile time
  log.debug("this compiles normally {}", "msg")

  // log.info("this does not compile because there is a missing arg {}")

  // log.warning("this does not compile because there is a missing placeholder", "a")

}
