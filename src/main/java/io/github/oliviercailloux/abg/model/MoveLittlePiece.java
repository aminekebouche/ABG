package io.github.oliviercailloux.abg.model;


import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class MoveLittlePiece implements Serializable  {
  
  private Square from;
  private Square to;
  private Pieces promotion;

  private MoveLittlePiece(Square from, Square to, Pieces promotion) {
    this.from = from;
    this.to = to;
    this.promotion = promotion;
  }

  @JsonbCreator
  public static MoveLittlePiece createMoveDAO(@JsonbProperty("from") Square from,
      @JsonbProperty("to") Square to, @JsonbProperty("promotion") Pieces promotion) {
    Preconditions.checkArgument(from != null && from != Square.NONE);
    Preconditions.checkArgument(to != null && to != Square.NONE);
    return new MoveLittlePiece(from, to, promotion);
  }
  
  public Piece getPiece() {
    switch (this.promotion) {
      case WP:
        return Piece.WHITE_PAWN;   
      case WN:
        return Piece.WHITE_KNIGHT;
      case WB:
        return Piece.WHITE_BISHOP;
      case WQ:
        return Piece.WHITE_QUEEN;
      case WK:
        return Piece.WHITE_KING;
      case BP:
        return Piece.BLACK_PAWN;
      case BN:
        return Piece.BLACK_KNIGHT;
      case BB:
        return Piece.BLACK_BISHOP;
      case BQ:
        return Piece.BLACK_QUEEN;
      case BK:
        return Piece.BLACK_KING;
      default:
        return Piece.NONE;
    }
  }

  public Square getFrom() {
    return from;
  }

  public void setFrom(Square from) {
    this.from = from;
  }

  public Square getTo() {
    return to;
  }

  public void setTo(Square to) {
    this.to = to;
  }
  
  

}
