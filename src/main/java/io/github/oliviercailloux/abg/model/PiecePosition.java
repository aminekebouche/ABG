package io.github.oliviercailloux.abg.model;import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.abg.model.enums.Piece;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.swing.text.Position;
import net.bytebuddy.description.ModifierReviewable.OfAbstraction;

@Entity
public class PiecePosition {
  
  @Id
  @GeneratedValue
  int id;

  private int position;
  private Piece piece;
  @ManyToOne
  private MyBoard checkerBoard;
  
  
  PiecePosition() {
    this.position = -1;
    this.piece = Piece.EMPTY;
  }
  
  public static PiecePosition Of(int position, Piece piece) {
    return new PiecePosition(position, piece); 
  }
  
  public static PiecePosition Of(String s) {
    checkNotNull(s);
    int pos = Integer.parseInt(s.substring(1,3));
    Character sPiece = s.charAt(0);
    Piece piece = Piece.EMPTY;
    switch (sPiece) {
      case 'P':
        piece = Piece.WHITE_PAWN;
        break;
      case 'p':
        piece = Piece.BLACK_PAWN;
        break;
      case 'Q':
        piece = Piece.WHITE_QUEEN;
        break;
      case 'q':
        piece = Piece.BLACK_QUEEN;
        break;
      default:
        break;
    }  
    return new PiecePosition(pos,piece); 
  }
  
  private PiecePosition(int position, Piece piece) {
    this.position = position;
    this.piece = piece;
  }
  

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }
  
  @Override
  public String toString() {
    String pi = "";
    switch (piece) {
      case BLACK_PAWN:
        pi="p";
        break;
      case BLACK_QUEEN:
        pi="q";
        break;
      case EMPTY:
        pi="";
        break;
      case WHITE_QUEEN:
        pi="Q";
        break;
      case WHITE_PAWN:
        pi="P";
        break;
      default:
        break;
    }
    
    return pi+position+"/";
    
  }
  
}
