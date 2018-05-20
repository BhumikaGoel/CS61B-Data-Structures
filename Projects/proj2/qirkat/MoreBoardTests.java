package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

public class MoreBoardTests {

    private final char[][] boardRepr = new char[][]{
        {'b', 'b', 'b', 'b', 'b'},
        {'b', 'b', 'b', 'b', 'b'},
        {'b', 'b', '-', 'w', 'w'},
        {'w', 'w', 'w', 'w', 'w'},
        {'w', 'w', 'w', 'w', 'w'}
    };

    private final PieceColor currMove = PieceColor.WHITE;

    /**
     * @return the String representation of the initial state. This will
     * be a string in which we concatenate the values from the bottom of
     * board upwards, so we can pass it into setPieces. Read the comments
     * in Board#setPieces for more information.
     * For our current boardRepr, the String returned by
     * getInitialRepresentation is
     * "  w w w w w\n  w w w w w\n  b b - w w\n  b b b b b\n  b b b b b"
     *
     * We use a StringBuilder to avoid recreating Strings (because Strings
     * are immutable).
     */
    private String getInitialRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = boardRepr.length - 1; i >= 0; i--) {
            for (int j = 0; j < boardRepr[0].length; j++) {
                sb.append(boardRepr[i][j] + " ");
            }
            sb.deleteCharAt(sb.length() - 1);
            if (i != 0) {
                sb.append("\n  ");
            }
        }
        return sb.toString();
    }

    private Board getBoard() {
        Board b = new Board();
        b.setPieces(getInitialRepresentation(), currMove);
        return b;
    }

    private void resetToInitialState(Board b) {
        b.setPieces(getInitialRepresentation(), currMove);
    }

    @Test
    public void testSetPieces() {
        Board b = getBoard();
        String str = "ww--w b-ww- ----- -b--- bb---";
        PieceColor nextMove = PieceColor.WHITE;
        b.setPieces(str, nextMove);
        b.toString(true);
    }

    @Test
    public void testGet() {
        Board b = getBoard();
        String str = "ww--w b-ww- ----- -b--- bb---";
        PieceColor nextMove = PieceColor.WHITE;
        b.setPieces(str, nextMove);
        assertEquals(PieceColor.EMPTY, b.get(2));
        assertEquals(PieceColor.BLACK, b.get(5));
        assertEquals(PieceColor.WHITE, b.get(7));
    }

    @Test
    public void testCheckJump() {
        Board b = new Board();
        String str = "----- -w--- -b--- ----- -----";
        b.setPieces(str, PieceColor.WHITE);
        Move mov = Move.parseMove("b2-b4");
        assertTrue(b.checkJump(mov, true));

        Board b1 = new Board();
        String str1 = "----- -w--- -bbb- ----- -----";
        b1.setPieces(str1, PieceColor.WHITE);
        b1.toString();
        Move mov1 = Move.parseMove("b2-b4-d2-d4");
        assertTrue(b1.checkJump(mov1, true));

        Board b4 = new Board();
        String str4 = "----- -w--- -ww-- ----- -----";
        b4.setPieces(str4, PieceColor.WHITE);
        Move mov4 = Move.parseMove("b2-b4");
        assertFalse(b4.checkJump(mov4, true));

    }


}

