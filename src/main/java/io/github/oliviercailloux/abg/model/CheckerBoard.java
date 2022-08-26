package io.github.oliviercailloux.abg.model;


import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import io.github.oliviercailloux.abg.model.enums.Piece;

@Entity
public class CheckerBoard extends MyBoard {

  @OneToMany(mappedBy = "checkerBoard")
  List<PiecePosition> piecePositions;
  
  
  private CheckerBoard(List<PiecePosition> piecePositions) {
    this.piecePositions = piecePositions;
    this.fen = this.toString();
  }
  
  public CheckerBoard() {
    String fenString ="";
    piecePositions = new ArrayList<>();
    for (int i = 0; i <= 19; i++) {
      PiecePosition piecePosition = PiecePosition.Of(i, Piece.BLACK_PAWN);
      fenString = fenString + piecePosition.toString();
      piecePositions.add(i, piecePosition);
    }
    for (int i = 20;  i <=29; i++) {
      PiecePosition piecePosition = PiecePosition.Of(i, Piece.EMPTY);
      piecePositions.add(i,piecePosition);  
    }
    for (int i = 30; i <= 49; i++) {
      PiecePosition piecePosition = PiecePosition.Of(i, Piece.WHITE_PAWN);
      fenString = fenString + piecePosition.toString();
      piecePositions.add(i, piecePosition);
    }
    this.fen = fenString;
  }

  public static CheckerBoard initialBoard() {
    return new CheckerBoard();
  }
  
  public static CheckerBoard of(List<PiecePosition> piecePositions) {
    return new CheckerBoard(piecePositions);
  }
  
  
  public List<PiecePosition> toPiecePositions(String fenReg){
    String [] posStrings = fenReg.split("/");
    List<PiecePosition> piecePositions1 = new ArrayList<>();
    PiecePosition piecePosition;
    for (String string : posStrings) {
      piecePosition = PiecePosition.Of(string);
      piecePositions1.add(piecePosition); 
    } 
    return piecePositions1;
  }

  public List<PiecePosition> getPiecePositions() {
    return piecePositions;
  }

  @Override
  public Side getSideToMove() {
    return sideToMove;
  }

  @Override
  public MyBoard doMoves(List<MoveEntity> moves) throws MoveException {
    return null;
  }
  
  @Override
  public String toString() {
    String positions = "";
    for (PiecePosition piecePosition : piecePositions) {
      positions = positions + piecePosition.toString();   
    }
    return positions;
  }

  @Override
  public String getFen() {
    return fen;
  }
}
