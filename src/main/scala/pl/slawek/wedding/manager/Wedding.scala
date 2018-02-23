package pl.slawek.wedding.manager

import akka.actor.{Actor, Props}

object Wedding {
  def props(): Props = Props[Wedding]
}

class Wedding extends Actor {
  override def receive: Receive = Actor.emptyBehavior
}