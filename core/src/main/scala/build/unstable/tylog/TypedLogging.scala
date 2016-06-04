package build.unstable.tylog

import org.slf4j.{Logger, MDC}

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

  protected def trace(log: Logger, traceId: TraceID, c: CallType, v: Variation, message: Option[String] = None): Unit = {
    MDC.put(TypedLogging.traceIdKey, traceId.toString)
    MDC.put(TypedLogging.callTypeKey, c.toString)
    MDC.put(TypedLogging.variationKey, v.toString)
    v match {
      case Variation.Failure(e) ⇒
        log.error(message.getOrElse(""), e)
        log.trace(message.getOrElse(""))
      case _ ⇒ log.trace(message.getOrElse(""))
    }
    MDC.clear()
  }
}

private[tylog] object TypedLogging {
  val callTypeKey: String = "call_type"
  val variationKey: String = "variation"
  val traceIdKey: String = "trace_id"
}
