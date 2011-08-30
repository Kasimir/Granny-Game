package de.granny

import scala.collection.mutable.HashMap

object NodeStore extends MongoNodeStore {
}

class HashMapNodeStore extends NodeSave {
  val myMap = new HashMap[Int,GameNode]
  def saveNode(node:GameNode) : Unit = {
    myMap += (node.hashCode -> node)
  }
    
  def getNode(id:Int) : Option[GameNode] = myMap.get(id)

}