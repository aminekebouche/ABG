package io.github.oliviercailloux.abg.model;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.google.common.base.Preconditions;
import java.time.Duration;
import java.util.Objects;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/***
 * 
 * @author Delmas Douo Bougna
 * @author Theophile Dano
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Move.find", query = "SELECT c FROM MoveEntity c WHERE c.id = :id"),
    @NamedQuery(name = "Move.getLastMoveId",
        query = "SELECT MAX(c.id) FROM MoveEntity c WHERE c.game.id = :id"),
    @NamedQuery(name = "Move.getLastMove", query = "SELECT MAX(c.id) FROM MoveEntity c ")})
@JsonbPropertyOrder({"from", "to", "promotion", "game"})
public class MoveEntity {

  @Id
  @GeneratedValue
  int id;
  Duration duration;
  Square from;
  Square to;
  Piece promotion;
  @ManyToOne
  GameEntity game;

  public MoveEntity() {
    from = Square.NONE;
    to = Square.NONE;
    promotion = Piece.NONE;
  }

  MoveEntity(GameEntity gameEntity, Square from, Square to, Piece promotion, Duration duration) {
    this.game = Objects.requireNonNull(gameEntity);
    this.duration = duration == null ? Duration.ZERO : duration;
    this.from = from;
    this.to = to;
    this.promotion = promotion == null ? Piece.NONE : promotion;
  }

  // public static MoveEntity createMoveEntity(GameEntity game, MoveDAO move) {
  // Preconditions.checkArgument(game != null);
  // Preconditions.checkArgument(move != null);
  // return new MoveEntity(game, move.getFrom(), move.getTo(), move.getPromotion(), Duration.ZERO);
  // }
  //
  public static MoveEntity createMoveEntity(GameEntity game, MoveDAO move, Duration duration) {
    Preconditions.checkArgument(game != null);
    Preconditions.checkArgument(move != null);
    Preconditions.checkArgument(duration != null);
    return new MoveEntity(game, move.getFrom(), move.getTo(), move.getPromotion(), duration);
  }

  public static MoveEntity createMoveEntity(GameEntity game, Move move) {
    Preconditions.checkArgument(game != null);
    Preconditions.checkArgument(move != null);
    return new MoveEntity(game, move.getFrom(), move.getTo(), move.getPromotion(), Duration.ZERO);
  }

  public static MoveEntity createMoveEntity(GameEntity game, Move move, Duration duration) {
    Preconditions.checkArgument(game != null);
    Preconditions.checkArgument(move != null);
    Preconditions.checkArgument(duration != null);
    return new MoveEntity(game, move.getFrom(), move.getTo(), move.getPromotion(), duration);
  }

  public int getId() {
    return id;
  }

  public Square getFrom() {
    return from;
  }

  public Square getTo() {
    return to;
  }

  public Piece getPromotion() {
    return promotion;
  }

  public GameEntity getGame() {
    return game;
  }

  public Duration getDuration() {
    return duration;
  }

  public Move asMove() {
    return new Move(from, to, promotion);
  }
}
