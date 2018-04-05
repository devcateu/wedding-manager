package pl.slawek.wedding.manager

import akka.actor.{Actor, ActorRef, Props}
import pl.slawek.wedding.manager.Group.GroupIdentification
import pl.slawek.wedding.manager.Guest.InvitationStatus.InvitationStatus
import pl.slawek.wedding.manager.Guest._

import scala.util.Random

object Guest {
  def props(guestIdentifier: GuestIdentifier, guestName: GuestName) =
    Props(new Guest(guestIdentifier, guestName))

  object InvitationStatus extends Enumeration {
    type InvitationStatus = Value
    val ACCEPTED, REJECTED, NO_ANSwER = Value
  }

  case class AcceptInvitation()

  case class RejectInvitation()

  case class RequiredAccommodation()

  case class AccommodationNotNeed()

  case class RetrieveInfo(receiver: ActorRef)

  case class ChangeName(guestName: GuestName)

  case class AssignToGroup(groupIdentification: GroupIdentification)

  //
  case class GuestName(name: String, surname: String)

  case class GuestIdentifier(id: String)

  case class GuestInfo(id: GuestIdentifier, guestName: GuestName, invitationStatus: InvitationStatus, accommodation: Boolean)

}

class Guest(guestIdentifier: GuestIdentifier, private var guestName: GuestName) extends Actor {
  var invitationStatus: InvitationStatus = InvitationStatus.NO_ANSwER
  var accommodation: Boolean = false

  override def receive: Receive = {
    case AcceptInvitation => invitationStatus = InvitationStatus.ACCEPTED
    case RejectInvitation => invitationStatus = InvitationStatus.REJECTED
    case RequiredAccommodation => accommodation = true
    case AccommodationNotNeed => accommodation = false
    case RetrieveInfo(receiver) => receiver ! GuestInfo(guestIdentifier, guestName, invitationStatus, accommodation)
    case ChangeName(newGuestName) => this.guestName = newGuestName
  }
}

case class AddGuest(guestName: GuestName, receiver: ActorRef)

case class GuestRequest(identifier: GuestIdentifier, request: AnyRef)

class GuestManager extends Actor {

  private var mapi = Map[GuestIdentifier, ActorRef]()

  override def receive: Receive = {
    case GuestRequest(identifier, request) =>
      mapi(identifier) ! request
    case AddGuest(guestName, receiver) =>
      val identifier = GuestIdentifier(Random.nextString(4))
      val newGuest = context.actorOf(Guest.props(identifier, guestName))
      mapi = mapi + ((identifier, newGuest))
      receiver ! identifier
  }
}