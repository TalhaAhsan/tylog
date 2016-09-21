package build.unstable.tylog

import org.slf4j.{Logger, MDC, Marker}

object Fixture {

  class MockLogger(level: Int) extends Logger {

    def isErrorEnabled: Boolean = level < 4

    def isInfoEnabled: Boolean = level < 3

    def isWarnEnabled: Boolean = level < 2

    def isDebugEnabled: Boolean = level < 1

    def isTraceEnabled: Boolean = true

    def getMDC = {
      Map(
        callTypeKey → MDC.get(callTypeKey),
        variationKey → MDC.get(variationKey),
        traceIdKey → MDC.get(traceIdKey)
      )
    }

    def intercept(msg: String) {
      interceptedMdc = getMDC
      interceptedMessage = Some(msg)
    }

    var interceptedMdc: Map[String, String] = Map.empty
    var interceptedMessage: Option[String] = None

    override def trace(msg: String): Unit = intercept(msg)

    override def info(msg: String): Unit = intercept(msg)

    override def debug(msg: String): Unit = intercept(msg)

    override def warn(msg: String): Unit = intercept(msg)

    override def error(msg: String, t: Throwable): Unit = intercept(msg)

    //not used
    override def isWarnEnabled(marker: Marker): Boolean = ???

    override def isInfoEnabled(marker: Marker): Boolean = ???

    override def isDebugEnabled(marker: Marker): Boolean = ???

    override def isTraceEnabled(marker: Marker): Boolean = ???

    override def warn(format: String, arg: scala.Any): Unit = ???

    override def warn(format: String, arguments: AnyRef*): Unit = ???

    override def warn(format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def warn(msg: String, t: Throwable): Unit = ???

    override def warn(marker: Marker, msg: String): Unit = ???

    override def warn(marker: Marker, format: String, arg: scala.Any): Unit = ???

    override def warn(marker: Marker, format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def warn(marker: Marker, format: String, arguments: AnyRef*): Unit = ???

    override def warn(marker: Marker, msg: String, t: Throwable): Unit = ???

    override def isErrorEnabled(marker: Marker): Boolean = ???

    override def getName: String = ???

    override def error(msg: String): Unit = ???

    override def error(format: String, arg: scala.Any): Unit = ???

    override def error(format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def error(format: String, arguments: AnyRef*): Unit = ???

    override def error(marker: Marker, msg: String): Unit = ???

    override def error(marker: Marker, format: String, arg: scala.Any): Unit = ???

    override def error(marker: Marker, format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def error(marker: Marker, format: String, arguments: AnyRef*): Unit = ???

    override def error(marker: Marker, msg: String, t: Throwable): Unit = ???

    override def debug(format: String, arg: scala.Any): Unit = ???

    override def debug(format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def debug(format: String, arguments: AnyRef*): Unit = ???

    override def debug(msg: String, t: Throwable): Unit = ???

    override def debug(marker: Marker, msg: String): Unit = ???

    override def debug(marker: Marker, format: String, arg: scala.Any): Unit = ???

    override def debug(marker: Marker, format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def debug(marker: Marker, format: String, arguments: AnyRef*): Unit = ???

    override def debug(marker: Marker, msg: String, t: Throwable): Unit = ???

    override def trace(format: String, arg: scala.Any): Unit = ???

    override def trace(format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def trace(format: String, arguments: AnyRef*): Unit = ???

    override def trace(msg: String, t: Throwable): Unit = ???

    override def trace(marker: Marker, msg: String): Unit = ???

    override def trace(marker: Marker, format: String, arg: scala.Any): Unit = ???

    override def trace(marker: Marker, format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def trace(marker: Marker, format: String, argArray: AnyRef*): Unit = ???

    override def trace(marker: Marker, msg: String, t: Throwable): Unit = ???

    override def info(format: String, arg: scala.Any): Unit = ???

    override def info(format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def info(format: String, arguments: AnyRef*): Unit = ???

    override def info(msg: String, t: Throwable): Unit = ???

    override def info(marker: Marker, msg: String): Unit = ???

    override def info(marker: Marker, format: String, arg: scala.Any): Unit = ???

    override def info(marker: Marker, format: String, arg1: scala.Any, arg2: scala.Any): Unit = ???

    override def info(marker: Marker, format: String, arguments: AnyRef*): Unit = ???

    override def info(marker: Marker, msg: String, t: Throwable): Unit = ???
  }

}
