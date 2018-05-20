package qirkat;

import java.util.HashMap;
import java.util.ArrayList;


import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author BHUMIKA GOEL
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        if (move ==  null) {
            throw new IndexOutOfBoundsException("wtffffff");
        }
        game().reportMove("%s moves %s.",
                myColor().toString(), move.toString());
        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        Move best;
        best = null;
        int v;

        if (board.isGameOver()) {
            if (sense == 1) {
                return WINNING_VALUE;
            }
            return WINNING_VALUE * -1;
        }

        if (depth <= 0) {
            return staticScore(board);
        }

        ArrayList<Move> potentialMoves = new ArrayList<>();
        potentialMoves = board.getMoves();
        int prev;
        if (sense == 1) {
            v = INFTY * -1;
            for (Move mov: potentialMoves) {
                Board child = new Board(board);
                child.makeFakeMove(mov);
                prev = v;
                v = Math.max(v, findMove(child, depth - 1,
                        false, -1, alpha, beta));
                alpha = Math.max(alpha, v);
                if (best == null || prev != v) {
                    best = mov;
                    prev = v;
                }
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            v = INFTY;
            for (Move mov: potentialMoves) {
                Board child = new Board(board);
                child.makeFakeMove(mov);
                prev = v;
                v = Math.min(v, findMove(child, depth - 1,
                        false, 1, alpha, beta));
                beta = Math.min(beta, v);
                if (best == null || prev != v) {
                    best = mov;
                    prev = v;
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }

        return v;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int whiteScore = 0;
        int blackScore = 0;
        int heuristicVal = 0;
        for (char r = '1'; r < '6'; r++) {
            for (char c = 'a'; c < 'f'; c++) {
                if (board.get(c, r) == PieceColor.WHITE) {
                    whiteScore += 6 - (int) r;
                } else if (board.get(c, r) == PieceColor.BLACK) {
                    blackScore += (int) r;
                } else {
                    continue;
                }
            }
        }
        heuristicVal = whiteScore - blackScore;
        return heuristicVal;
    }

    /**MEMO is a hashmap to potentially store values.*/
    private static HashMap<Board, Integer> memo = new HashMap<>();

}
