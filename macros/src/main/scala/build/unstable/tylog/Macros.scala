package build.unstable.tylog

import org.slf4j.Logger

import scala.reflect.macros.whitebox

private[tylog] object Macros {

  private def assert_(c: whitebox.Context)(template: c.Expr[String], arg: c.Expr[Any]*): Int = {
    import c.universe._

    /*
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

    val s_template: String = template.tree match {
      case Literal(Constant(s: String)) ⇒ s
      case tree ⇒ c.abort(c.enclosingPosition, s"log template must be a string literal")
    }

    val na = arg.length
    val np = "\\{\\}".r.findAllIn(s_template).length
    assert(na == np, s"$na arguments passed but there are $np placeholders")
    na
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
      val _log = log.splice
      if (_log.isErrorEnabled) _log.error(replace(template.splice, argsExpr.splice, nExpr.splice), error.splice)
    }
  }

  def warning(c: whitebox.Context)
             (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      val _log = log.splice
      if (_log.isWarnEnabled) _log.warn(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }

  def info(c: whitebox.Context)
          (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      val _log = log.splice
      if (_log.isInfoEnabled) _log.info(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }

  def debug(c: whitebox.Context)
           (log: c.Expr[Logger], template: c.Expr[String], arg: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val n = assert_(c)(template, arg: _*)
    val argsExpr = c.Expr[Seq[Any]] { q"Seq(..$arg)" }
    val nExpr = c.Expr[Int](Literal(Constant(n)))
    reify {
      val _log = log.splice
      if (_log.isDebugEnabled) _log.debug(replace(template.splice, argsExpr.splice, nExpr.splice))
    }
  }
}
