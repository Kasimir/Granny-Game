package de.granny

object Main{
  def main (args: Array[String]) : Unit = {
    Boot.boot
    Player.solve(SolitaireBoard)
  }
}