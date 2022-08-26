class ChessBoardManager {
    game;
    /**
     * Initialize a ChessBoardManager with an empty board
     */
    constructor() {
        this.gameId = -1;
        this.timeOut = false;
    }

    /**
     * Sets a ChessBoard as board, once it has been initialised
     * @param board
     */
    setBoard(board) {
        this.board = board;
    }

    /**
     * Gets the turn
     * @returns {*} - "w" for white, "b" for black
     */
    getTurn() {
        return this.game.turn();
    }

    /**
     * Preventing a player from picking up a piece if the game is over or it is not the color's turn
     * @param src - source
     * @param piece - piece
     * @param pos - position
     * @param o - orientation
     * @returns {boolean} - if false, the piece cannot be dragged
     */
    onDragStart(src, piece, pos, o) {
        if (this.game.game_over() ||
            (this.game.turn() === 'w' && piece.search(/^b/) !== -1) ||
            (this.game.turn() === 'b' && piece.search(/^w/) !== -1) ||
            this.timeOut) {
			console.log("onDragStart" + piece);
            return false;
        }
		console.log("onDragStart1" + piece);
    };

    /**
     * Prevents the drop if the move is not valid or time is out, else saves the move in the database and updates the board
     * @param src - original position of the piece
     * @param dst - destination position of the piece
	   @param piece

     */
    onDrop(source, target, piece) {
        if (this.timeOut) {
            return;
        }

        let move = this.game.move({
            from: source,
            to: target,
            promotion: 'q' //todo: get right promotion; auto promote to queen for now...
        });

		let movee = {
            from: source,
            to: target,
            promotion: piece
		};
        // a move is valid iff it isn't returned as null
        if (move === null) {
            return 'snapback'; // snapback the piece to its previous location
        }

        // otherwise do play the move on the server
        this.playMove(movee);
        this.updateTurn();
    };

    /**
     * updates the position of a piece
     */
    onSnapEnd() {
        this.board.position(this.game.fen());
    };

    /**
     * Checks status of the game (checkmate, draw or continue normally) and switches the turn of players
     */
    updateTurn() {
        let status = '';
        let player = this.game.turn() === 'w' ? 'White' : 'Black';

        // checkmate?
        if (this.game.in_checkmate()) {
            status = 'Game over, ' + player + ' is in checkmate.';
        } else if (this.game.in_draw()) {
            // draw?
            status = 'Game over, drawn position';
        } else {
            // game still on
            status = player + ' to move';

            // check?
            if (this.game.in_check()) {
                status += ', ' + player + ' is in check';
            }
        }

        // update status & pgn spans
        $('#status').html(status);
        $('#fen').html(this.game.fen());
        $('#pgn').html(this.game.pgn());
    };

    /**
     * Updates the gameId for the board (necessary for post request)
     * @param gameId - integer for gameId
     */
    updateGameId(gameId) {
        this.gameId = gameId;
    }

    /**
     * Updates the move in the database and plays it on the board
     * @param move - Object representing a move
     * @throws error when game id is not entered yet (cannot sent to database)
     */
    playMove(move) {
	console.log(move);
        if (this.gameId < 0) {
            return console.error('Invalid game ID');
        }

        const url = '/v0/api/v1/game/' + this.gameId + '/move';
        const objSent = JSON.stringify({
            from: move.from.toUpperCase(),
            to: move.to.toUpperCase(),
            promotion: move.promotion.toUpperCase() || "NONE", // just to avoid passing null
        })

        let fen;
        database.post(url, objSent, 'json', (data) => {
            fen = data;
        })
		console.log(fen);
        this.board.position(fen);

        this.updateTurn();
    };

    /**
     * Resets the board and load all moves in argument
     * @param moves - array of moves
     */
    loadMoves(moves) {
        this.game = Chess();
        for(let i in moves) {
            let move = moves[i];
            this.game.move({
                from: move.from.toLowerCase(),
                to: move.to.toLowerCase(),
            });
        }
        this.board.position(this.game.fen());
        this.updateTurn();
    };

    /**
     * Sets the value of timeOut
     * @param val - boolean, set to true if there is no time left, false otherwise
     */
    setTimeOut(val) {
        this.timeOut = val;
    }

}