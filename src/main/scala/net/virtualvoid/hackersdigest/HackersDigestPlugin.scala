package net.virtualvoid.hackersdigest

import sbt._
import Keys._
import sbt.hackersdigest.InternalAccess

sealed trait AnnotationOrigin
object AnnotationOrigin {
  case object Compilation extends AnnotationOrigin
  case object Testing extends AnnotationOrigin
}
sealed trait AnnotationSeverity
object AnnotationSeverity {
  case object Warning extends AnnotationSeverity
  case object Error extends AnnotationSeverity
}
trait AnnotationFilter {
  /** Filter for annotations */
  def filter(origin: AnnotationOrigin, severity: AnnotationSeverity, title: String, fileName: Option[String], lineNumber: Option[Int]): Boolean
}

trait Annotator {
  def createAnnotation(origin: AnnotationOrigin, severity: AnnotationSeverity, title: String, fileName: Option[String] = None, lineNumber: Option[Int] = None): Unit
  def createAnnotation(origin: AnnotationOrigin, severity: AnnotationSeverity, title: String, fileName: String, lineNumber: Int): Unit =
    createAnnotation(origin, severity, title, Some(fileName), Some(lineNumber))
  def error(origin: AnnotationOrigin,  title: String, fileName: String, lineNumber: Int): Unit =
    createAnnotation(origin, AnnotationSeverity.Error, title, fileName, lineNumber)
  def error(origin: AnnotationOrigin,  title: String): Unit =
    createAnnotation(origin, AnnotationSeverity.Error, title)
  def warn(origin: AnnotationOrigin,  title: String, fileName: String, lineNumber: Int): Unit =
    createAnnotation(origin, AnnotationSeverity.Warning, title, fileName, lineNumber)
  def warn(origin: AnnotationOrigin,  title: String): Unit =
    createAnnotation(origin, AnnotationSeverity.Warning, title)
}

object HackersDigestPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger  = allRequirements

  object autoImport {
    val hackersDigestAnnotateTestFailures = settingKey[Boolean]("Whether test failures should be annotated")
    val hackersDigestAnnotateCompileWarnings = settingKey[Boolean]("Whether compilation warnings should be annotated")
    val hackersDigestAnnotateCompileErrors = settingKey[Boolean]("Whether compilation errors should be annotated")
    val hackersDigestAnnotationFilter =
      settingKey[AnnotationFilter](
        "Can be overridden to do fine-grained filtering for annotations. Will override whatever the boolean flag keys have set"
      )

    private[HackersDigestPlugin] val hackersDigestAnnotator = settingKey[Annotator]("")
  }
  import autoImport._

  override def projectSettings =
    if (sys.env.contains("GITHUB_ENV"))
      Seq(
        reporterFor(Compile),
        reporterFor(Test),
        testListeners ++= Seq(
          new GithubAnnotationTestsListener(hackersDigestAnnotator.value, (ThisBuild / baseDirectory).value, (Test / sourceDirectories).value)
        )
      )
    else
      Seq.empty


  override def globalSettings = Seq(
    hackersDigestAnnotateTestFailures := true,
    hackersDigestAnnotateCompileWarnings := true,
    hackersDigestAnnotateCompileErrors := true,
    hackersDigestAnnotationFilter := {
      val annotateTestFailures = hackersDigestAnnotateTestFailures.value
      val compileWarnings = hackersDigestAnnotateCompileWarnings.value
      val compileErrors = hackersDigestAnnotateCompileErrors.value
      new AnnotationFilter {
        override def filter(origin: AnnotationOrigin, severity: AnnotationSeverity, title: String, fileName: Option[String], lineNumber: Option[Int]): Boolean = {
          origin match {
            case AnnotationOrigin.Testing => annotateTestFailures
            case AnnotationOrigin.Compilation =>
              severity match {
                case AnnotationSeverity.Warning => compileWarnings
                case AnnotationSeverity.Error => compileErrors
              }
          }
        }
      }
    },
    hackersDigestAnnotator := annotator(hackersDigestAnnotationFilter.value),
  )

  private def reporterFor(config: Configuration): Setting[_] =
    config / compile / InternalAccess.compilerReporter :=
      new GithubActionCompileReporter(
        hackersDigestAnnotator.value,
        (config / compile / InternalAccess.compilerReporter).value,
        (ThisBuild / baseDirectory).value
      )

  private def annotator(filter: AnnotationFilter): Annotator = new Annotator {
    override def createAnnotation(origin: AnnotationOrigin, severity: AnnotationSeverity, title: String, fileName: Option[String], lineNumber: Option[Int]): Unit =
      if (filter.filter(origin, severity, title, fileName, lineNumber)) {
        val severityTag = severity match {
          case AnnotationSeverity.Warning => "warning"
          case AnnotationSeverity.Error => "error"
        }
        def e(key: String, value: Option[Any]): Option[String] =
          value.map[String](v => s"$key=$v")

        val entries =
          Seq(
            "file" -> fileName,
            "line" -> lineNumber
          ).flatMap(x => e(x._1, x._2))
            .mkString(",")

        println(s"::$severityTag $entries::$title")
      }
  }
}
