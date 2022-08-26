package io.github.oliviercailloux.abg.model;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveException;
import com.google.common.collect.ImmutableList;
import io.github.oliviercailloux.abg.model.state.GameState;
import io.github.oliviercailloux.abg.model.state.PlayerState;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;

/***
 * 
 * @author Delmas Douo Bougna
 * @author Theophile Dano
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Game.find", query = "SELECT e FROM GameEntity e WHERE e.id = :id"),
    @NamedQuery(name = "Game.getLastGameId", query = "SELECT MAX(e.id) FROM GameEntity e")})
public class GameEntity {

  public static final String STARTING_FEN_POSITION =
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
  @Id
  @GeneratedValue
  int id;
  @CreationTimestamp
  Instant startTime;
  /**
   * Duration of the game for each side, by default 30 minutes.
   */
  Duration clockDuration;
  /**
   * Duration of the move increment, by default 10 seconds.
   */
  Duration clockIncrement;
  /**
   * The initial board with which the game starts.
   */
  @OneToOne
  @Cascade(CascadeType.ALL)
  MyBoard startBoard;
  @Cascade(CascadeType.ALL)
  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  List<MoveEntity> moves;

  @OneToMany(mappedBy = "checkerBoard")
  List<PiecePosition> piecePositions;

  public GameEntity() {
    clockDuration = Duration.ofSeconds(1800);
    clockIncrement = Duration.ofSeconds(10);
    moves = new ArrayList<>(); // avoid NPE in tests
  }

  public static GameEntity cerateNewGameWithChess() {
    GameEntity game = new GameEntity();
    ChessBoard startBoard = ChessBoard.createChessBoard();
    game.setMyBoard(startBoard);
    return game;
  }

  public static GameEntity cerateNewGameWithChecker() {
    GameEntity game = new GameEntity();
    CheckerBoard startBoard = CheckerBoard.initialBoard();
    game.setMyBoard(startBoard);
    return game;
  }

  public GameEntity(GameState gameState) {
    this();
    this.startBoard = gameState.getChessBoard();
  }

  public GameEntity(GameState gameState, Instant startTime, Duration clockDuration,
      Duration clockIncrement) {
    this(gameState);
    this.startTime = startTime;
    this.clockDuration = clockDuration;
    this.clockIncrement = clockIncrement;
  }

  public int getId() {
    return id;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public void setMyBoard(MyBoard board) {
    startBoard = board;
  }

  void setStartTime(Instant startTime) {
    this.startTime = startTime;
  }

  public List<MoveEntity> getMoves() {
    return moves;
  }

  public void addMove(MoveEntity move) {
    moves.add(move);
  }

  public MoveEntity getLastMove() {
    return moves.get(moves.size() - 1);
  }

  public Duration getClockDuration() {
    return clockDuration;
  }

  void setClockDuration(Duration clockDuration) {
    this.clockDuration = clockDuration;
  }

  public Duration getClockIncrement() {
    return clockIncrement;
  }

  private Duration getGameDuration() {
    return moves.stream().map(MoveEntity::getDuration).reduce(Duration.ZERO, Duration::plus);
  }

  public Side getStartSide() {
    return startBoard.getSideToMove();
  }

  public MyBoard getStartBoard() {
    return startBoard;
  }

  public Duration getCurrentMoveDuration() {
    final Instant now = Instant.now();
    final Duration gameDuration = getGameDuration();
    final Instant lastMove = startTime.plus(gameDuration);
    return Duration.between(lastMove, now);
  }

  public Duration getRemainingTime(Side side, boolean withCurrentTurn) {
    GameState gameState = getGameState();
    PlayerState playerState = gameState.getPlayerState(side);
    if (withCurrentTurn) {
      return playerState.getRemainingTimeAt(Instant.now());
    }
    return playerState.getRemainingTime();
  }

  public Duration getRemainingTime(Side side) {
    return getRemainingTime(side, false);
  }

  private Duration computeRemainingTime(Side side) {
    final IntPredicate turnOf = i -> i % 2 == (side == Side.WHITE ? 0 : 1);
    Duration timeLeft = clockDuration;
    final List<Duration> moveDurations =
        moves.stream().map(MoveEntity::getDuration).collect(Collectors.toList());
    final List<Duration> blackMoveDurations = IntStream.range(0, moveDurations.size())
        .filter(turnOf).mapToObj(moveDurations::get).collect(Collectors.toList());
    final Duration playedDuration =
        blackMoveDurations.stream().reduce(Duration.ZERO, Duration::plus);
    timeLeft = timeLeft.minus(playedDuration);
    final Duration incrementDuration = clockIncrement.multipliedBy(blackMoveDurations.size());
    timeLeft = timeLeft.plus(incrementDuration);
    return timeLeft;
  }

  public GameState getGameState() {
    final MyBoard board;
    try {
      board = startBoard.doMoves(moves);
    } catch (MoveException e) {
      // this exception can't happen here since the moves were already validated upon
      // insertion
      throw new AssertionError("moves should have been validated");
    }
    final Duration gameDuration = getGameDuration();
    final Instant whiteTimeAtTurnStart = startTime.plus(gameDuration);
    final Duration whiteRemainingTime = computeRemainingTime(Side.WHITE);
    final PlayerState whitePlayer =
        PlayerState.of(Side.WHITE, whiteTimeAtTurnStart, whiteRemainingTime);
    final Instant blackTimeAtTurnStart = startTime.plus(gameDuration);
    final Duration blackRemainingTime = computeRemainingTime(Side.BLACK);
    final PlayerState blackPlayer =
        PlayerState.of(Side.BLACK, blackTimeAtTurnStart, blackRemainingTime);
    return GameState.of(board, whitePlayer, blackPlayer);
  }

  public ImmutableList<PlayerState> getPlayerStates() {
    final List<PlayerState> playerStates = new ArrayList<>();
    Instant time = startTime;
    Duration[] remainingTimes = new Duration[] {clockDuration, clockDuration};
    for (int i = 0; i < moves.size(); i++) {
      final MoveEntity move = moves.get(i);
      final int side = i % 2;
      playerStates
          .add(PlayerState.of(side == 0 ? Side.WHITE : Side.BLACK, time, remainingTimes[side]));
      remainingTimes[side] = remainingTimes[side].minus(move.getDuration());
      remainingTimes[side] = remainingTimes[side].plus(clockIncrement);
      time = time.plus(move.getDuration());
    }
    return ImmutableList.copyOf(playerStates);
  }
}
