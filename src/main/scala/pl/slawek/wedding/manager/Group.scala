package pl.slawek.wedding.manager

import akka.actor.{Actor, Props}
import pl.slawek.wedding.manager.Group.{AddGuestToGroup, GuestIdentifiersInGroup, RetrieveGuestsIdentifiers}
import pl.slawek.wedding.manager.Guest.GuestIdentifier

object Group {
  def props() = Props[Group]

  case class GroupName(name: String)

  case class GroupIdentification(id: String)

  // requests
  case class AddGuestToGroup(groupIdentification: GroupIdentification, guestIdentifier: GuestIdentifier)

  case class RetrieveGuestsIdentifiers()

  // response
  case class GuestIdentifiersInGroup(guestIdentifiers: List[GuestIdentifier])

}

class Group() extends Actor {

  var guestIdentifiers = List[GuestIdentifier]()

  override def receive: Receive = {
    case AddGuestToGroup(_, identifier) =>
      guestIdentifiers = identifier :: guestIdentifiers
    case RetrieveGuestsIdentifiers() =>
      sender() ! GuestIdentifiersInGroup(guestIdentifiers)
  }
}
