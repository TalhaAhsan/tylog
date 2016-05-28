package build.unstable.tylog

import org.scalatest.{Matchers, WordSpec}

class VariationSpec extends WordSpec with Matchers with TypedLogging {

  "Variation" should {
    "implement isFailure method" in {
      assert(!Variation.Attempt.isFailure)
      assert(!Variation.Success.isFailure)
      assert(Variation.Failure(new Exception("")).isFailure)
    }
  }
}
