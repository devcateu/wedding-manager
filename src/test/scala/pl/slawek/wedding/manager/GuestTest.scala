package pl.slawek.wedding.manager

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.slawek.wedding.manager.Guest._

import scala.language.postfixOps

class GuestTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  "Guest " should {
    " accept when receive AcceptInvitation" in {
      val underTest = TestActorRef(Guest.props(GuestIdentifier("123"), GuestName("Nick", "Johnson")))
      val receiver = TestProbe()

      retrieveInvitationStatus(underTest, receiver) shouldEqual Guest.InvitationStatus.NO_ANSwER

      underTest ! AcceptInvitation

      retrieveInvitationStatus(underTest, receiver) shouldEqual Guest.InvitationStatus.ACCEPTED
    }
    " reject when receive RejectInvitation" in {
      val underTest = TestActorRef(Guest.props(GuestIdentifier("123"), GuestName("Nick", "Johnson")))
      val receiver = TestProbe()

      retrieveInvitationStatus(underTest, receiver) shouldEqual Guest.InvitationStatus.NO_ANSwER

      underTest ! RejectInvitation

      retrieveInvitationStatus(underTest, receiver) shouldEqual Guest.InvitationStatus.REJECTED
    }
    " could request of needing accommodation" in {
      val underTest = TestActorRef(Guest.props(GuestIdentifier("123"), GuestName("Nick", "Johnson")))
      val receiver = TestProbe()

      retrieveGuestInfo(underTest, receiver).accommodation shouldEqual false

      underTest ! RequiredAccommodation

      retrieveGuestInfo(underTest, receiver).accommodation shouldEqual true
    }
  }

  private def retrieveInvitationStatus(underTest: TestActorRef[Nothing], receiver: TestProbe) = {
    val guestInfo: GuestInfo = retrieveGuestInfo(underTest, receiver)

    guestInfo.invitationStatus
  }

  private def retrieveGuestInfo(underTest: TestActorRef[Nothing], receiver: TestProbe) = {
    underTest ! RetrieveInfo(receiver.ref)

    val guestInfo = receiver.expectMsgType[GuestInfo]
    guestInfo
  }
}
