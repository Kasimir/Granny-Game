package de.granny

import akka.stm._

object NodeStore extends TMap[Int, GameNode]