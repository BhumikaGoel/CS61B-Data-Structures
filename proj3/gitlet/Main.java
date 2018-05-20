package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 * This project is done in a group of three additional people:
 * Andrew Haitz, Pedram Pourdavood, and the biggest thanks to Max Yao,
 * for always helping me through this unusually rough
 * and crunched and jetlagged semester.
 * @author Bhumika Goel
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        Command start = new Command();
        start.setArgs(args);
    }
}
