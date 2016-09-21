package build.unstable.tylog

import build.unstable.tylog.Fixture.MockLogger
import org.scalatest.{Matchers, WordSpec}
import org.slf4j.MDC
import org.slf4j.event.Level

class TypedLoggingSpec extends WordSpec with Matchers with TypedLogging {

  override type CallType = String
  override type TraceID = String

  "TypedLogging" should {

    "replace placeholders with arguments" in {
      {
        val res = Macros.replace("A: {}", Seq("a"), 1)
        assert(res == "A: a")
      }
    }

    "check missing args and placeholders at compile time" in {

      """
        | log.error(new Exception("BOOM"), "A: {}")""".stripMargin shouldNot compile

      """
        | log.error(new Exception("BOOM"), "A", "a")""".stripMargin shouldNot compile

      """
        | log.error(new Exception("BOOM"), "A: {}", "a")""".stripMargin should compile

      """
        | log.debug("A: {}")""".stripMargin shouldNot compile

      """
        | log.debug("A", "a")""".stripMargin shouldNot compile

      """
        | log.debug("A: {}", "a")""".stripMargin should compile

      """
        | log.info("A: {}")""".stripMargin shouldNot compile

      """
        | log.info("A", "a")""".stripMargin shouldNot compile

      """
        | log.info("A: {}", "a")""".stripMargin should compile

      """
        | log.warning("A: {}")""".stripMargin shouldNot compile

      """
        | log.warning("A", "a")""".stripMargin shouldNot compile

      """
        | log.warning("A: {}", "a")""".stripMargin should compile

      """
        | log.trace("A: {}")""".stripMargin shouldNot compile

      """
        | log.trace("A", "a")""".stripMargin shouldNot compile

      """
        | log.trace("A: {}", "a")""".stripMargin should compile

      """
        | log.tylog(org.slf4j.event.Level.TRACE, "A", "a", Variation.Attempt, "{}")""".stripMargin shouldNot compile

      """
        | log.tylog(org.slf4j.event.Level.TRACE, "A", "a", Variation.Attempt, "", "a")""".stripMargin shouldNot compile

      """
        | log.tylog(org.slf4j.event.Level.TRACE, "A", "a", Variation.Attempt, "{}", "a")""".stripMargin should compile
    }

    "not allow non-literal strings if args are passed" in {
      """
        | val msg = "{}"
        | log.info(msg, "a")""".stripMargin shouldNot compile
    }

    "allow non-literal strings if no args are passed" in {
      val msg = "A"
      log.info(msg)
    }

    /* TODO

    "log trace messages with set MDC context" in {
      {
        log.tylog(Level.TRACE, "1", "a", Variation.Attempt, "{}", "a")
        MDC.get(callTypeKey) shouldBe "a"
        MDC.get(traceIdKey) shouldBe "1"
        // should clean variation after logging
        MDC.get(variationKey) shouldBe null

        log.tylog(Level.TRACE, "1", "a", Variation.Success, "{}", "a")
        // should clean ALL context after success or failure
        MDC.get(callTypeKey) shouldBe null
        MDC.get(traceIdKey) shouldBe null
      }
      {
        log.tylog(Level.TRACE, "1", "a", Variation.Attempt, "{}", "a")
        MDC.get(callTypeKey) shouldBe "a"
        MDC.get(traceIdKey) shouldBe "1"
        // should clean variation after logging
        MDC.get(variationKey) shouldBe null

        log.tylog(Level.TRACE, "1", "a", Variation.Failure(new Exception("BOOM")), "{}", "a")
        // should clean ALL context after success or failure
        MDC.get(callTypeKey) shouldBe null
        MDC.get(traceIdKey) shouldBe null
      }
    }

    "not not interpolate string if level is not enabled" in {
      val log = new MockLogger(100)
      info(log, "{}", "a")
      assert(log.interceptedMessage.isEmpty)
    }*/

    "not allow other levels other than INFO/DEBUG/TRACE with tylog method" in {
      """
        | log.tylog(log, org.slf4j.event.Level.WARNING, "A", "a", Variation.Attempt, "msg")""".stripMargin shouldNot compile

      """
        | log.tylog(log, org.slf4j.event.Level.ERROR, "A", "a", Variation.Attempt, "msg")""".stripMargin shouldNot compile
    }
  }
}
