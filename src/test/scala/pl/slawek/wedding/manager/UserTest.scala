package pl.slawek.wedding.manager

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.slawek.wedding.manager.User._
import pl.slawek.wedding.manager.UserManager.{UserDoesNotExist, UserExists, VerifyUserExist}

class UserTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  private val incorrectEmail = "slkw@gmailcom"
  private val correctEmail = "slkw@gmail.com"
  "CreateUser " should {
    " return successful message " in {

      val userManager = TestActorRef[UserManager](UserManager.props())
      val receiver = TestProbe()

      val user = CreateUser(UserIdentifier(correctEmail), receiver.ref)

      userManager ! user

      receiver.expectMsgType[UserCreatedSuccessfully]
    }
    " return wrong message when invalid email " in {

      val userManager = TestActorRef[UserManager](UserManager.props())
      val receiver = TestProbe()

      val user = CreateUser(UserIdentifier(incorrectEmail), receiver.ref)

      userManager ! user

      receiver.expectMsgType[UserCannotBeUpdated].reason shouldEqual UpdateRejectionReason.EMAIL_IS_INVALID
    }
    " return wrong message when user with such email exist already " in {

      val userManager = TestActorRef[UserManager](UserManager.props())
      val receiver = TestProbe()

      val user1 = CreateUser(UserIdentifier(correctEmail), TestProbe().ref)
      val user2 = CreateUser(UserIdentifier(correctEmail), receiver.ref)

      userManager ! user1
      userManager ! user2

      receiver.expectMsgType[UserCannotBeUpdated].reason shouldEqual UpdateRejectionReason.USER_WITH_SUCH_ID_EXIST
    }
  }

  "VerifyUser " should {
    " return Exist when user was created" in {
      val userManager = TestActorRef[UserManager](UserManager.props())

      val user1 = CreateUser(UserIdentifier(correctEmail), TestProbe().ref)
      userManager ! user1

      val receiver = TestProbe()
      userManager ! VerifyUserExist(UserIdentifier(correctEmail), receiver.ref)

      receiver.expectMsgType[UserExists].userIdentifier shouldEqual UserIdentifier(correctEmail)
    }

    " return Not Exist when user was not created" in {
      val userManager = TestActorRef[UserManager](UserManager.props())

      val receiver = TestProbe()
      userManager ! VerifyUserExist(UserIdentifier(correctEmail), receiver.ref)

      receiver.expectMsgType[UserDoesNotExist].userIdentifier shouldEqual UserIdentifier(correctEmail)
    }

    " return Not Exist after failed tried of add user" in {
      val userManager = TestActorRef[UserManager](UserManager.props())

      val user1 = CreateUser(UserIdentifier(incorrectEmail), TestProbe().ref)
      userManager ! user1
      val receiver = TestProbe()
      userManager ! VerifyUserExist(UserIdentifier(correctEmail), receiver.ref)

      receiver.expectMsgType[UserDoesNotExist].userIdentifier shouldEqual UserIdentifier(correctEmail)
    }
  }
}
