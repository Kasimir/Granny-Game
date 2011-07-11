package de.granny

object SolitaireStartNode extends StartNode(new BoardSignature(SolitaireBoard.uniqueSignature.hashCode, null, SolitaireBoard)) {

}

object EasyGame extends StartNode(new BoardSignature(EasyBoard.uniqueSignature.hashCode, null, EasyBoard)){
}

object EasyBoard extends Board(
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 0, 2, 2) ::
    List(2, 2, 2, 2, 2) ::
    List(2, 2, 2, 2, 2) :: Nil)

object SolitaireBoard extends Board(
    List(2, 2, 1, 1, 1, 2, 2) ::
    List(2, 2, 1, 1, 1, 2, 2) ::
    List(1, 1, 1, 1, 1, 1, 1) ::
    List(1, 1, 1, 0, 1, 1, 1) ::
    List(1, 1, 1, 1, 1, 1, 1) ::
    List(2, 2, 1, 1, 1, 2, 2) ::
    List(2, 2, 1, 1, 1, 2, 2) :: Nil)

