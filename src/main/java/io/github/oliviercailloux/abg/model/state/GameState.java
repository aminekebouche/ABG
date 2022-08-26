package io.github.oliviercailloux.abg.model.state;

import static java.util.Objects.requireNonNull;

import com.github.bhlangonijr.chesslib.Side;
import io.github.oliviercailloux.abg.model.ChessBoard;
import io.github.oliviercailloux.abg.model.MyBoard;
import java.util.EnumMap;

/**
 * 
 * @author theophile
 *
 */
public class GameState {

  private EnumMap<Side, PlayerState> playerStates;
  private MyBoard board;

  private GameState(MyBoard board, PlayerState whitePlayer, PlayerState blackPlayer) {
    requireNonNull(board);
    requireNonNull(whitePlayer);
    requireNonNull(blackPlayer);
    this.board = board;
    this.playerStates = new EnumMap<>(Side.class);
    playerStates.put(Side.WHITE, whitePlayer);
    playerStates.put(Side.BLACK, blackPlayer);
  }

  public EnumMap<Side, PlayerState> getPlayerStates() {
    return playerStates;
  }

  public MyBoard getChessBoard() {
    return board;
  }

  public boolean isSideToMove(Side side) {
    return side == board.getSideToMove();
  }

  public PlayerState getPlayerState(Side side) {
    return playerStates.get(side);
  }

  public PlayerState getCurrentPlayerState() {
    return getPlayerState(board.getSideToMove());
  }

  public static GameState of(MyBoard board, PlayerState whitePlayer, PlayerState blackPlayer) {
    return new GameState(board, whitePlayer, blackPlayer);
  }
}
