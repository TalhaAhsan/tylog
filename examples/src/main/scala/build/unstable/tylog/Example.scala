package build.unstable.tylog

import org.slf4j.LoggerFactory

object Example extends App with TypedLogging {
  type TraceID = String

  sealed trait CallType

  case object A extends CallType

  case object B extends CallType

  val log = LoggerFactory.getLogger(this.getClass)

  //this adds callType/variation/traceID to MDC context
  trace(log, "1", A, Variation.Attempt, "{}", "we attempted a")

  trace(log, "1", B, Variation.Success, "{}", "B succeeded")

  //it also checks placeholders/args at compile time
  //trace(log, "1", B, Variation.Success, "{}")

  //this compiles
  debug(log, "an interesting message: {}", "msg")

  //this doesn't compile
  //info(log, "an interesting message: {}")

  //neither does this
  //warning(log, "an interesting message:", "a")

}
