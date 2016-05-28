package build.unstable.tylog

sealed trait Variation {
  def isFailure: Boolean = this.getClass == classOf[Variation.Failure]
}

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

