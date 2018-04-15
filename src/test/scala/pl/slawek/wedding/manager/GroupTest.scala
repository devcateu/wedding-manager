package pl.slawek.wedding.manager

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.slawek.wedding.manager.Group.{AddGuestToGroup, GroupIdentification, GuestIdentifiersInGroup, RetrieveGuestsIdentifiers}
import pl.slawek.wedding.manager.Guest.GuestIdentifier

class GroupTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with Matchers
  with BeforeAndAfterAll {

  "Group " should {
    " allow to group guests " in {
      val group = TestActorRef(Group.props())

      val groupIdentification = GroupIdentification("123")

      group ! AddGuestToGroup(groupIdentification, GuestIdentifier("g-1"))
      group ! AddGuestToGroup(groupIdentification, GuestIdentifier("g-2"))

      group ! RetrieveGuestsIdentifiers()

      expectMsg(GuestIdentifiersInGroup(List(GuestIdentifier("g-2"), GuestIdentifier("g-1"))))
    }
  }

}
