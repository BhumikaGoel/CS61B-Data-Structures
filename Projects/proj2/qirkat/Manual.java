package qirkat;


import static qirkat.PieceColor.*;
import static qirkat.Command.Type.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author BHUMIKA GOEL
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _prompt = myColor + ": ";
    }

    /** Return a legal move for me. Assumes that
     *  board.whoseMove() == myColor and that !board.gameOver(). */
    @Override
    Move myMove() {

        Move temp = null;
        while (game().state() == Game.State.PLAYING) {
            try {
                Command promptCmd = game().getMoveCmnd(_prompt);
                if (promptCmd == null) {
                    continue;
                }
                temp = Move.parseMove(promptCmd.operands()[ 0 ]);
                if (!game().readboard().legalMove(temp)) {
                    game().reportError("Illegal move.");
                    continue;
                }
                return temp;
            } catch (IllegalArgumentException
                    | NullPointerException exception) {
                game().reportError(exception.getMessage());
            }
        }

        return temp;
    }

    /** Identifies the player serving as a source of input commands. */
    private String _prompt;
}

