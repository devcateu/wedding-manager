package pl.slawek.wedding.manager

import akka.actor.{ActorRef, ActorSystem}
import pl.slawek.wedding.manager.WeddingManager.CreateWedding

object AkkaWeddingManagerApp extends App {


  val system: ActorSystem = ActorSystem("helloAkka")

  private val slawek: ActorRef = system.actorOf(WeddingManager.props(), "slawek")

  slawek ! CreateWedding("bestOf", Person("Kasia"), Person("Slawek"))
}
