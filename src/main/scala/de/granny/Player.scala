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
      if (NodeStore.contains(current.hashCode))
        return ReferenceNode(current, node, NodeStore(current.hashCode)) :: tailResult
      else if (current.board.won) return WonNode(current, node) :: tailResult
      val regularDrawNode = RegularDrawNode(current, node)
      NodeStore put (current.hashCode , regularDrawNode)
      return regularDrawNode :: tailResult
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
    return solveDeep(makeStartNode(board))
  } 
  
  private def makeStartNode(board: Board) : StartNode = new StartNode(new BoardSignature(board.uniqueSignature.hashCode, new BoardTranslation(0,false), board))
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
      node match {
        case tNode: StartNode => return tNode.boardSignature :: Nil
        case tNode: RegularDrawNode => return tNode.boardSignature :: extractDraws(tNode.fatherNode)
        case tNode: WonNode => return tNode.boardSignature :: extractDraws(tNode.fatherNode) 
        case _ => return Nil
      }
    }
    return extractDraws(this).reverse
  }
}

case class StartNode(override val boardSignature: BoardSignature) extends GameNode(boardSignature) {
  override def toString = "Start Board " + boardSignature
}

case class RegularDrawNode(override val boardSignature: BoardSignature, val fatherNode: GameNode) extends GameNode(boardSignature) {
  override def toString = boardSignature.toString
}

case class WonNode(override val boardSignature: BoardSignature, val fatherNode: GameNode) extends GameNode(boardSignature) {
  override def toString = boardSignature.toString
}



case class ReferenceNode(override val boardSignature: BoardSignature, val fatherNode: GameNode, val referencedNode: GameNode) extends GameNode(boardSignature) {
  override def toString = "referencing: " + referencedNode
}
