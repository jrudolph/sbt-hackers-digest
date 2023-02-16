package net.virtualvoid.hackersdigest

import sbt._
import Keys._
import sbt.hackersdigest.InternalAccess

object HackersDigestPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger  = allRequirements

  override def projectSettings =
    if (sys.env.contains("GITHUB_ENV"))
      Seq(
        reporterFor(Compile),
        reporterFor(Test),
        testListeners ++= Seq(
          new GithubAnnotationTestsListener((ThisBuild / baseDirectory).value, (Test / sourceDirectories).value)
        )
      )
    else
      Seq.empty

  private def reporterFor(config: Configuration): Setting[_] =
    config / compile / InternalAccess.compilerReporter :=
      new GithubActionCompileReporter(
        (config / compile / InternalAccess.compilerReporter).value,
        (ThisBuild / baseDirectory).value
      )
}
