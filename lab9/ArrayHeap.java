import java.util.ArrayList;

/** A Generic heap class. Unlike Java's priority queue, this heap doesn't just
  * store Comparable objects. Instead, it can store any type of object
  * (represented by type T) and an associated priority value.
  * @author CS 61BL Staff*/
public class ArrayHeap<T> {

    /* DO NOT CHANGE THESE METHODS. */

    /* An ArrayList that stores the nodes in this binary heap. */
    private ArrayList<Node> contents;

    /* A constructor that initializes an empty ArrayHeap. */
    public ArrayHeap() {
        contents = new ArrayList<>();
        contents.add(null);
    }

    /* Returns the number of elments in the priority queue. */
    public int size() {
        return contents.size() - 1;
    }

    /* Returns the node at index INDEX. */
    private Node getNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.get(index);
        }
    }

    /* Sets the node at INDEX to N */
    private void setNode(int index, Node n) {
        // In the case that the ArrayList is not big enough
        // add null elements until it is the right size
        while (index + 1 > contents.size()) {
            contents.add(null);
        }
        contents.set(index, n);
    }

    /* Returns and removes the node located at INDEX. */
    private Node removeNode(int index) {
        if (index >= contents.size()) {
            return null;
        } else {
            return contents.remove(index);
        }
    }

    /* Swap the nodes at the two indices. */
    private void swap(int index1, int index2) {
        Node node1 = getNode(index1);
        Node node2 = getNode(index2);
        this.contents.set(index1, node2);
        this.contents.set(index2, node1);
    }

    /* Prints out the heap sideways. Use for debugging. */
    @Override
    public String toString() {
        return toStringHelper(1, "");
    }

    /* Recursive helper method for toString. */
    private String toStringHelper(int index, String soFar) {
        if (getNode(index) == null) {
            return "";
        } else {
            String toReturn = "";
            int rightChild = getRightOf(index);
            toReturn += toStringHelper(rightChild, "        " + soFar);
            if (getNode(rightChild) != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + getNode(index) + "\n";
            int leftChild = getLeftOf(index);
            if (getNode(leftChild) != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += toStringHelper(leftChild, "        " + soFar);
            return toReturn;
        }
    }

    /* A Node class that stores items and their associated priorities. */
    public class Node {
        private T item;
        private double priority;

        private Node(T item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        public T item() {
            return this.item;
        }

        public double priority() {
            return this.priority;
        }

        public void setPriority(double priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            return this.item.toString() + ", " + this.priority;
        }
    }



    /* FILL IN THE METHODS BELOW. */

    /* Returns the index of the node to the left of the node at i. */
    private int getLeftOf(int i) {
        //YOUR CODE HERE

        int parent_index = getParentOf(i);;
        if (i != 2 * parent_index) {
            return 2 * parent_index;
        } else {
            return i;
        }
    }

    /* Returns the index of the node to the right of the node at i. */
    private int getRightOf(int i) {
        //YOUR CODE HERE
        int parent_index = getParentOf(i);
        int right_index = 2 * parent_index + 1;
        if (i != right_index) {
            return right_index;
        } else {
            return i;
        }

    }

    /* Returns the index of the node that is the parent of the node at i. */
    private int getParentOf(int i) {
        //YOUR CODE HERE
        if (i > 1) {
            return Math.floorDiv(i, 2);
        } else {
            return i;
        }

    }

    /* Adds the given node as a left child of the node at the given index. */
    private void setLeft(int index, Node n) {
        //YOUR CODE HERE
        int left_index = 2 * index;
        setNode(left_index, n);
        return;

    }

    /* Adds the given node as the right child of the node at the given index. */
    private void setRight(int index, Node n) {
        //YOUR CODE HERE
        int right_index = 2 * index + 1;
        setNode(right_index, n);
        return;
    }

    /** Returns the index of the node with smaller priority. Precondition: not
      * both nodes are null. */
    private int min(int index1, int index2) {
        //YOUR CODE HERE
        if (getNode(index1) == null) {
            return index1;
        } else if (getNode(index2) == null) {
            return index2;
        } else {
            double priority1 = getNode(index1).priority();
            double priority2 = getNode(index2).priority();
            return priority1 < priority2 ? index1 : index2;
        }
    }

    /* Returns the Node with the smallest priority value, but does not remove it
     * from the heap. */
    public Node peek() {
        //YOUR CODE HERE
        return getNode(1);
    }

    /* Bubbles up the node currently at the given index. */
    private void bubbleUp(int index) {

       /* if (index == 1) {
            return;
        } else if (index!= 0) {
            int parent_index = getParentOf(index);
            if ((getNode(index) != null) && (getNode(parent_index) != null)) {
                if (getNode(index).priority() < getNode(parent_index).priority()) {
                    swap(index, parent_index);
                    bubbleUp(parent_index);
                }
            }
        }*/

       while (getNode(index).priority() < getNode(getParentOf(index)).priority()
               && getParentOf(index) != index) {
           swap(index, getParentOf(index));
       }

    }

    /* Bubbles down the node currently at the given index. */
    private void bubbleDown(int index) {
        //YOUR CODE HERE
        int original_index = index;
        int left_index, right_index, min_index;
        left_index = getLeftOf(original_index);
        right_index = getRightOf(original_index);

        if (getNode(left_index) == null) {
            if (getNode(right_index) != null && getNode(original_index).priority() > getNode(right_index).priority()) {
                swap(original_index, right_index);
                bubbleDown(right_index);
            }

        } else if (getNode(right_index) == null) {
            if (getNode(left_index) != null && getNode(original_index).priority() > getNode(left_index).priority()) {
                swap(original_index, left_index);
                bubbleDown(left_index);
            }

        } else {
            min_index = min(left_index, right_index);
            if (min_index == left_index) {
                swap(original_index, left_index);
                bubbleDown(left_index);
            } else {
                swap(original_index, right_index);
                bubbleDown(right_index);
            }

        }
        return ;
    }

    /* Inserts an item with the given priority value. Same as enqueue, or offer. */
    public void insert(T item, double priority) {
        //YOUR CODE HERE
        Node newNode = new Node(item, priority);
        setNode(size() + 1, newNode);
        bubbleUp(size());

    }

    /* Returns the element with the smallest priority value, and removes it from
     * the heap. Same as dequeue, or poll. */
    public T removeMin() {
        //YOUR CODE HERE
        Node newNode = peek();
        swap(1, size());
        removeNode(size());
        bubbleDown(1);

        return newNode.item();
    }

    /* Changes the node in this heap with the given item to have the given
     * priority. You can assume the heap will not have two nodes with the same
     * item. Check for item equality with .equals(), not == */
    public void changePriority(T item, double priority) {
        for (int i = 1; i <= size(); i++) {
            if (getNode(i).item().equals(item)) {
                getNode(i).setPriority(priority);
            }
        }
        return;
    }

}
