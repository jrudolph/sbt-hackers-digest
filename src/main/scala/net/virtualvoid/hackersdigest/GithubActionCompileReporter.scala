package net.virtualvoid.hackersdigest

import xsbti.Severity

class GithubActionCompileReporter(delegate: xsbti.Reporter, baseDir: File) extends xsbti.Reporter {
  def reset(): Unit                  = delegate.reset()
  def hasErrors: Boolean             = delegate.hasErrors
  def hasWarnings: Boolean           = delegate.hasWarnings
  def printSummary(): Unit           = delegate.printSummary()
  def problems: Array[xsbti.Problem] = delegate.problems()

  def log(problem: xsbti.Problem): Unit = {
    delegate.log(problem)
    import problem._

    if ((severity == Severity.Warn || severity == Severity.Error) && position.sourceFile.isPresent) {
      val file    = baseDir.toPath.relativize(position.sourceFile.get().toPath).toFile
      val message = problem.message.split("\n").head
      def e(key: String, value: java.util.Optional[Integer]): String =
        value.map[String](v => s",$key=$v").orElse("")

      val level = severity match {
        case Severity.Warn  => "warning"
        case Severity.Error => "error"
        case _              => throw new IllegalStateException
      }
      println(
        s"::$level file=${file}${e("line", position.line())}${e("col", position.startColumn())}${e("endColumn", position.endColumn())}}::$message"
      )
    }
  }

  /** Reports a comment. */
  def comment(pos: xsbti.Position, msg: String): Unit = delegate.comment(pos, msg)

  override def toString = "GithubActionCompileReporter"
}
