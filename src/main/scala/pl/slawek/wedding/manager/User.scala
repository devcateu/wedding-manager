package pl.slawek.wedding.manager

import akka.actor.{Actor, ActorRef, Props}
import pl.slawek.wedding.manager.User.UpdateRejectionReason.UpdateRejectionReason
import pl.slawek.wedding.manager.User.{CreateUser, UserCannotBeUpdated, UserCreatedSuccessfully, UserIdentifier}
import pl.slawek.wedding.manager.UserManager.{UserDoesNotExist, UserExists, VerifyUserExist}
import pl.slawek.wedding.manager.common.Validation


object User {

  object UpdateRejectionReason extends Enumeration {
    type UpdateRejectionReason = Value
    val USER_WITH_SUCH_ID_EXIST, EMAIL_IS_INVALID = Value
  }

  def props(id: UserIdentifier) = Props(new User(id))

  case class UserIdentifier(email: String)

  //requests
  case class CreateUser(userIdentifier: UserIdentifier, receiver: ActorRef)

  //response
  case class UserCannotBeUpdated(reason: UpdateRejectionReason)

  case class UserCreatedSuccessfully()

}

class User(id: UserIdentifier) extends Actor {
  override def receive: Receive = Actor.emptyBehavior

}

object UserManager {
  def props() = Props[UserManager]

  //requests
  case class VerifyUserExist(userIdentifier: UserIdentifier, receiver: ActorRef)

  //response
  case class UserDoesNotExist(userIdentifier: UserIdentifier)

  case class UserExists(userIdentifier: UserIdentifier)

}


class UserManager extends Actor {

  private var users = Map[UserIdentifier, ActorRef]()

  override def receive: Receive = {
    case CreateUser(id, receiver) =>
      if (users.contains(id)) {
        receiver ! UserCannotBeUpdated(User.UpdateRejectionReason.USER_WITH_SUCH_ID_EXIST)
      }
      if (!Validation.isEmailValid(id.email)) {
        receiver ! UserCannotBeUpdated(User.UpdateRejectionReason.EMAIL_IS_INVALID)
      }
      users = users + ((id, context.actorOf(User.props(id))))
      receiver ! UserCreatedSuccessfully()
    case VerifyUserExist(id, receiver) =>
      if (users.contains(id)) {
        receiver ! UserExists(id)
      } else {
        receiver ! UserDoesNotExist(id)
      }

  }
}
