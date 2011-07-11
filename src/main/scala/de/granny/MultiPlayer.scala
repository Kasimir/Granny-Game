package de.granny

import scala.collection.mutable.Map
import akka.stm._
import akka.actor.{Actor, PoisonPill}
import Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._

class MultiPlayer extends Actor{
  val myPlayer = new Player  
  def receive = {
    case Work(node) => self reply  WorkResult( myPlayer.play(node) map (Work(_))) _
  }
}



sealed trait MultiMessage
case class Work(val node:GameNode) extends MultiMessage
case class WorkResult(moreWork : List[Work]) extends MultiMessage

