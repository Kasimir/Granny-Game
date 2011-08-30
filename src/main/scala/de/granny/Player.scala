package de.granny

import scala.collection.mutable.Map
//import akka.stm._
import scala.actors._

class Player() {
  /*
   *plays a board, returns a list of new tasks (Game Nodes)
  */
  def play(node: GameNode): List[GameNode] = {
    /*
     * takes a list of children board signatures and returns a list of children
     */
    def processChildren(uniqueNextSignatures: List[BoardSignature]): List[GameNode] = {
      if (uniqueNextSignatures.isEmpty) return Nil
      val tailResult = processChildren(uniqueNextSignatures.tail)
      val current = uniqueNextSignatures.head
      val storedObject = NodeStore.getNode(current.signatureId)
      val resultNode  = (storedObject,current.board.won) match {
        case (Some(x),_) => ReferenceNode(current, node.boardSignature.signatureId, current.signatureId)
        case (None,true) => WonNode(current, node.boardSignature.signatureId)
        case (None,false)=> RegularDrawNode(current, node.boardSignature.signatureId)
      }            
      NodeStore.saveNode(resultNode)
      return resultNode :: tailResult
    }
    return processChildren(node.boardSignature.board.uniqueNextBoards)
  }

  /*
   * solves a board, returns only winnung boards
   */
  def solveDeep(node: GameNode): List[GameNode] = {
    def doWork(work: List[GameNode]): List[GameNode] = {
      if (work.isEmpty)
        return Nil
      return solveDeep(work.head) ::: doWork(work.tail)
    }
    val children = play(node)
    val result = children filter { _ match { case _: WonNode => true; case _ => false } }
    val work = children filter { _ match { case _: RegularDrawNode => true; case _ => false } }
    return result ::: doWork(work)
  }
  
  def solve(board: Board) : List[GameNode] = {
    val startNode = makeStartNode(board)
    NodeStore.saveNode(startNode)
    return solveDeep(startNode)
  } 
  
  private def makeStartNode(board: Board) : StartNode = new StartNode(new BoardSignature(board.uniqueSignature.signatureId, new BoardTranslation(0,false), board))
}

object Player extends Player{}

class ActorPlayer(board : Board) extends Player with Actor{   
  def act: Unit = {
    solve(board)
  }  
}


abstract class GameNode(val boardSignature: BoardSignature) {
  def draws: List[BoardSignature] = {  
    def extractDraws(node: GameNode): List[BoardSignature] = {
      def extractIdsDraws(id:Int): List[BoardSignature] = NodeStore.getNode(id) match {case Some(fNode) => extractDraws(fNode);case None => Nil}        
      node match {
        case tNode: StartNode => return tNode.boardSignature :: Nil
        case tNode: RegularDrawNode => return tNode.boardSignature :: extractIdsDraws(tNode.fatherNodeId)
        case tNode: WonNode => return tNode.boardSignature :: extractIdsDraws(tNode.fatherNodeId) 
        case _ => return Nil
      }
    }
    return extractDraws(this).reverse
  }
}

case class StartNode(override val boardSignature: BoardSignature) extends GameNode(boardSignature) {
  override def toString = "Start Board " + boardSignature
}

case class RegularDrawNode(override val boardSignature: BoardSignature, val fatherNodeId: Int) extends GameNode(boardSignature) {
  override def toString = boardSignature.toString
}

case class WonNode(override val boardSignature: BoardSignature, val fatherNodeId: Int) extends GameNode(boardSignature) {
  override def toString = boardSignature.toString
}



case class ReferenceNode(override val boardSignature: BoardSignature, val fatherNodeId: Int, val referencedNodeId: Int) extends GameNode(boardSignature) {
  override def toString = "referencing: " + referencedNodeId
}


trait NodeSave {
  def saveNode(node:GameNode) : Unit
  def getNode(id:Int) : Option[GameNode]
}