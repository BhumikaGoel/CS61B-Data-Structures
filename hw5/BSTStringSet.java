import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a BST based String Set.
 * @author BHUMIKA GOEL
 */
public class BSTStringSet implements StringSet {
    /** Creates a new empty set. */
    public BSTStringSet() {
        root = null;
    }

    /** Adds the string S to the string set. If it is already present in the
     * set, do nothing. */
    @Override
    public void put(String s) {
        root = helperPut(s, root);
    }

    private Node helperPut(String s, Node n) {
        if (n == null) {
            return new Node(s);
        }

        int c = s.compareTo(n.s);
        if (c < 0) {
            n.left = helperPut(s, n.left);
        } else if (c == 0) {
            return n;
        } else {
            n.right = helperPut(s, n.right);
        }
        return n;
    }

    @Override
    public boolean contains(String s) {
        return helperContains(s, root);
    }

    private boolean helperContains(String s, Node n) {
       if (n == null) {
           return false;
       }
       int c = s.compareTo(n.s);
       if (c < 0) {
           return helperContains(s, n.left);
       } else if (c == 0) {
           return true;
       } else {
           return helperContains(s, n.right);
       }
    }

    @Override
    public List<String> asList() {
        return helperAsList(root);
    }

    private List<String> helperAsList(Node n) {
        ArrayList<String> ascendingList = new ArrayList<String>();
        if (n == null) {
            return ascendingList;
        }

        if (n.left != null) {
            ascendingList.addAll(helperAsList(n.left));
        }
        ascendingList.add(n.s);
        if (n.right != null) {
            ascendingList.addAll(helperAsList(n.right));
        }
        return ascendingList;
    }

    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        public Node(String sp) {
            s = sp;
        }
    }

    /** Root node of the tree. */
    private Node root;
}
