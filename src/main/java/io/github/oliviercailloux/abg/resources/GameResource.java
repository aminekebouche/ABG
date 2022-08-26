package io.github.oliviercailloux.abg.resources;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveException;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import io.github.oliviercailloux.abg.model.CheckerBoard;
import io.github.oliviercailloux.abg.model.ChessBoard;
import io.github.oliviercailloux.abg.model.GameEntity;
import io.github.oliviercailloux.abg.model.MoveDAO;
import io.github.oliviercailloux.abg.model.MoveEntity;
import io.github.oliviercailloux.abg.model.MoveLittlePiece;
import io.github.oliviercailloux.abg.model.MyBoard;
import io.github.oliviercailloux.abg.model.state.GameState;
import io.github.oliviercailloux.abg.model.state.PlayerState;
import io.github.oliviercailloux.abg.service.ChessService;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("api/v1/game")
@RequestScoped
public class GameResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameResource.class);
  @Inject
  ChessService chessService;

  @POST
  @Path("{typeGame}/new")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_PLAIN)
  public int createGame(@FormParam("duration") int duration, @FormParam("increment") int increment,
      @PathParam("typeGame") String typeGame) {
    LOGGER.info("POST game/{typeGame}/new " + duration + increment);
    GameEntity game;
    if (typeGame.equals("chess")) {
      game = GameEntity.cerateNewGameWithChess();
    } else {
      game = GameEntity.cerateNewGameWithChecker();
    }

    Duration clockIncrement = game.getClockIncrement();
    if (increment != 0)
      clockIncrement = Duration.ofSeconds(increment);
    if (duration != 0) {
      MyBoard board =
          typeGame.equals("chess") ? ChessBoard.createChessBoard() : CheckerBoard.initialBoard();
      game = new GameEntity(
          GameState.of(board, PlayerState.of(Side.WHITE), PlayerState.of(Side.BLACK)),
          Instant.now(), Duration.ofSeconds(duration), clockIncrement);
    }
    chessService.persist(game);
    return game.getId();
  }

  @GET
  @Path("{gameId}")
  @Produces(MediaType.TEXT_PLAIN)
  public String getGame(@PathParam("gameId") int gameId) throws MoveException {
    LOGGER.info("GET game/{}", gameId);
    final GameEntity game = chessService.getGame(gameId);
    final List<MoveEntity> moves = game.getMoves();
    final MyBoard board = game.getStartBoard().doMoves(moves);
    return board.getFen();
  }

  @GET
  @Path("{gameId}/moves")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Move> getMoves(@PathParam("gameId") int gameId) {
    LOGGER.info("GET game/{}/moves", gameId);
    LOGGER.info("Avoir les moves" + chessService.getGame(gameId).getMoves());
    return chessService.getGame(gameId).getMoves().stream().map(MoveEntity::asMove)
        .collect(Collectors.toList());
  }

  @POST
  @Path("{gameId}/move")
  @Consumes(MediaType.APPLICATION_JSON)
  public void addMove(@PathParam("gameId") int gameId, MoveLittlePiece movel) {
    MoveDAO move = MoveDAO.createMoveDAO(movel.getFrom(),movel.getTo(),movel.getPiece());
    LOGGER.info("POST game/{}/move", gameId);
    LOGGER.info("Coucou{}", move.getPromotion());
    final GameEntity game = chessService.getGame(gameId);
    final Duration duration = game.getCurrentMoveDuration();
    final MoveEntity moveEntity = MoveEntity.createMoveEntity(game, move, duration);
    game.addMove(moveEntity);
    chessService.persist(moveEntity);
  }

  @GET
  @Path("{gameId}/clock/{side}")
  public Duration getPlayerRemainingTime(@PathParam("gameId") int gameId,
      @PathParam("side") String side) {
    LOGGER.info("GET game/{}/clock/{}", gameId, side);
    GameEntity game = chessService.getGame(gameId);
    LOGGER.info("1 apres gameEntity");
    GameState gameState = game.getGameState();
    LOGGER.info("2 apres gameState");
    Side enSide = side.equals("black") ? Side.BLACK : Side.WHITE;
    PlayerState playerState = gameState.getPlayerState(enSide);
    LOGGER.info("3 apres playerState");
    if (gameState.isSideToMove(enSide)) {
      return playerState.getRemainingTimeAt(Instant.now());
    }
    return playerState.getRemainingTime();
  }
}
