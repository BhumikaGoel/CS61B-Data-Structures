package qirkat;

/* Author: P. N. Hilfinger */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

import static qirkat.PieceColor.*;
import static qirkat.Game.State.*;
import static qirkat.Command.Type.*;
import static qirkat.GameException.error;

/** Controls the play of the game.
 *  @author BHUMIKA GOEL
 */
class Game {

    /** States of play. */
    static enum State {
        SETUP, PLAYING;
    }

    /** A new Game, using BOARD to play on, reading initially from
     *  BASESOURCE and using REPORTER for error and informational messages. */
    Game(Board board, CommandSource baseSource, Reporter reporter) {
        _inputs.addSource(baseSource);
        _board = board;
        _constBoard = _board.constantView();
        _reporter = reporter;
    }

    /** Run a session of Qirkat gaming. */
    void process() {
        Player white, black;

        white = black = null;
        doClear(null);


        while (true) {
            _whiteIsManual = true;
            _blackIsManual = false;
            while (_state == SETUP) {
                doCommand();
            }

            if (!_whiteIsManual) {
                white = new AI(this, WHITE);
            } else {
                white = new Manual(this, WHITE);
            }

            if (_blackIsManual) {
                black = new Manual(this, BLACK);
            } else {
                black = new AI(this, BLACK);
            }

            while (_state != SETUP && !_board.gameOver()) {
                Move move;
                move = null;

                if (board().whoseMove() == PieceColor.WHITE) {
                    move = white.myMove();
                } else {
                    move = black.myMove();
                }

                if (_state == PLAYING) {
                    _board.makeMove(move);
                    _constBoard = _board.constantView();
                    _board.isGameOver();
                }
            }

            if (_state == PLAYING) {
                reportWinner();
            }

            _state = SETUP;
        }

    }

    /** Return a read-only view of my game board. */
    Board board() {
        return _constBoard;
    }

    /** Perform the next command from our input source. */
    void doCommand() {
        try {
            Command cmnd =
                Command.parseCommand(_inputs.getLine("qirkat: "));
            _commands.get(cmnd.commandType()).accept(cmnd.operands());
        } catch (GameException excp) {
            _reporter.errMsg(excp.getMessage());
        }
    }

    /** Read and execute commands until encountering a move or until
     *  the game leaves playing state due to one of the commands. Return
     *  the terminating move command, or null if the game first drops out
     *  of playing mode. If appropriate to the current input source, use
     *  PROMPT to prompt for input. */
    Command getMoveCmnd(String prompt) {
        while (_state == PLAYING) {
            try {
                Command cmnd = Command.parseCommand(_inputs.getLine(prompt));
                switch (cmnd.commandType()) {
                case PIECEMOVE:
                    return cmnd;
                default:
                    _commands.get(cmnd.commandType()).accept(cmnd.operands());
                }
            } catch (GameException excp) {
                _reporter.errMsg(excp.getMessage());
            }
        }
        return null;
    }

    /** Return random integer between 0 (inclusive) and MAX>0 (exclusive). */
    int nextRandom(int max) {
        return _randoms.nextInt(max);
    }

    /** Report a move, using a message formed from FORMAT and ARGS as
     *  for String.format. */
    void reportMove(String format, Object... args) {
        _reporter.moveMsg(format, args);
    }

    /** Report an error, using a message formed from FORMAT and ARGS as
     *  for String.format. */
    void reportError(String format, Object... args) {
        _reporter.errMsg(format, args);
    }

    /* Command Processors */

    /** Perform the command 'auto OPERANDS[0]'. */
    void doAuto(String[] operands) {
        _state = SETUP;
        String temp = operands[0].toLowerCase();
        switch (temp) {
        case "white":
            _whiteIsManual = false;
            break;
        case "black":
            _blackIsManual = false;
            break;
        default:
            break;
        }

    }

    /** Perform a 'help' command. */
    void doHelp(String[] unused) {
        InputStream helpIn =
            Game.class.getClassLoader().getResourceAsStream("qirkat/help.txt");
        if (helpIn == null) {
            System.err.println("No help available.");
        } else {
            try {
                BufferedReader r
                    = new BufferedReader(new InputStreamReader(helpIn));
                while (true) {
                    String line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(line);
                }
                r.close();
            } catch (IOException e) {
                /* Ignore IOException */
            }
        }
    }

    /** Perform the command 'load OPERANDS[0]'. */
    void doLoad(String[] operands) {
        try {
            FileReader reader = new FileReader(operands[0]);
            _inputs.addSource(new ReaderSource(reader, false));
        } catch (IOException e) {
            throw error("Cannot open file %s", operands[0]);
        }
    }

    /** Perform the command 'manual OPERANDS[0]'. */
    void doManual(String[] operands) {
        _state = SETUP;
        String temp = operands[0].toLowerCase();
        switch (temp) {
        case "black":
            _blackIsManual = true;
            break;
        case "white":
            _whiteIsManual = true;
            break;
        default:
            break;
        }
    }

    /** Exit the program. */
    void doQuit(String[] unused) {
        Main.reportTotalTimes();
        System.exit(0);
    }

    /** Perform the command 'start'. */
    void doStart(String[] unused) {
        _state = PLAYING;
    }

    /** Perform the move OPERANDS[0]. */
    void doMove(String[] operands) {
        Move temp = Move.parseMove(operands[0]);
        if (_board.legalMove(temp)) {
            _board.makeMove(temp);
        } else {
            _reporter.errMsg("illegal move");
        }
    }

    /** Perform the command 'clear'. */
    void doClear(String[] unused) {
        _board.clear();
        _state = SETUP;
        _whiteIsManual = true;
        _blackIsManual = false;
    }

    /** Perform the command 'set OPERANDS[0] OPERANDS[1]'. */
    void doSet(String[] operands) {
        switch (operands[0].charAt(0)) {
        case 'b':
            _board.setPieces(operands[1], PieceColor.BLACK);
            break;
        case 'w':
            _board.setPieces(operands[1], PieceColor.WHITE);
            break;
        default:
            _board.setPieces(operands[1], PieceColor.EMPTY);
            break;
        }
        if (!operands[0].equals(PieceColor.WHITE)
                && !operands[0].equals(PieceColor.BLACK)) {
            doError(new String[] {});
        }
        _state = SETUP;

    }

    /** Perform the command 'dump'. */
    void doDump(String[] unused) {
        System.out.println("===");
        System.out.println(_board.toString());
        System.out.println("===");
    }

    /** Execute 'seed OPERANDS[0]' command, where the operand is a string
     *  of decimal digits. Silently substitutes another value if
     *  too large. */
    void doSeed(String[] operands) {
        try {
            _randoms.setSeed(Long.parseLong(operands[0]));
        } catch (NumberFormatException e) {
            _randoms.setSeed(Long.MAX_VALUE);
        }
    }

    /** Execute the artificial 'error' command. */
    void doError(String[] unused) {
        throw error("Command not understood");
    }

    /** Report the outcome of the current game. */
    void reportWinner() {
        /**When either player enters a winning move,
         * the program should print a line saying either White wins.
         * or Black wins. (both with a period) as appropriate.
         * Use exactly those phrases, alone on their line. At that point,
         * the program goes back into set-up state (maintaining the final
         * state of the board so that the user may examine it). Don't use
         * these phrases in any other situation.*/
        String win = "";
        win = _board.whoseMove().opposite().toString();
        String msg;
        msg = win + " wins.";
        _reporter.outcomeMsg(msg);
        _state = SETUP;
    }

    /**returns State of the game.*/
    public State state() {
        return _state;
    }

    /**returns board of the game.*/
    public Board readboard() {
        return _board;
    }

    /** Mapping of command types to methods that process them. */
    private final HashMap<Command.Type, Consumer<String[]>> _commands =
        new HashMap<>();

    {
        _commands.put(AUTO, this::doAuto);
        _commands.put(CLEAR, this::doClear);
        _commands.put(DUMP, this::doDump);
        _commands.put(HELP, this::doHelp);
        _commands.put(MANUAL, this::doManual);
        _commands.put(PIECEMOVE, this::doMove);
        _commands.put(SEED, this::doSeed);
        _commands.put(SETBOARD, this::doSet);
        _commands.put(START, this::doStart);
        _commands.put(LOAD, this::doLoad);
        _commands.put(QUIT, this::doQuit);
        _commands.put(ERROR, this::doError);
        _commands.put(EOF, this::doQuit);
    }

    /** Input source. */
    private final CommandSources _inputs = new CommandSources();

    /** My board and its read-only view. */
    private Board _board, _constBoard;
    /** Indicate which players are manual players (as opposed to AIs). */
    private boolean _whiteIsManual, _blackIsManual;
    /** Current game state. */
    private State _state;
    /** Used to send messages to the user. */
    private Reporter _reporter;
    /** Source of pseudo-random numbers (used by AIs). */
    private Random _randoms = new Random();
}
