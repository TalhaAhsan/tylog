package build.unstable.tylog

import org.slf4j.Logger

import scala.language.experimental.macros

trait TypedLogging {

  type CallType
  type TraceID

  sealed abstract class Variation

  object Variation {

    case object Attempt extends Variation {
      override def toString: String = "Attempt"
    }

    case object Success extends Variation {
      override def toString: String = "Success"
    }

    case class Failure(e: Throwable) extends Variation {
      override def toString: String = "Failure"
    }

  }

  protected def debug(log: Logger, template: String, arg: Any*): Unit = macro Macros.debug

  protected def info(log: Logger, template: String, arg: Any*): Unit = macro Macros.info

  protected def error(log: Logger, error: Throwable, template: String, arg: Any*): Unit = macro Macros.error

  protected def warning(log: Logger, template: String, arg: Any*): Unit = macro Macros.warning

  protected def trace(log: Logger, traceId: TraceID,
                      callType: CallType, variation: Variation,
                      template: String, arg: Any*): Unit = macro Macros.trace[TraceID, CallType, Variation]
}
