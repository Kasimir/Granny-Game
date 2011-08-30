package de.granny

import com.mongodb._
import net.liftweb.mongodb._
import com.foursquare.rogue.Rogue._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.common.{Box, Empty, Full}

object GameNodeTypes extends Enumeration{
  val StartNode = Value("StartNode")
  val WonNode = Value("WonNode")
  val RegularNode = Value("Regular")
  val ReferenceNode = Value("ReferenceNode")
}


object MongoAdapter extends MongoAdapter{
}

class MongoAdapter {

  def store(node: GameNode) = {
    toDoc(node).save  
  }

  def get(id:Int) : Option[GameNode] = {    
    GameNodeDoc.find(id) match {
      case Full(nodeDoc) => return Some(toNode(nodeDoc))
      case _             => return None
    }    
  }
  
  private def toDoc (node: GameNode): GameNodeDoc = {
    def toBoardDoc(board : Board) : List[BoardRowDoc] = {
      return board.board.map(row => BoardRowDoc.createRecord.row(row))
    }
    val result = GameNodeDoc.createRecord
      .id(node.boardSignature.signatureId)
      .stones(node.boardSignature.board.stones)
      .board(toBoardDoc(node.boardSignature.board))
      .mirror(node.boardSignature.translation.mirror)
      .clockwise(node.boardSignature.translation.clockwise)
      node match {
        case _:StartNode => result
                            .nodeType(GameNodeTypes.StartNode)                            
        case wn:WonNode => result
                            .nodeType(GameNodeTypes.WonNode)
                            .refFather(wn.fatherNodeId)                          
        case regn :RegularDrawNode=> result
                            .nodeType(GameNodeTypes.RegularNode)
                            .refFather(regn.fatherNodeId)
        case refn: ReferenceNode => result
                            .nodeType(GameNodeTypes.ReferenceNode)
                            .refFather(refn.fatherNodeId)
                            .refReferenced(refn.referencedNodeId) 
      }
      return result      
  }
  
  private def toNode(doc : GameNodeDoc) : GameNode = {
    val board = new Board(doc.board.is map (rowDoc => rowDoc.row.is ))
    val boardTranslation = new BoardTranslation(doc.clockwise.is, doc.mirror.is)
    val boardSignature = new BoardSignature(doc.id.is, boardTranslation, board)
    val result = doc.nodeType.is match {
      case GameNodeTypes.StartNode => new StartNode(boardSignature)
      case GameNodeTypes.WonNode => new WonNode(boardSignature, doc.refFather.is)
      case GameNodeTypes.RegularNode => new RegularDrawNode(boardSignature, doc.refFather.is)
      case GameNodeTypes.ReferenceNode => new ReferenceNode(boardSignature, doc.refFather.is, doc.refReferenced.is)
      case _ => null
    }
    return result
  } 
}


class GameNodeDoc private() extends MongoRecord[GameNodeDoc] with IntPk[GameNodeDoc]{
  def meta = GameNodeDoc
  
  // the stones on this nodes board
  object stones extends IntField(this)  
  
  //Translation
  object mirror extends BooleanField(this)
  object clockwise extends IntField(this)
  
  //Board
  object board extends BsonRecordListField(this,BoardRowDoc)  

  //class goes here Mapped by enum  
  object nodeType extends EnumNameField(this,GameNodeTypes)

  // Father Node
  object refFather extends IntField(this)
    
  // ReferencedNode
  object refReferenced extends IntField(this)
}

object GameNodeDoc extends GameNodeDoc with MongoMetaRecord[GameNodeDoc]{
}

class BoardRowDoc private() extends BsonRecord[BoardRowDoc]{
  def meta = BoardRowDoc
  object row extends MongoListField[BoardRowDoc,Int](this)
}

object BoardRowDoc extends BoardRowDoc with BsonMetaRecord[BoardRowDoc]{
}



