/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

/** List problem.
 *  @author Bhumika Goel
 */
class Lists {
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntList2 naturalRuns(IntList L) {

        if (L == null) {
            return null;
        }

        IntList trailer = null;
        IntList header = L;
        IntList p2 = L;
        for (int i = L.head; (L != null) && (i <= L.head); L = L.tail) {
            i = L.head;
            trailer = L.tail;
            header = L;
            if (trailer != null && i == trailer.head) {
                break;
            }
        }
        if (header != null) {
            header.tail = null;
        }
        return new IntList2(p2, naturalRuns(trailer));
    }
}

