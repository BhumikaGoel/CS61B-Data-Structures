package qirkat;

import java.util.Observer;
import java.util.Observable;
import java.util.Stack;
import java.util.Arrays;
import java.util.Formatter;
import java.util.ArrayList;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author BHUMIKA GOEL
 */
class Board extends Observable {

    /**Size of the game board.*/
    static final int BOARDSIZE = 25;

    /** A new, cleared board at the start of the game. */
    Board() {
        /**my data structure for the board is a linearized index array*/
        _board = new PieceColor[BOARDSIZE];
        _horizontalBoard = new String[BOARDSIZE];
        _undoBoard = new Stack<>();
        clear();
    }

    /** Return size of the board. */
    private int boardSize() {
        return _board.length;
    }


    /** A copy of B. */
    Board(Board b) {
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;

        String str = "wwwww wwwww bb-ww bbbbb bbbbb";
        setPieces(str, _whoseMove);

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        PieceColor[] pieces = new PieceColor[BOARDSIZE];
        for (int k = 0; k < BOARDSIZE; k++) {
            pieces[k] = b.get(k);
        }
        this._board = pieces;

        String[] horiz = new String[BOARDSIZE];
        for (int k = 0; k < BOARDSIZE; k++) {
            horiz[k] = b.getHorizontal(k);
        }
        this._horizontalBoard = horiz;
        _whoseMove = b.whoseMove();
        _gameOver = b.gameOver();

    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        _whoseMove = nextMove;

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }

        _horizontalBoard = new String[BOARDSIZE];
        _undoBoard.push(new Board(this));


        if (isGameOver()) {
            _gameOver = true;
        }
        setChanged();
        notifyObservers();
    }

    /**Return true if any piece of given piececolor
     * NEXTMOVE is still on the board.*/
    boolean anyPiecesLeft(PieceColor nextMove) {
        for (int i = 0; i < BOARDSIZE; i++) {
            if (_board[i].equals(nextMove)) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'.  */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        return _board[k];
    }

    /** Return the horizontal state of the square at linearized index K. */
    String getHorizontal(int k) {
        assert validSquare(k);
        return _horizontalBoard[k];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _board[k] = v;
    }

    /** Set getHorizontal(K) to V.K is the linearized index of a square. */
    private void setHorizontal(int k, String v) {
        assert validSquare(k);
        _horizontalBoard[k] = v;
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {
        if (mov == null || mov.isVestigial()) {
            return false;
        }
        if (get(mov.fromIndex()) != whoseMove()) {
            return false;
        }
        int diff = Math.abs(mov.fromIndex() - mov.toIndex());
        if (diff == 4 || diff == 6 || diff == 8 || diff == 12) {
            if (mov.fromIndex() % 2 == 1) {
                return false;
            }
        }
        if (mov.isJump()) {
            if (diff != 2 && diff != 10 && diff != 8 && diff != 12) {
                return false;
            }
            if ((mov.col1() < mov.col0() && mov.col0() == 'a')
                    || (mov.col1() < mov.col0() && mov.col0() == 'b')) {
                return false;
            }
            if ((mov.col1() > mov.col0() && mov.col0() == 'd')
                    || (mov.col1() > mov.col0() && mov.col0() == 'e')) {
                return false;
            }
            if (checkJump(mov, true)) {
                Board temp = new Board(this);
                temp.makeFakeMove(mov);
                Move tempMove = mov;
                while (tempMove.jumpTail() != null) {
                    tempMove = tempMove.jumpTail();
                }
                int k = index(tempMove.col1(), tempMove.row1());
                if (temp.jumpPossible(k)) {
                    return false;
                }
                return true;
            }
            return false;
        } else {
            if (!nonCapLegalMove(mov)) {
                return false;
            }
        }
        return true;
    }

    /**Returns if MOV is legal given that it is non-capturing.*/
    boolean nonCapLegalMove(Move mov) {
        int diff = Math.abs(mov.fromIndex() - mov.toIndex());
        if (jumpPossible()) {
            return false;
        }
        if ((mov.isLeftMove() && mov.col0() == 'a')
                || (mov.isRightMove() && mov.col0() == 'e')) {
            return false;
        }
        if (diff != 1 && diff != 6 && diff != 4 && diff != 5) {
            return false;
        }
        if ((mov.jumpTail() != null) || (isBackWards(mov))
                || (isBadHorizontal(mov))) {
            return false;
        }
        if (whoseMove() == PieceColor.WHITE
                && BOARDSIZE - 5 <= mov.fromIndex()
                && mov.fromIndex() <= BOARDSIZE - 1) {
            return false;
        }
        if (whoseMove() == PieceColor.BLACK
                && 0 <= mov.fromIndex()
                && mov.fromIndex() <= 4) {
            return false;
        }
        return true;

    }

    /** Returns boolean if MOV is disallowed backwards. */
    boolean isBackWards(Move mov) {

        char srcRow = mov.row0();
        char destRow = mov.row1();
        if (whoseMove() == WHITE && srcRow - destRow > 0) {
            return true;
        }
        if (whoseMove() == BLACK && srcRow - destRow < 0) {
            return true;
        }
        return false;
    }

    /** Returns boolean if MOV is disallowed horizontally. */
    boolean isBadHorizontal(Move mov) {

        char srcCol = mov.col0();
        char srcRow = mov.row0();
        char destCol = mov.col1();
        char destRow = mov.row1();
        String prev = getHorizontal(index(srcCol, srcRow));
        if (srcRow == destRow && prev != null) {
            if (mov.isRightMove()) {
                return prev.equals("R");
            } else {
                return prev.equals("L");
            }
        }
        return false;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }
        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getJumps(moves, k);
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    public void getMoves(ArrayList<Move> moves, int k) {
        if (get(k) == whoseMove()) {
            assert (validSquare(k));
            ArrayList<Move> temp1 = adjMoves(k);
            for (Move mov : temp1) {
                if (legalMove(mov)) {
                    moves.add(mov);
                }
            }
        }

    }

    /** Returns all non-capturing moves from the position
     *  with linearized index K to MOVES. */
    private ArrayList<Move> adjMoves(int k) {
        ArrayList<Move> temp = new ArrayList<>();
        if (validSquare(k + 5) && get(k + 5) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k + 5), row(k + 5)));
        }
        if (validSquare(k - 5) && get(k - 5) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k - 5), row(k - 5)));
        }
        if ((validSquare(k + 1)) && (col(k) != 'e')
                && get(k + 1) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k + 1), row(k + 1)));
        }
        if ((validSquare(k + 6)) && col(k) != 'e'
                && get(k + 6) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k + 6), row(k + 6)));
        }
        if ((validSquare(k - 4)) && col(k) != 'e'
                && get(k - 4) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k - 4), row(k - 4)));
        }
        if ((validSquare(k - 1)) && col(k) != 'a'
                && get(k - 1) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k - 1), row(k - 1)));
        }
        if ((validSquare(k + 4)) && col(k) != 'a'
                && get(k + 4) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k + 4), row(k + 4)));
        }
        if ((validSquare(k - 6)) && col(k) != 'a'
                && get(k - 6) == PieceColor.EMPTY) {
            temp.add(Move.move(col(k), row(k), col(k - 6), row(k - 6)));
        }

        return temp;
    }


    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    public void getJumps(ArrayList<Move> moves, int k) {
        if (get(k) == whoseMove()) {
            getJumpHelper(moves, k);
        }
    }

    /**This is a get Jump helper using MOVES and K.*/
    private void getJumpHelper(ArrayList<Move> moves, int k) {

        ArrayList<Move> potentialJumps = singleJumpMoves(k);
        ArrayList<Move> movList;
        for (Move mov: potentialJumps) {
            if (this.checkJump(mov, false)) {
                Board newBoard = new Board(this);
                newBoard.makeFakeMove(mov);
                movList = new ArrayList<>();
                newBoard.getJumpHelper(movList, mov.toIndex());
                if (movList.isEmpty()) {
                    moves.add(mov);
                } else {
                    for (Move newmov : movList) {
                        moves.add(move(mov, newmov));
                    }
                }
            }
        }
    }

    /** Returns all single jumps from the position
     *  with linearized index K to an arraylist. */
    ArrayList<Move> singleJumpMoves(int k) {
        ArrayList<Move> mov = new ArrayList<Move>();
        if (validSquare(k + 5) && validSquare(k + 10)) {
            if (checkJump(Move.move(col(k), row(k), col(k + 10),
                    row(k + 10)), false)) {
                mov.add(Move.move(col(k), row(k),
                        col(k + 10), row(k + 10)));
            }
        }
        if (validSquare(k - 5) && validSquare(k - 10)) {
            if (checkJump(Move.move(col(k), row(k), col(k - 10),
                    row(k - 10)), false)) {
                mov.add(Move.move(col(k), row(k),
                        col(k - 10), row(k - 10)));
            }
        }
        if ((validSquare(k + 1)) && (validSquare(k + 2))
                && col(k) != 'e' && col(k) != 'd') {
            if (checkJump(Move.move(col(k), row(k), col(k + 1),
                    row(k + 1)), false)) {
                mov.add(Move.move(col(k), row(k),
                        col(k + 1), row(k + 1)));
            }
        }
        if ((validSquare(k - 1)) && (validSquare(k - 2))
                && col(k) != 'a' && col(k) != 'b') {
            if (checkJump(Move.move(col(k), row(k), col(k - 2),
                    row(k - 2)), false)) {
                mov.add(Move.move(col(k), row(k),
                        col(k - 2), row(k - 2)));
            }
        }
        if (k % 2 == 0) {
            singleEvenJumpMoves(mov, k);
        }
        return mov;
    }

    /** Returns all single jumps from the position
     *  with linearized index K to an arraylist MOV if K is even. */
    void singleEvenJumpMoves(ArrayList<Move> mov, int k) {
        if (k % 2 == 0) {
            if ((validSquare(k + 6)) && (validSquare(k + 12))
                    && col(k) != 'e' && col(k) != 'd') {
                if (checkJump(Move.move(col(k), row(k), col(k + 12),
                        row(k + 12)), false)) {
                    mov.add(Move.move(col(k), row(k),
                            col(k + 12), row(k + 12)));
                }
            }
            if ((validSquare(k - 4)) && (validSquare(k - 8))
                    && col(k) != 'e' && col(k) != 'd') {
                if (checkJump(Move.move(col(k), row(k), col(k - 8),
                        row(k - 8)), false)) {
                    mov.add(Move.move(col(k), row(k),
                            col(k - 8), row(k - 8)));
                }
            }
            if ((validSquare(k + 4)) && (validSquare(k + 8))
                    && col(k) != 'a' && col(k) != 'b') {
                if (checkJump(Move.move(col(k), row(k), col(k + 8),
                        row(k + 8)), false)) {
                    mov.add(Move.move(col(k), row(k),
                            col(k + 8), row(k + 8)));
                }
            }
            if ((validSquare(k - 6)) && (validSquare(k - 12))
                    && col(k) != 'a' && col(k) != 'b') {
                if (checkJump(Move.move(col(k), row(k), col(k - 12),
                        row(k - 12)), false)) {
                    mov.add(Move.move(col(k), row(k),
                            col(k - 12), row(k - 12)));
                }
            }
        }

    }


    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go.
     *  My Addition:
     *  if allowPartial is false, you are only checking one jump,
     *  and no sequential jumps
     *  VALID JUMPS RETURNED*/
    boolean checkJump(Move mov, boolean allowPartial) {
        if (mov == null) {
            return true;
        }
        char col0 = mov.col0();
        char row0 = mov.row0();
        char colJump = mov.jumpedCol();
        char rowJump = mov.jumpedRow();
        char col1 = mov.col1();
        char row1 = mov.row1();

        int k0 = index(col0, row0);
        int k1 = index(col1, row1);

        if ((col0 == 'a' && k1 == k0 + 8) || (col0 == 'b' && k1 == k0 + 8)) {
            return false;
        }

        if ((col0 == 'a' && k1 == k0 - 12) || (col0 == 'b' && k1 == k0 - 12)) {
            return false;
        }

        if ((col0 == 'd' && k1 == k0 + 12) || (col0 == 'e' && k1 == k0 + 12)) {
            return false;
        }

        if ((col0 == 'd' && k1 == k0 - 8) || (col0 == 'e' && k1 == k0 - 8)) {
            return false;
        }

        if (get(col0, row0) != whoseMove()) {
            return false;
        }

        if (get(colJump, rowJump) != whoseMove().opposite()) {
            return false;
        }
        if (get(col1, row1) != PieceColor.EMPTY) {
            return false;
        }

        if (allowPartial) {
            return true;
        } else {

            Board simulatedBoard = new Board(this);
            simulatedBoard.makeFakeMove(move(col0, row0, col1, row1));
            return simulatedBoard.checkJump(mov.jumpTail(), allowPartial);
        }

    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        return singleJump(k);

    }

    /** Returns a boolean and add all
     * single jumps from the position
     *  with linearized index K to an arraylist. */
    boolean singleJump(int k) {
        if (validSquare(k + 5) && validSquare(k + 10)) {
            if (checkJump(Move.move(col(k), row(k), col(k + 10),
                    row(k + 10)), false)) {
                return true;
            }
        }
        if (validSquare(k - 5) && validSquare(k - 10)) {
            if (checkJump(Move.move(col(k), row(k), col(k - 10),
                    row(k - 10)), false)) {
                return true;
            }
        }
        if ((validSquare(k + 1)) && (validSquare(k + 2))
                && col(k) != 'e' && col(k) != 'd') {
            if (checkJump(Move.move(col(k), row(k), col(k + 1),
                    row(k + 1)), false)) {
                return true;
            }
        }
        if ((validSquare(k - 1)) && (validSquare(k - 2))
                && col(k) != 'a' && col(k) != 'b') {
            if (checkJump(Move.move(col(k), row(k), col(k - 2),
                    row(k - 2)), false)) {
                return true;
            }
        }
        if (k % 2 == 0) {
            if ((validSquare(k + 6)) && (validSquare(k + 12))
                    && col(k) != 'e' && col(k) != 'd') {
                if (checkJump(Move.move(col(k), row(k), col(k + 12),
                        row(k + 12)), false)) {
                    return true;
                }
            }
            if ((validSquare(k - 4)) && (validSquare(k - 8))
                    && col(k) != 'e' && col(k) != 'd') {
                if (checkJump(Move.move(col(k), row(k), col(k - 8),
                        row(k - 8)), false)) {
                    return true;
                }
            }
            if ((validSquare(k + 4)) && (validSquare(k + 8))
                    && col(k) != 'a' && col(k) != 'b') {
                if (checkJump(Move.move(col(k), row(k), col(k + 8),
                        row(k + 8)), false)) {
                    return true;
                }
            }
            if ((validSquare(k - 6)) && (validSquare(k - 12))
                    && col(k) != 'a' && col(k) != 'b') {
                if (checkJump(Move.move(col(k), row(k), col(k - 12),
                        row(k - 12)), false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1. Assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /**Makes MOV on the board without altering stack
     * and other tracking variables.*/
    void makeFakeMove(Move mov) {
        if (mov == null || mov.isVestigial()) {
            return;
        }
        makeMoveHelper(mov);
    }


    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        assert legalMove(mov);
        if (mov == null || mov.isVestigial()) {
            return;
        }
        _undoBoard.push(new Board(this));
        makeMoveHelper(mov);
        _whoseMove = whoseMove().opposite();

        isGameOver();
        setChanged();
        notifyObservers();
    }

    /**Used as a recursive helper using MOV.*/
    private void makeMoveHelper(Move mov) {

        char sourceC = mov.col0();
        char sourceR = mov.row0();
        char destC = mov.col1();
        char destR = mov.row1();


        if (mov.isJump()) {

            char jumpedC = mov.jumpedCol();
            char jumpedR = mov.jumpedRow();

            set(sourceC, sourceR, EMPTY);
            set(jumpedC, jumpedR, EMPTY);

            setHorizontal(index(sourceC, sourceR), null);
            setHorizontal(index(jumpedC, jumpedR), null);

            if (mov.jumpTail() == null) {
                set(destC, destR, whoseMove());
                setHorizontal(index(destC, destR), null);

            } else {
                makeMoveHelper(mov.jumpTail());
            }

        } else {

            set(sourceC, sourceR, EMPTY);
            set(destC, destR, whoseMove());

            setHorizontal(index(sourceC, sourceR), null);

            if (mov.isLeftMove()) {
                setHorizontal(index(destC, destR), "R");
            } else if (mov.isRightMove()) {
                setHorizontal(index(destC, destR), "L");
            }
        }

    }

    /** Undo the last move, if any. */
    void undo() {

        if (!_undoBoard.isEmpty()) {
            copy(_undoBoard.pop());
        }

        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();

        if (!legend) {
            for (int k = BOARDSIZE - 5; k >= 0; k = k - 5) {
                out.format("  ");
                for (int i = k; i < k + 5; i++) {
                    if (i != k + 4) {
                        out.format("%s ", get(i).shortName());
                    } else {
                        out.format("%s", get(i).shortName());
                    }
                }
                if (k != 0) {
                    out.format("\n");
                }
            }
        } else {
            for (int k = BOARDSIZE - 5, c = 5; k >= 0; k -= 5, c--) {
                out.format("%d" + "  ", c);
                for (int i = k; i < k + 5; i++) {
                    out.format("%s ", get(i).shortName());
                }
                out.format("\n");
            }
            out.format("   ");

            for (char alphabet = 'a'; alphabet <= 'e'; alphabet++) {
                out.format("%s ", alphabet);
            }

        }
        return out.toString();
    }

    /**Return true iff the game is over.*/
    public boolean isGameOver() {
        if (!isMove()) {
            _gameOver = true;
        }
        return gameOver();
    }

    /** Return true iff there is a move for the current player. */
    private boolean isMove() {
        PieceColor currPlayer = whoseMove();
        ArrayList<Move> temp = new ArrayList<>();

        for (int k = 0; k < BOARDSIZE; k++) {
            if (get(k) == currPlayer) {
                getMoves(temp, k);
                if (temp.size() != 0) {
                    return true;
                }
                getJumps(temp, k);
                if (temp.size() != 0) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Board) {
            Board b = (Board) o;
            return (b.toString().equals(toString())
                    && _whoseMove == b.whoseMove()
                    && Arrays.equals(b._board, _board)
                    && Arrays.equals(b._horizontalBoard, _horizontalBoard));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**The data structure that holds the information of the board.*/
    private PieceColor[] _board;

    /**The data structure that holds the horizontal
     * information of the board.*/
    private String[] _horizontalBoard;

    /**The data structure that holds prev and current
     * information of the board.*/
    private static Stack<Board> _undoBoard;

    /**The data structure that holds the prev and current
     * horizontal information of the board.*/
    private Stack<Board> _undoHorizontal;

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}
