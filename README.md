# Tylog [![Build Status](https://travis-ci.org/ernestrc/tylog.svg?branch=master)](https://travis-ci.org/ernestrc/tylog)

# The problem
Server-side inhomogeneous logging can be frustrating to parse and analyze.

# The solution
Tylog provides a thin interface that abstracts over SLF4J's facade to provide compile time checks for your debug/info/error/warning messages and a type-safe [measure API](src/main/scala/build/unstable/tylog/TypedLogging.scala#L44).

# Usage
```scala

import org.slf4j.event.Level
import build.unstable.tylog._

// Mix `TypedLogging` trait with your classes
object Example extends App with TypedLogging {

  // define TraceID and CallType
  type TraceID = String

  sealed trait CallType
  case object A extends CallType
  case object B extends CallType

  val traceId = "1"

  // this adds callType/variation/traceID to MDC
  log.tylog(Level.TRACE, traceId, A, Variation.Attempt, "let's see..\n...")

  // normal log statements between attempt and resolution will have MDC set
  log.debug("a message with context")

  // won't compile, ERROR is not a valid Level for tylog method
  // log.tylog(Level.ERROR, traceId, A, Variation.Success, "yay!")

  // logging Success/Failure will clear MDC
  log.tylog(Level.INFO, traceId, A, Variation.Failure(new Exception("BOOM")), "yay!")

  // placeholders and arguments are checked at compile time
  log.debug("this compiles normally {}", "msg")

  // log.info("this does not compile because there is a missing arg {}")

  // log.warning("this does not compile because there is a missing placeholder", "a")

}
```

# Contribute
If you would like to contribute to the project, please fork the project, include your changes and submit a pull request back to the main repository.

# TODO
- Compile time checks for message templates defined in the project's compilation unit

# License
MIT License 
