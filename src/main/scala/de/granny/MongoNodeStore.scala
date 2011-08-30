package de.granny

class MongoNodeStore extends NodeSave {      
  
  def getNode(key:Int) : Option[GameNode] = {
    return MongoAdapter.get(key)
  }
  
  def saveNode(node:GameNode) : Unit = {
    MongoAdapter.store(node)
  }  
}
