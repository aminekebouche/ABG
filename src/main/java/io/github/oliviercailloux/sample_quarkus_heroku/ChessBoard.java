package io.github.oliviercailloux.sample_quarkus_heroku;


import static com.google.common.base.Preconditions.checkNotNull;


import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;



/**
 *
 * @author elbaylot
 *
 */
@Entity
@Table(name = "boards")
public class ChessBoard {


  public static final ChessBoard STARTING_CHESS_BOARD = new ChessBoard();
  @Id
  @GeneratedValue
  int id;
  String fen;
  Side sideToMove;


  ChessBoard() {
    this.fen = GameEntity.STARTING_FEN_POSITION;
    this.sideToMove = Side.WHITE;
  }

  private ChessBoard(Board board) {
    checkNotNull(board);
    this.fen = board.getFen(true);
    this.sideToMove = board.getSideToMove();
  }

  public static ChessBoard createChessBoard() {
    return new ChessBoard();
  }

  public static ChessBoard createChessBoard(String fen) {
    final Board board = new Board();
    board.loadFromFen(fen);
    return new ChessBoard(board);
  }

  public static ChessBoard createChessBoard(Board board) {
    return new ChessBoard(board);
  }

  public int getId() {
    return id;
  }

  public String getFen() {
    return fen;
  }

  public Side getSideToMove() {
    return sideToMove;
  }

  public ChessBoard doMove(MoveEntity move) throws MoveException {
    final Board board = asBoard();
    if (!board.doMove(move.asMove(), true)) {
      throw new MoveException();
    }
    return createChessBoard(board);
  }

  public ChessBoard doMoves(List<MoveEntity> moves) throws MoveException {
    final Board board = asBoard();
    for (MoveEntity move : moves) {
      if (!board.doMove(move.asMove(), true)) {
        throw new MoveException();
      }
    }
    return createChessBoard(board);
  }

  public Board asBoard() {
    final Board board = new Board();
    board.loadFromFen(fen);
    return board;
  }
}