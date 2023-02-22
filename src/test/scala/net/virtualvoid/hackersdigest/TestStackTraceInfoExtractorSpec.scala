package net.virtualvoid.hackersdigest

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class TestStackTraceInfoExtractorSpec extends AnyFreeSpec with Matchers {
  "TestStackTraceInfoExtractor must find the most relevant trace line" - {
    "for ScalaTest" - {
      "user code directly on top of OutcomeOf" in {
        val trace = stackTrace(
          """at org.apache.pekko.actor.testkit.typed.internal.LoggingTestKitImpl.expect(LoggingTestKitImpl.scala:113)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.f$proxy3$1(MailboxSelectorSpec.scala:93)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.$init$$$anonfun$1$$anonfun$3(MailboxSelectorSpec.scala:75)
            |at org.scalatest.OutcomeOf.outcomeOf(OutcomeOf.scala:85)
            |at org.scalatest.OutcomeOf.outcomeOf$(OutcomeOf.scala:31)
            |at org.scalatest.OutcomeOf$.outcomeOf(OutcomeOf.scala:104)
            |at org.scalatest.Transformer.apply(Transformer.scala:22)
            |at org.scalatest.Transformer.apply(Transformer.scala:21)
            |at org.scalatest.wordspec.AnyWordSpecLike$$anon$3.apply(AnyWordSpecLike.scala:1105)
            |at org.apache.pekko.actor.testkit.typed.scaladsl.LogCapturing.withFixture(LogCapturing.scala:79)
            |at org.apache.pekko.actor.testkit.typed.scaladsl.LogCapturing.withFixture$(LogCapturing.scala:42)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.withFixture(MailboxSelectorSpec.scala:34)
            |at org.scalatest.wordspec.AnyWordSpecLike.invokeWithFixture$1(AnyWordSpecLike.scala:1111)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTest$$anonfun$1(AnyWordSpecLike.scala:1115)
            |at org.scalatest.SuperEngine.runTestImpl(Engine.scala:306)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTest(AnyWordSpecLike.scala:1115)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTest$(AnyWordSpecLike.scala:44)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.runTest(MailboxSelectorSpec.scala:34)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTests$$anonfun$1(AnyWordSpecLike.scala:1174)
            |at org.scalatest.SuperEngine.traverseSubNodes$2$$anonfun$1(Engine.scala:413)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
            |at scala.collection.immutable.List.foreach(List.scala:333)
            |at org.scalatest.SuperEngine.traverseSubNodes$1(Engine.scala:429)
            |at org.scalatest.SuperEngine.runTestsInBranch(Engine.scala:390)
            |at org.scalatest.SuperEngine.traverseSubNodes$2$$anonfun$1(Engine.scala:427)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)""".stripMargin)

        val relevant = TestStackTraceInfoExtractor.mostRelevantTraceElement(trace).get
        relevant.getFileName mustEqual "MailboxSelectorSpec.scala"
        relevant.getLineNumber mustBe 75
      }
      "user code on Transformer on top of OutcomeOf" in {
        val trace = stackTrace(
          """at org.apache.pekko.actor.testkit.typed.internal.LoggingTestKitImpl.expect(LoggingTestKitImpl.scala:113)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.f$proxy3$1(MailboxSelectorSpec.scala:93)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.$init$$$anonfun$1$$anonfun$3(MailboxSelectorSpec.scala:75)
            |at org.scalatest.Transformer.apply$$anonfun$1(Transformer.scala:22)
            |at org.scalatest.OutcomeOf.outcomeOf(OutcomeOf.scala:85)
            |at org.scalatest.OutcomeOf.outcomeOf$(OutcomeOf.scala:31)
            |at org.scalatest.OutcomeOf$.outcomeOf(OutcomeOf.scala:104)
            |at org.scalatest.Transformer.apply(Transformer.scala:22)
            |at org.scalatest.Transformer.apply(Transformer.scala:21)
            |at org.scalatest.wordspec.AnyWordSpecLike$$anon$3.apply(AnyWordSpecLike.scala:1105)
            |at org.apache.pekko.actor.testkit.typed.scaladsl.LogCapturing.withFixture(LogCapturing.scala:79)
            |at org.apache.pekko.actor.testkit.typed.scaladsl.LogCapturing.withFixture$(LogCapturing.scala:42)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.withFixture(MailboxSelectorSpec.scala:34)
            |at org.scalatest.wordspec.AnyWordSpecLike.invokeWithFixture$1(AnyWordSpecLike.scala:1111)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTest$$anonfun$1(AnyWordSpecLike.scala:1115)
            |at org.scalatest.SuperEngine.runTestImpl(Engine.scala:306)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTest(AnyWordSpecLike.scala:1115)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTest$(AnyWordSpecLike.scala:44)
            |at org.apache.pekko.actor.typed.MailboxSelectorSpec.runTest(MailboxSelectorSpec.scala:34)
            |at org.scalatest.wordspec.AnyWordSpecLike.runTests$$anonfun$1(AnyWordSpecLike.scala:1174)
            |at org.scalatest.SuperEngine.traverseSubNodes$2$$anonfun$1(Engine.scala:413)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
            |at scala.collection.immutable.List.foreach(List.scala:333)
            |at org.scalatest.SuperEngine.traverseSubNodes$1(Engine.scala:429)
            |at org.scalatest.SuperEngine.runTestsInBranch(Engine.scala:390)
            |at org.scalatest.SuperEngine.traverseSubNodes$2$$anonfun$1(Engine.scala:427)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
            |at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)""".stripMargin)

        val relevant = TestStackTraceInfoExtractor.mostRelevantTraceElement(trace).get
        relevant.getFileName mustEqual "MailboxSelectorSpec.scala"
        relevant.getLineNumber mustBe 75
      }
      "user code on top of anonymous function (nested tests)" in {
        val trace = stackTrace(
          """at scala.Predef$.assert(Predef.scala:279)
            |at org.apache.pekko.testkit.TestKitBase.$anonfun$receiveN_internal$1(TestKit.scala:852)
            |at org.apache.pekko.testkit.TestKitBase.$anonfun$receiveN_internal$1$adapted(TestKit.scala:849)
            |at scala.collection.immutable.Range.map(Range.scala:59)
            |at org.apache.pekko.testkit.TestKitBase.receiveN_internal(TestKit.scala:849)
            |at org.apache.pekko.testkit.TestKitBase.receiveN(TestKit.scala:840)
            |at org.apache.pekko.testkit.TestKitBase.receiveN$(TestKit.scala:840)
            |at org.apache.pekko.testkit.TestKit.receiveN(TestKit.scala:984)
            |at org.apache.pekko.remote.artery.RemoteFailureSpec.$anonfun$new$7(RemoteFailureSpec.scala:60)
            |at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
            |at org.apache.pekko.testkit.TestKitBase.within(TestKit.scala:429)
            |at org.apache.pekko.testkit.TestKitBase.within$(TestKit.scala:416)
            |at org.apache.pekko.testkit.TestKit.within(TestKit.scala:984)
            |at org.apache.pekko.testkit.TestKitBase.within(TestKit.scala:444)
            |at org.apache.pekko.testkit.TestKitBase.within$(TestKit.scala:444)
            |at org.apache.pekko.testkit.TestKit.within(TestKit.scala:984)
            |at org.apache.pekko.remote.artery.RemoteFailureSpec.$anonfun$new$2(RemoteFailureSpec.scala:60)
            |at scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
            |at org.scalatest.OutcomeOf.outcomeOf(OutcomeOf.scala:85)
            |at org.scalatest.OutcomeOf.outcomeOf$(OutcomeOf.scala:83)
            |at org.scalatest.OutcomeOf$.outcomeOf(OutcomeOf.scala:104)
            |at org.scalatest.Transformer.apply(Transformer.scala:22)
            |at org.scalatest.Transformer.apply(Transformer.scala:20)
            |at org.scalatest.wordspec.AnyWordSpecLike$$anon$3.apply(AnyWordSpecLike.scala:1076)
            |at org.scalatest.TestSuite.withFixture(TestSuite.scala:196)
            |at org.scalatest.TestSuite.withFixture$(TestSuite.scala:195)""".stripMargin)

        val relevant = TestStackTraceInfoExtractor.mostRelevantTraceElement(trace).get
        relevant.getFileName mustEqual "RemoteFailureSpec.scala"
        relevant.getLineNumber mustBe 60
      }
    }
  }

  val TraceLineR = """at (.+)\.([^(]+)\(([^:]+):([^)]+)\)""".r
  def stackTrace(stackTrace: String): Array[StackTraceElement] = {
    stackTrace.split("\n")
      .map {
      case TraceLineR(className, methodName, fileName, lineNumber) =>
          new StackTraceElement(className, methodName, fileName, lineNumber.toInt)
    }
  }
}
