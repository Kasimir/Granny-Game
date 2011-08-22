/**

package de.granny

import net.liftweb.mongodb._

object GameNodeTypes extends Enumeration{
  val StartNode = Value("StartNode")
  val WonNode = Value("WonNode")
  val RegularNode = Value("Regular")
}

class MongoAdapter {
  def findeGameNode (hashCode : Int) : Option[GameNode] = {
  }
  
  def storeGameNode( node: GameNode) : Unit = {
  }

  private def toDoc (node: GameNode): GameModeDoc = {
  }
  
  private def toNode(doc : GameNodeDoc) : GameNode = {
  } 
}



class GameNodeDoc private() extends MongoRecord[GameNodeDoc]{
  def meta = GameNodeDoc
  
  // hashCode goes here
  object id extends IntField(this)
  
  //class goes here Mapped by enum  
  object nodeType extends StringField(this,10)
  
  //Translation
  object mirror extends BooleanField(this)
  object clockwise extends IntField(this)
  
  //Board
  object board extends 
}

object GameNodeDoc extends GameNodeDoc with MongoMetaRecord[GameNodeDoc]

**/