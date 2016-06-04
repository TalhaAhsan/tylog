package build.unstable.tylog

import build.unstable.tylog.Fixture.MockLogger
import org.scalatest.{Matchers, WordSpec}

class TypedLoggingSpec extends WordSpec with Matchers with TypedLogging {

  override type CallType = String
  override type TraceID = String

  "TypedLogging" should {

    "inject args to template" in {
      val log = new MockLogger(0)
      error(log, new Exception("BOOM"), "A: {}", "a")
      assert(log.interceptedMessage == "A: a")
    }

    "check missing args and placeholders at compile time" in {

      """
        | val log = new MockLogger(0)
        |error(log, new Exception("BOOM"), "A: {}")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |error(log, new Exception("BOOM"), "A", "a")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |debug(log, "A: {}", "a")""".stripMargin should compile

      """
        | val log = new MockLogger(0)
        |debug(log, "A: {}")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |debug(log, "A", "a")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |info(log, "A: {}")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |info(log, "A", "a")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |warning(log, "A: {}")""".stripMargin shouldNot compile

      """
        | val log = new MockLogger(0)
        |warning(log, "A", "a")""".stripMargin shouldNot compile
    }

    "log trace messages with the right MDC context" in {
      val log = new MockLogger(0)
      trace(log, "1", "a", Variation.Attempt, None)
      log.interceptedMdc should contain theSameElementsAs Map(
        TypedLogging.callTypeKey → "a",
        TypedLogging.traceIdKey → "1",
        TypedLogging.variationKey → "Attempt"
      )
    }

    "no string interpolation occurs if level is not enabled" in {
      val log = new MockLogger(100)
      trace(log, "1", "a", Variation.Attempt, None)
      assert(log.interceptedMessage == "")
    }
  }
}
