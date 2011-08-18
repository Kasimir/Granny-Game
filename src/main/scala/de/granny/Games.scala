package de.granny

//BEGIN Solitaire Game
object SolitaireBoard extends Board(
    List(2, 2, 1, 1, 1, 2, 2) ::
    List(2, 2, 1, 1, 1, 2, 2) ::
    List(1, 1, 1, 1, 1, 1, 1) ::
    List(1, 1, 1, 0, 1, 1, 1) ::
    List(1, 1, 1, 1, 1, 1, 1) ::
    List(2, 2, 1, 1, 1, 2, 2) ::
    List(2, 2, 1, 1, 1, 2, 2) :: Nil)
// END Solitaire Game

// BEGIN EasyGame
object EasyBoard extends Board(
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 0, 2, 2) ::
    List(2, 2, 2, 2, 2) ::
    List(2, 2, 2, 2, 2) :: Nil)
// END EasyGame

// BEGIN EasyGame2
object EasyBoard2 extends Board(
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 0, 2, 2) ::
    List(2, 2, 1, 2, 2) ::
    List(2, 2, 2, 2, 2) :: Nil)
// END EasyGame2


