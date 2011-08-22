package de.granny

//import org.scala-tools.scala-stm._

object NodeStore{
  def contains(hashCode:Int): Boolean = {
    return MongoAdapter.findGameNode(hashCode) match ( case Some(x) : true ; case _ : false)
  }
  
  def put (key :Int, value :GameNode) : Unit ={
    MongoAdapter.storeGameNode(value)
  }

  def apply(hashCode :Int): GameNode = {
    return MongoAdapter.findGameNode(hashCode) match ( case Some(x) : x ; case _ : null)
  }
}

// extends TMap[Int, GameNode]