import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.MatchResult;

public class P2 {

    public static void main(String... ignored) {
        String pattern = "\\s*([A-Za-z0-9]+)\\s+([A-Za-z0-9]+)";
        Scanner scanner = new Scanner(System.in);
        int match = 1;
        String str1, str2;

        while (/*some condition */  != null) {
            MatchResult smthng = scanner.match();
            String str1 = smthng.group(1);
            String str2 = smthng.group(2);

            Tree tree = consSpecificTree(str1, str2);
            String str = successivelyTrav(tree, "");

            System.out.printf("Case %d: %s\n\n", match, str);
            match++;
        }

    }

    /** Somehow I want to make a new tree which takes in str1 and str2*/
    private static Tree consSpecificTree(String str1, String str2) {
        return null; //Magic
    }

    private static String successivelyTrav(Tree tree, String str) {
        if (tree == null) {
            return "";
        } else {
            return str
                    + successivelyTrav(tree._left, str)
                    + traverseInOrder(tree._right, str)
                    + tree._entry;
        }
    }
    private static class Tree {

        Tree(Tree left, Tree right, Character root) {
            _leftB = left;
            _rightB = right;
            _root = root;
        }
        private Tree _leftB, _rightB;
        private Character _root;
    }


}

