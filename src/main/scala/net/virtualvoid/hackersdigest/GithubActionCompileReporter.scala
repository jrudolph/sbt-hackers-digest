package net.virtualvoid.hackersdigest

import java.io.File
import xsbti.Severity

class GithubActionCompileReporter(annotator: Annotator, delegate: xsbti.Reporter, baseDir: File)
    extends xsbti.Reporter {
  def reset(): Unit                  = delegate.reset()
  def hasErrors: Boolean             = delegate.hasErrors
  def hasWarnings: Boolean           = delegate.hasWarnings
  def printSummary(): Unit           = delegate.printSummary()
  def problems: Array[xsbti.Problem] = delegate.problems()

  def log(problem: xsbti.Problem): Unit = {
    delegate.log(problem)
    import problem._

    if (
      (severity == Severity.Warn || severity == Severity.Error) && position.sourceFile.isPresent && position.sourceFile
        .get()
        .toPath
        .getRoot != null
    ) {
      def e(key: String, value: java.util.Optional[Integer]): String =
        value.map[String](v => s",$key=$v").orElse("")

      val level = severity match {
        case Severity.Warn  => AnnotationSeverity.Warning
        case Severity.Error => AnnotationSeverity.Error
        case _              => throw new IllegalStateException
      }
      // TODO: resurrect column info: ${e("col", position.startColumn())}${e("endColumn", position.endColumn())}
      val message = problem.message.split("\n").head
      val file    = baseDir.toPath.relativize(position.sourceFile.get().toPath).toFile
      val line    = if (position.line.isPresent) Some(position.line.get().intValue) else None
      annotator.createAnnotation(AnnotationOrigin.Compilation, level, message, Some(file.toString), line)
    }
  }

  /** Reports a comment. */
  def comment(pos: xsbti.Position, msg: String): Unit = delegate.comment(pos, msg)

  override def toString = "GithubActionCompileReporter"
}
