package de.granny
 
import org.scalatest.Spec

class BoardSpec extends Spec {
 
  describe("Easy Board") {
           
    it("should have one next board") {
      expect(1) { EasyBoard.nextBoards.size}
    }
    
    it("should have one unique next board") {
      expect(1) {EasyBoard.uniqueNextBoards.size}
    }
    
    it("should have only one unique next board"){        
        val idList : List[Int]= (for(i <- 1 to 10) yield EasyBoard.uniqueNextBoards.head.signatureId).toList
        val filteredList = idList filter (_ == idList.head)         
        expect(10) {filteredList.size}
    }        
  }
  
  describe("WoistBoard"){
    val woistBoard = new Board(
      List(2, 2, 0, 1, 1, 2, 2) ::
      List(2, 2, 1, 1, 1, 2, 2) ::
      List(1, 1, 1, 0, 0, 0, 1) ::
      List(1, 1, 1, 0, 1, 0, 0) ::
      List(1, 1, 1, 1, 1, 0, 1) ::
      List(2, 2, 1, 2, 1, 2, 2) ::
      List(2, 2, 1, 1, 1, 2, 1) :: Nil)
    val translations = woistBoard.translations
    //println(translations)
    val translationIds = translations.map( _.board.uniqueSignature.signatureId ).filter(x => x == woistBoard.uniqueSignature.signatureId)
      
      it("all translations should have same set of unique next boards"){
        expect(8) {translations.size}
        expect(8) {translationIds.size}
      }
          
  }
  
  describe("Symmetric Board"){
    val symBoard = new Board(
      List(2, 2, 0, 0, 0, 2, 2) ::
      List(2, 2, 0, 1, 0, 2, 2) ::
      List(0, 0, 0, 1, 0, 0, 0) ::
      List(0, 1, 1, 0, 1, 1, 0) ::
      List(0, 0, 0, 1, 0, 0, 0) ::
      List(2, 2, 0, 1, 0, 2, 2) ::
      List(2, 2, 0, 0, 0, 2, 2) :: Nil)      
      it("should have one unique next boards"){
        val unb = symBoard.uniqueNextBoards
        expect(2) {unb.size}        
      }
          
  }
  
  
  
}