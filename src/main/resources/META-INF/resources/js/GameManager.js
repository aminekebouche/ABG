class GameManager {
    CBM;

    /**
     * Initialises a GameManager with the players
     */
    constructor() {
        this.TMWhite = new TimeManager("White");
        this.TMBlack = new TimeManager("Black");

        this.inputIncrement = 0;
        this.inputDuration = 0;
        this.gameId = -1;
        this.intervalID = -1;
    }

    /**
     * Initialises a ChessBoard and ChessBoardManager
     */
    initialiseChessBoard() {
        this.CBM = new ChessBoardManager();
        const CBM = this.CBM;
        const config = {
            draggable: true,
            dropOffBoard: 'snapback',
            onDragStart: function(src, piece, pos, o) {
                CBM.onDragStart(src, piece, pos, o);
            },
            onDrop: function(source, target, piece) {
                CBM.onDrop(source, target, piece);
            },
            onSnapEnd: function() {
                CBM.onSnapEnd();
            },
        };
        let board = ChessBoard('board', config);
        this.CBM.setBoard(board);
    }

    /**
     * Sends the duration and increment wanted (0 if none entered) to the database and loads a game with the gameId returned by database
     */
    newGame() {
        //this.disableInputDurationIncrement();
        const duration = document.getElementById("duration").value;
        const increment = document.getElementById("increment").value;

        if (!this.validateDuration(duration) || !this.validateIncrement(increment)) {
            return;
        }

        this.inputDuration = Number(duration);
        this.inputIncrement = increment.trim() === '' ? 10 : Number(increment.trim()); //default increment is 10seconds

        const objSent = {
            duration: this.inputDuration || 0,
            increment: this.inputIncrement || 0
        };

        let gameId;
        database.post('/v0/api/v1/game/chess/new', objSent, '', (id) => {
            return gameId = id;
        });
        this.loadGame(gameId);
    }

    /**
     * Gets the moves for the gameId in argument or in input on the page; play the moves returned by database; manage time display
     * @param gameId -
     */
    loadGame(gameId) {
        // if no gameId is given, get it from user's input
        if (gameId === undefined) {
            gameId = $('#game-id').val();
            // if user didn't provid a game id, alter him
            if (!gameId) {
                alert('Please provide a game Id');
                return;
            }
        }

        $('#game-id-display').html(gameId);

        this.gameId = gameId;
        this.CBM.updateGameId(gameId);
        this.TMWhite.updateGameId(gameId);
        this.TMBlack.updateGameId(gameId);

        this.getAndPlayMoves();

        this.resetDurationIncrement();

        this.manageTimePlayer();
    }

	

    /**
     * Get moves from the database and plays them on the board
     */
    getAndPlayMoves() {
        const url = '/v0/api/v1/game/' + this.gameId + '/moves';
        let moves;
        database.get(url, (data) => {
        	this.CBM.loadMoves(data);
			console.log(data);
            return moves = data;
        });
		return moves;
    }

    /**
     * Gets remaining time for each player and displays it every second.
     * Stops the game when time has run out
     */
    manageTimePlayer() {
        clearInterval(this.intervalID);

        this.TMWhite.getRemainingTime();
        this.TMBlack.getRemainingTime();
        if (this.TMWhite.hasTimeLeft() && this.TMBlack.hasTimeLeft()) {
            this.CBM.setTimeOut(false);
        }

        let precedent_turn = this.CBM.getTurn();

        this.intervalID = window.setInterval(() => {
            if (!this.TMWhite.hasTimeLeft() || !this.TMBlack.hasTimeLeft()) {
                clearInterval(this.intervalID);
                this.CBM.setTimeOut(true);
                return;
            }

            const player = this.CBM.getTurn();
            if (player === "w") {
                if (precedent_turn === "b") {
                    this.TMBlack.updateTime(this.inputIncrement);
                    this.TMBlack.displayTime();
                    precedent_turn = "w";
                }
                else {
                    this.TMWhite.updateTime(-1);
                }
                this.TMWhite.displayTime();
            }
            else if (player === "b") {
                if (precedent_turn === "w") {
                    this.TMWhite.updateTime(this.inputIncrement);
                    this.TMWhite.displayTime();
                    precedent_turn = "b";
                }
                else {
                    this.TMBlack.updateTime(-1);
                }
                this.TMBlack.displayTime();
            }
            else {
                clearInterval(this.intervalID);
            }

        }, 1000);
    }

    /**
     * Resets duration and increment html inputs and values in object
     */
    resetDurationIncrement() {
        document.getElementById("duration").value = "";
        document.getElementById("increment").value = "";
        this.inputDuration = 0;
    }


    /**
     * Checks if duration is between 600 and 36 000 seconds
     * @param duration - duration input value
     * @returns {boolean} - true if the duration correct or default, false otherwise
     */
    validateDuration(duration) {
        //default option
        if (duration.trim() === '') {
            return true;
        }
        if (duration < 600) {
            alert("Minimum game duration time is 600 seconds (10 minutes)!");
            return false;
        }
        if (duration > 36000) {
            alert("Maximum game duration time is 36000 seconds (10 hours)!");
            return false;
        }
        return true;
    }

    /**
     * Checks if increment is valid (between 0 and 60 seconds)
     * @param increment - increment input value
     * @returns {boolean} - true if the increment is valid (no value is a valid increment), false otherwise
     */
    validateIncrement(increment) {
        if (increment.trim() !== '' && increment < 0) {
            alert("Minimum increment value is 0 seconds (no increment)!");
            return false;
        }
        if (increment.trim() !== '' && increment > 60) {
            alert("Maximum increment value is 60 seconds!");
            return false;
        }
        return true;
    }
}

