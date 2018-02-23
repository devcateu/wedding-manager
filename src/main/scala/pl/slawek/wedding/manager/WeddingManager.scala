package pl.slawek.wedding.manager

import akka.actor.{Actor, ActorRef, Props}
import pl.slawek.wedding.manager.WeddingManager.CreateWedding

object WeddingManager {

  def props(): Props = Props[WeddingManager]

  final case class CreateWedding(weddingName: String, women: Person, men: Person)

}

class WeddingManager extends Actor {

  var weddings: Map[String, ActorRef] = Map()

  override def receive: Receive = {
    case CreateWedding(weddingName, women, men) =>
      if (weddings.contains(weddingName)) {
        throw WeddingWithThatNameExist()
      }
      val ref: ActorRef = context.actorOf(Wedding.props())

      weddings = weddings + ((weddingName, ref))
      println(s"ura ura $weddingName women:$women men:$men   $weddings")

  }
}

object WeddingWithThatNameExist {
  def apply(): WeddingWithThatNameExist = new WeddingWithThatNameExist()
}

class WeddingWithThatNameExist extends Exception