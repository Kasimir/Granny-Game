package de.granny

import scala.collection.mutable.Map
import akka.stm._

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
      NodeStore += (current.hashCode -> regularDrawNode)
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
}

abstract class GameNode(val boardSignature: BoardSignature) {}

case class RegularDrawNode(val regSignature: BoardSignature, val fatherNode: GameNode) extends GameNode(regSignature) {
  override def toString = boardSignature + " father " + fatherNode.boardSignature
}

case class WonNode(val wonSignature: BoardSignature, val wonFatherNode: GameNode) extends GameNode(wonSignature) {
  override def toString = boardSignature + " father " + wonFatherNode.boardSignature
  def winningDraws: List[BoardSignature] = {
    def extractWinningDraws(node: GameNode): List[BoardSignature] = {
      node match {
        case tNode: StartNode => return tNode.startSignature :: Nil
        case tNode: RegularDrawNode => return tNode.boardSignature :: extractWinningDraws(tNode.fatherNode)
        case _ => return Nil
      }
    }
    return (wonSignature :: extractWinningDraws(wonFatherNode)).reverse
  }

}

case class StartNode(val startSignature: BoardSignature) extends GameNode(startSignature) {
  override def toString = "Start Board " + startSignature
}

case class ReferenceNode(val refSignature: BoardSignature, val refFatherNode: GameNode, val referencedNode: GameNode) extends GameNode(refSignature) {
  override def toString = "referencing: " + referencedNode
}

object NodeStore extends TMap[Int, GameNode]
