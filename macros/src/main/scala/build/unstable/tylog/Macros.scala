package build.unstable.tylog

import org.slf4j.event.Level
import org.slf4j.{Logger, MDC}

import scala.reflect.macros.whitebox

private[tylog] object Macros {

  private def assert_(c: whitebox.Context)(template: c.Expr[String], arg: c.Expr[Any]*): Int = {
    import c.universe._

    /* TODO traverse compilation unit's AST to provide non-literal compile-time checks
    case class FindLiteral(name: c.universe.Name, scopeTree: c.universe.Tree) extends Traverser {
      var template: Option[String] = None
      var traversed: String = ""

      override def traverse(tree: c.universe.Tree): Unit = tree match {
        case t if t equalsStructure scopeTree ⇒ throw new Exception("voila!")
        case t ⇒
          traversed += showRaw(tree) + ":::::::::"
          super.traverse(t)
      }
    }

    def findRef(i: RefTree) = {
      val qualifier = i.qualifier
      val tree = c.enclosingUnit.body
      val a: FindLiteral = new FindLiteral(i.name, tree)
      a.traverse(tree)
      a.template.getOrElse {
        c.abort(c.enclosingPosition, s"template must be a string literal in macro compilation unit: ${a.traversed}")
      }
    }*/

    template.tree match {
      case Literal(Constant(s: String)) ⇒
        val na = arg.length
        val np = "\\{\\}".r.findAllIn(s).length
        assert(na == np, s"$na arguments passed but there are $np placeholders")
        na
      case tree if arg.isEmpty ⇒ 0
      case tree ⇒ c.abort(c.enclosingPosition, "log template must be a string literal if arguments are to be injected")
    }
  }

  def replace(template: String, arg: Seq[Any], n: Int): String = {
    val sb = new java.lang.StringBuilder(64)
    var p = 0
    var startIndex = 0
    while (p < n) {
      val index = template.indexOf("{}", startIndex)
      sb.append(template.substring(startIndex, index))
        .append(arg(p))
      startIndex = index + 2
      p += 1
    }
    sb.append(template.substring(startIndex, template.length)).toString
  }

  def error(c: whitebox.Context)
           (log: c.Expr[Logger], error: c.Expr[Throwable], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      if (log.splice.isErrorEnabled) log.splice.error(replace(template.splice, argsExpr.splice, nExpr.splice), error.splice)
    }
  }

  def warning(c: whitebox.Context)
             (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      if (log.splice.isWarnEnabled) log.splice.warn(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }

  def info(c: whitebox.Context)
          (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      if (log.splice.isInfoEnabled) log.splice.info(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }

  def debug(c: whitebox.Context)
           (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      if (log.splice.isDebugEnabled) log.splice.debug(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }

  def trace(c: whitebox.Context)
           (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      if (log.splice.isDebugEnabled) log.splice.trace(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }

  @deprecated("", "0.2.5")
  def _trace[TraceID, CallType](c: whitebox.Context)
                               (log: c.Expr[Logger], traceId: c.Expr[TraceID],
                                callType: c.Expr[CallType], variation: c.Expr[Variation],
                                template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))

    reify {
      if (log.splice.isTraceEnabled) {
        MDC.put(traceIdKey, traceId.splice.toString)
        MDC.put(callTypeKey, callType.splice.toString)
        MDC.put(variationKey, variation.splice.toString)

        val message = replace(template.splice, argsExpr.splice, nExpr.splice)
        log.splice.trace(message)

        //if failure, log error level as well
        if (variation.splice.isFailure) {
          val e = variation.splice.asInstanceOf[Variation.Failure].e
          log.splice.error(message, e)
        }
        MDC.clear()
      }
    }
  }

  def logMethod(c: whitebox.Context)(logger: c.Expr[Logger], method: String): c.Expr[String ⇒ Unit] = {
    import c.universe._
    val term = TermName(method)
    c.Expr[String ⇒ Unit](q"(s: String) => $logger.$term(s)")
  }

  def errorMethod(c: whitebox.Context)(logger: c.Expr[Logger], method: String): c.Expr[(String, Throwable) ⇒ Unit] = {
    import c.universe._
    val term = TermName(method)
    c.Expr[(String, Throwable) ⇒ Unit](q"(s: String, e: Throwable) => $logger.$term(s, e)")
  }

  def selectIsEnabled(c: whitebox.Context)(logger: c.Expr[Logger], method: String): c.Expr[Boolean] = {
    import c.universe._
    c.Expr[Boolean](Select(logger.tree, TermName(method)))
  }

  def tylog[T, C](c: whitebox.Context)(logger: c.Expr[Logger], level: c.Expr[Level], traceId: c.Expr[T],
                                       callType: c.Expr[C], variation: c.Expr[Variation],
                                       template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] {
      q"Seq(..$arg)"
    }
    val nExpr = c.Expr[Int](Literal(Constant(n)))

    val (logFn, errorFn, isEnabled) = level match {

      case Expr(Literal(Constant(s: TermSymbol))) if s.fullName == "org.slf4j.event.Level.TRACE" ⇒
        (logMethod(c)(logger, "trace"),
          errorMethod(c)(logger, "warn"),
          selectIsEnabled(c)(logger, "isTraceEnabled"))

      case Expr(Literal(Constant(s: TermSymbol))) if s.fullName == "org.slf4j.event.Level.DEBUG" ⇒
        (logMethod(c)(logger, "debug"),
          errorMethod(c)(logger, "warn"),
          selectIsEnabled(c)(logger, "isDebugEnabled"))

      case Expr(Literal(Constant(s: TermSymbol))) if s.fullName == "org.slf4j.event.Level.INFO" ⇒
        (logMethod(c)(logger, "info"),
          errorMethod(c)(logger, "error"),
          selectIsEnabled(c)(logger, "isInfoEnabled"))

      case Expr(Literal(Constant(s: TermSymbol))) ⇒
        c.abort(c.enclosingPosition, s"${s.fullName} is not allowed for tylog method")

      case s ⇒ c.abort(c.enclosingPosition, s"unexpected expression $s")
    }

    reify {
      if (isEnabled.splice) {
        val $variation: Variation = variation.splice
        val $callType = callType.splice
        val $traceId = traceId.splice
        val $message = replace(template.splice, argsExpr.splice, nExpr.splice)

        MDC.put(traceIdKey, $traceId.toString)
        MDC.put(callTypeKey, $callType.toString)
        MDC.put(variationKey, $variation.toString)

        $variation match {
          case Variation.Attempt ⇒
            logFn.splice($message)
            MDC.remove(variationKey)

          case Variation.Success ⇒
            logFn.splice($message)
            MDC.clear()

          case f@Variation.Failure(e) ⇒
            errorFn.splice($message, f.e)
            MDC.clear()
        }
      }
    }
  }
}
