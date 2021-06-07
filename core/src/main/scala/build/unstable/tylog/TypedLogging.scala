package build.unstable.tylog

import org.slf4j.event.Level

import scala.language.experimental.macros

trait TypedLogging {

  type CallType
  type TraceID

  object log {
    def debug(template: String, arg: Any*): Unit = macro Macros.debug

    def info(template: String, arg: Any*): Unit = macro Macros.info

    def error(error: Throwable, template: String, arg: Any*): Unit = macro Macros.error

    def warning(template: String, arg: Any*): Unit = macro Macros.warning

    def trace(template: String, arg: Any*): Unit = macro Macros.trace

    /**
      * Log a measurement with the following diagnostic context (MDC):
      * {
      *   "call_type": <callType>,
      *   "variation": <variation>,
      *   "trace_id": <traceId>
      * }
      *
      * Calls to MDC.put prior to calling this method will be respected and its values logged accordingly.
      * After measurement is resolved by logging a Variation.Failure or a Variation.Success,
      * ALL the context will be cleared.
      *
      * Failures will be logged at WARN level for TRACE and DEBUG, or at ERROR level for INFO.
      *
      * @param level slf4j log level of this measurement. Only INFO, DEBUG and TRACE are allowed
      * @param traceId measurements group id
      * @param callType measurement name
      * @param variation measurement status
      * @param template message to be logged
      * @param arg template arguments if any
      */
    def tylog(level: Level, traceId: TraceID,
              callType: CallType, variation: Variation,
              template: String, arg: Any*): Unit = macro Macros.tylog[TraceID, CallType]
  }
}
