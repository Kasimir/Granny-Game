package de.granny
 
import org.scalatest.Spec
import com.mongodb._
import net.liftweb.mongodb._

class SimpleBoardSpec extends Spec {
 
  describe("A simple Game") {
    Boot.boot
    val result = Player.solve(EasyBoard)    
    it("should have results") {
      expect(true) { result.size > 0}
    }
  }
}