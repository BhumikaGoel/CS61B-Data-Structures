
public class IntDList {

    protected DNode _front, _back;

    public IntDList() {
        _front = _back = null;
    }

    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /** Returns the first item in the IntDList. */
    public int getFront() {
        return _front._val;
    }

    /** Returns the last item in the IntDList. */
    public int getBack() {
        return _back._val;
    }

    /** Return value #I in this list, where item 0 is the first, 1 is the
     *  second, ...., -1 is the last, -2 the second to last.... */
    public int get(int i) {
        //return 0;
        DNode tracker;
       // int ans;
        if (i < 0) {
            tracker = _back;
            int c = -1;
            while (c > i) {
                //ans = tracker._val;
                tracker = tracker._prev;
                c--;

            }
            return tracker._val;

        }
        else {
            tracker = _front;
            int c = 0;
            while (c < i) {
                //ans = tracker._val;
                tracker = tracker._next;
                c++;

            }
            return tracker._val;
        }
          // Your code here
    }

    /** The length of this list. */
    public int size() {
        //return 0;
        int counter = 0;
        DNode tracker = _front;
        if (_front != null) {
            while (tracker._next != null) {
                tracker = tracker._next;
                counter++;
            }
            counter +=1; // Your code here
        }
        return counter;
    }

    /** Adds D to the front of the IntDList. */
    public void insertFront(int d) {
        DNode newDNode = new DNode(null, d, null);// Your code here
        newDNode._next = _front;
        _front._prev = newDNode;
        _front = newDNode;
    }

    /** Adds D to the back of the IntDList. */
    public void insertBack(int d) {
        DNode newDNode = new DNode(null, d, null);
        if (_front == null && _back == null) {
            _front = newDNode;// Your code here
            _back = newDNode;
        }
        else {
            _back._next = newDNode;
            newDNode._prev = _back;
            _back = newDNode;

        }

    }

    /** Removes the last item in the IntDList and returns it.
     * This is an extra challenge problem. */
    public int deleteBack() {
        return 0;   // Your code here

    }

    /** Returns a string representation of the IntDList in the form
     *  [] (empty list) or [1, 2], etc. 
     * This is an extra challenge problem. */
    public String toString() {
        return null;   // Your code here
    }

    /* DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information! */
    protected static class DNode {
        protected DNode _prev;
        protected DNode _next;
        protected int _val;

        private DNode(int val) {
            this(null, val, null);
        }

        private DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
