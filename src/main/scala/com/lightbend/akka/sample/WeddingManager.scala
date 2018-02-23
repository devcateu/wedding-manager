package com.lightbend.akka.sample

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.lightbend.akka.sample.WeddingManager.CreateWedding

object WeddingManager {

  def props(): Props = Props[WeddingManager]

  final case class CreateWedding(weddingName: String, women: Person, men: Person)

}

class WeddingManager extends Actor {

  var weddings: Map[String, ActorRef] = Map()

  override def receive: Receive = {
    case CreateWedding(weddingName, women, men) =>
      val ref: ActorRef = context.actorOf(Wedding.props())
      weddings = weddings + ((weddingName, ref))
      println(s"ura ura $weddingName women:$women men:$men")

  }
}

object Wedding {
  def props(): Props = Props[Wedding]
}

class Wedding extends Actor {
  override def receive: Receive = Actor.emptyBehavior
}

final case class Person(name: String)


object AkkaQuickstartSlawekWedding extends App {


  val system: ActorSystem = ActorSystem("helloAkka")

  private val slawek: ActorRef = system.actorOf(WeddingManager.props(), "slawek")

  slawek ! CreateWedding("bestOf", Person("Kasia"), Person("Slawek"))
}
