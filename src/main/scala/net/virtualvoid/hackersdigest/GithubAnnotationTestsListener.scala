package net.virtualvoid.hackersdigest

import sbt._
import sbt.testing.Status

import java.nio.file.{Files, Path}

class GithubAnnotationTestsListener(baseDir: File, sourceDirs: Seq[File]) extends TestsListener {
  override def doInit(): Unit = ()

  override def doComplete(finalResult: TestResult): Unit = ()

  override def startGroup(name: String): Unit = println(s"::group::$name")

  override def testEvent(event: TestEvent): Unit = {
    if (event.result.contains(TestResult.Failed)) {
      val failed = event.detail.filter(_.status() == Status.Failure)
      failed.foreach { t =>
        if (t.throwable().isDefined) {
          val throwable = t.throwable().get()
          val firstLine = throwable.getMessage.split("\n").head
          TestStackTraceInfoExtractor.mostRelevantTraceElement(throwable.getStackTrace) match {
            case Some(ele) =>
              println(
                s"::error file=${findFile(ele.getFileName).getOrElse(ele.getFileName)},line=${ele.getLineNumber}::$firstLine"
              )
            case None =>
              println(
                s"::error::$firstLine"
              )
          }
        }
      }
    }
  }

  private def findFile(fileName: String): Option[File] = {
    def findIn(base: File): Seq[File] =
      Files
        .find(base.toPath, 20, (p, _) => p.toFile.getName == fileName)
        .toArray
        .toSeq
        .map(p => baseDir.toPath.relativize(p.asInstanceOf[Path]).toFile)

    sourceDirs.filter(_.exists()).flatMap(findIn(_)).headOption
  }

  override def endGroup(name: String, t: Throwable): Unit = println("::endgroup::")

  override def endGroup(name: String, result: TestResult): Unit = println("::endgroup::")

  def println(str: String): Unit = {
    scala.Console.println(str)
    scala.Console.flush()
  }
}
