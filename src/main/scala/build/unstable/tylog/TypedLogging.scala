package build.unstable.tylog

import org.slf4j.Logger
import org.slf4j.event.Level

import scala.language.experimental.macros

trait TypedLogging {

  type CallType
  type TraceID

  protected def debug(log: Logger, template: String, arg: Any*): Unit = macro Macros.debug

  protected def info(log: Logger, template: String, arg: Any*): Unit = macro Macros.info

  protected def error(log: Logger, error: Throwable, template: String, arg: Any*): Unit = macro Macros.error

  protected def warning(log: Logger, template: String, arg: Any*): Unit = macro Macros.warning

  protected def trace(log: Logger, template: String, arg: Any*): Unit = macro Macros.trace

  @deprecated(message = "use `tylog` method instead. Placeholders vs arg won't be checked at compile time", since = "0.2.5")
  protected def trace(log: Logger, traceId: TraceID,
                      callType: CallType, variation: Variation,
                      template: String, arg: Any*): Unit = macro Macros._trace[TraceID, CallType]

  protected def tylog(logger: Logger, level: Level, traceId: TraceID,
                      callType: CallType, variation: Variation,
                      template: String, arg: Any*): Unit = macro Macros.tylog[TraceID, CallType]
}
