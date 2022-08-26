package io.github.oliviercailloux.abg.resources;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveException;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import io.github.oliviercailloux.abg.model.ChessBoard;
import io.github.oliviercailloux.abg.model.GameEntity;
import io.github.oliviercailloux.abg.model.MoveEntity;
import io.github.oliviercailloux.abg.service.ChessService;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 
 * @author Megan Brassard
 *
 */
@Path("api/v1/help")
@RequestScoped
public class HelpResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpResource.class);
    @Inject
    ChessService chessService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String suggestMove(@QueryParam("gid") int gameId)
	    throws MoveGeneratorException, MoveException {
	LOGGER.info("GET\t/help\tgid={}", gameId);
	final GameEntity game = chessService.getGame(gameId);
	final List<MoveEntity> moves = game.getMoves();
	final ChessBoard chessBoard = (ChessBoard) ChessBoard.STARTING_CHESS_BOARD.doMoves(moves);
	// Generate possible moves
	final MoveList moveList = MoveGenerator.generateLegalMoves(chessBoard.asBoard());
	final MoveList mated = new MoveList();
	final MoveList staleMate = new MoveList();
	final Board board = chessBoard.asBoard();
	for (Move move : moveList) {
	    board.doMove(move);
	    if (board.isMated()) {
		mated.add(move);
		moveList.remove(move);
	    }
	    if (board.isStaleMate()) {
		staleMate.add(move);
		moveList.remove(move);
	    }
	    board.undoMove();
	}
	return "Move to mate opponent : " + mated.toString() + "\n" + "Move leading to stalemate : "
	+ staleMate.toString() + "\n" + "Other legal move : " + moveList.toString();
    }
}