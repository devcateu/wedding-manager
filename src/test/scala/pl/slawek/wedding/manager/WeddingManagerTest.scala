package pl.slawek.wedding.manager

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.scalatest._
import pl.slawek.wedding.manager.WeddingManager.CreateWedding

class WeddingManagerTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  "WeddingManager " should {
    "create new Wedding actor when receive CreateWedding event" in {

      val weddingManager = TestActorRef[WeddingManager](WeddingManager.props())

      val weddingName = "firstWedding"
      weddingManager ! CreateWedding(weddingName, Person("Kasia"), Person("Me"))

      weddingManager.underlyingActor.weddings should (contain key weddingName)
      weddingManager.underlyingActor.weddings(weddingName) shouldBe an[ActorRef]
    }

    "when trying CreateWedding and wedding with that name exist throw exception" in {
      // val weddingManager = TestActorRef[WeddingManager](WeddingManager.props())

      val weddingName = "wed with repeated name"


      val failures = TestProbe()
      val props = WeddingManager.props()
      val failureParent = system.actorOf(Props(new Actor {
        val child = context.actorOf(props, "child")
        override val supervisorStrategy = OneForOneStrategy() {
          case f => failures.ref ! f; Stop // or whichever directive is appropriate
        }

        def receive = {
          case msg => child forward msg
        }
      }))

      failureParent ! CreateWedding(weddingName, Person("Kasia"), Person("Me"))

      failures.expectNoMsg()

      failureParent ! CreateWedding(weddingName, Person("Kasia"), Person("Me"))

      failures.expectMsgType[WeddingWithThatNameExist]


    }
  }
}
