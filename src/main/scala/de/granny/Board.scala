package de.granny


class Board(val board: List[List[Int]]) {

  /*
   * the default board
   */
  def this() = this(
    List(1, 1, 1, 1, 1) ::
      List(1, 1, 1, 1, 1) ::
      List(1, 1, 0, 1, 1) ::
      List(1, 1, 1, 1, 1) ::
      List(1, 1, 1, 1, 1) :: Nil)

  def state: String = if (won) "Won!" else if (nextBoards.isEmpty) "Impossible!" else ".. go on .."

  def won = board.flatten.count(_ == 1) == 1

  /*
   * Lists possible next boards
   */
  def nextBoards: List[Board] = {
    /*
    * finds all possible horizontal next lines
    */
    def findHorizontals(inputHorizontal: List[Int]): List[List[Int]] = {
      inputHorizontal match {
        case x :: y :: z :: l =>
          (x, y, z) match {
            case (1, 1, 0) => return (0 :: 0 :: 1 :: l) :: findHorizontals(inputHorizontal.tail).map(x :: _)
            case (0, 1, 1) => return (1 :: 0 :: 0 :: l) :: findHorizontals(inputHorizontal.tail).map(x :: _)
            case _ => {
              val rest = findHorizontals(inputHorizontal.tail)
              return rest.map(x :: _)
            }
          }
        case _ => Nil
      }
    }
    /*
  	* finds all possible horizontal next boards
  	*/
    def nextBoardsHorizontal(input: List[List[Int]]): List[List[List[Int]]] = {
      input match {
        case Nil => Nil
        case _ => {
          return findHorizontals(input.head).map(_ :: input.tail) ::: nextBoardsHorizontal(input.tail).map(input.head :: _)
        }
      }
    }
    /*
    * finds all possible vertical next boards
    */
    def nextBoardsVertical(input: List[List[Int]]): List[List[List[Int]]] = {
      val turnedBoard = turnBoardClockwise(board);
      val newTurnedBoards = nextBoardsHorizontal(turnedBoard)
      return newTurnedBoards.map(turnBoardCounterClockwise(_))
    }
    return (nextBoardsHorizontal(board) ::: nextBoardsVertical(board)).map(new Board(_))
  }

  private def turnBoardClockwise(inputTurn: List[List[Int]]): List[List[Int]] =
    {
      if (inputTurn == Nil || inputTurn.head == Nil)
        return Nil
      return inputTurn.map(_.head).reverse :: turnBoardClockwise(inputTurn.map(_.tail))
    }

  private def turnBoardCounterClockwise(inputTurn: List[List[Int]]): List[List[Int]] = {
    return turnBoardClockwise(inputTurn.map(_.reverse).reverse)
  }

  private def mirror(input: List[List[Int]]): List[List[Int]] = {
    input.reverse
  }

  def mirror : Board = new Board(mirror(board))

  def clockwise : Board = new Board(turnBoardClockwise(board))

  def counterClockwise : Board = new Board(turnBoardCounterClockwise(board))

  override def toString() = state + "\n" + board.map(_.map(_ match {
    case 0 => " "
    case 1 => "O"
    case _ => "X"
  }).mkString + "\n").mkString

  /*
   * gives the signature of a board. Therefore all translations of a board are calculated and the string with the lowest lexicografical value is taken 
   */
  def uniqueSignature = translations.sortWith((s, t) => s.hashCode < t.hashCode).head

  /*
  * the translations of the board
  */
  def translations: List[BoardSignature] = {
    var translations: List[BoardSignature] = new BoardSignature(signature(board), new BoardTranslation(0, false), this) :: Nil
    for (i <- 1 to 3) {
      val newBoard = turnBoardClockwise(translations.head.board.board)
      val oldTranslation = translations.head.translation
      translations = new BoardSignature(signature(newBoard), new BoardTranslation(oldTranslation.clockwise + 1, oldTranslation.mirror), new Board(newBoard)) :: translations
    }
    val mirroredBoard = mirror(board)
    translations = new BoardSignature(signature(mirroredBoard), new BoardTranslation(0, true), new Board(mirroredBoard)) :: translations
    for (i <- 1 to 3) {
      val newBoard = turnBoardClockwise(translations.head.board.board)
      val oldTranslation = translations.head.translation
      translations = new BoardSignature(signature(newBoard), new BoardTranslation(oldTranslation.clockwise + 1, oldTranslation.mirror), new Board(newBoard)) :: translations
    }
    return translations
  }

  /*
  * gives the signature of a board
  */
  def signature(board: List[List[Int]]) = board.flatten.mkString.hashCode

  /*
  * gives the unique next boards
  */
  def uniqueNextBoards: List[BoardSignature] = {
    val nB = nextBoards;
    var result: List[BoardSignature] = Nil
    for (board <- nB) {
      val boardsSignature = board.uniqueSignature
      if (!result.exists(_.hashCode == boardsSignature.hashCode)) {
        result = boardsSignature :: result
      }
    }
    return result
  }
}

/*
* gives information about, how a board was translated
*/
class BoardTranslation(val clockwise: Int, val mirror: Boolean) {
  override def toString() = clockwise + " clockwise turns, Mirror:" + mirror
}

/*
 * represents the signature of a board
 */
class BoardSignature(val hashCod: Int, val translation: BoardTranslation,val board: Board) {
  override def toString = board.toString
}


/*
* represents a draw to board
* @param drawList: a list sorted from first draw to last draw
*/
class DrawHistory (val drawList : List[BoardSignature]){
  override def toString : String= {
    def recToString(clockwisePrev : Int, mirrorPrev : Boolean, remaining : List[BoardSignature]) : String = {
      def correctView(clockwise : Int, mirror : Boolean, board : Board) : Board = {
        def unturn (board : Board, clockwise : Int) : Board = {
          clockwise match {
            case 0 => return board
            case _ => return unturn(board.counterClockwise, clockwise -1)
          }
        }
        val result = if (mirror) board.mirror else board
        return unturn(result, clockwise)
      }
      if(remaining.isEmpty) return ""
      val actTrans = remaining.head.translation
      val actClockwise = (clockwisePrev + actTrans.clockwise) % 4
      val actMirror =  mirrorPrev && ! actTrans.mirror || !mirrorPrev && actTrans.mirror 
      return correctView(actClockwise,actMirror,remaining.head.board).toString + recToString(actClockwise, actMirror, remaining.tail)
    }
    return recToString(0,false,drawList)
  }
}
