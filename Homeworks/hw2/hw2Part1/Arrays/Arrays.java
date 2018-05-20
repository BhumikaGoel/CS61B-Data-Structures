import java.util.ArrayList;
/* NOTE: The file ArrayUtil.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author Bhumika Goel
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        /* *Replace this body with the solution. */
        int totalLength = A.length + B.length;
        int[] arr = new int[totalLength];

        if (A.length == 0) {
            return B;
        } else if (B.length == 0) {
            return A;
        } else {
            System.arraycopy(A, 0, arr, 0, A.length);
            System.arraycopy(B, 0, arr, A.length, B.length);
            return arr;
        }
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        /* *Replace this body with the solution. */
        if (A.length != 0) {
            int[] arr = new int[A.length - len];

            System.arraycopy(A, 0, arr, 0, start);
            System.arraycopy(A, start + len, arr, start, A.length - (len + start));
            return arr;
        } else {
            return A;
        }
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        int subArrays = 1;
        if (A != null) {
            for (int i = 0; i < A.length - 1; i++) {
                if (A[ i ] > A[ i + 1 ]) {
                    subArrays++;
                }
            }

            int[][] res = new int[ subArrays ][];
            int count = 0;
            while ( count < subArrays ) {
                int subArrayIndex = 0;
                while ( (subArrayIndex < A.length - 1) && (A[ subArrayIndex ] < A[ subArrayIndex + 1 ]) ) {
                    subArrayIndex++;
                }
                res[ count ] = Utils.subarray(A, 0, subArrayIndex + 1);
                A = remove(A, 0, subArrayIndex + 1);
                count++;
            }

            return res;
        } else {
            return null;
        }
    }

}
