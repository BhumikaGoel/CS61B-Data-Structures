import org.junit.Test;
import static org.junit.Assert.*;

/**
 *  @author Bhumika Goel
 */

public class ArraysTest {

    @Test
    public void testconcatenate() {
        int[] A = {1,2,3};
        int[] B = {4,5};
        int[] res = {1,2,3,4,5};
        int[] C = {};

        assertArrayEquals(Arrays.catenate(A,B), res);
        assertArrayEquals(Arrays.catenate(A,C), A);
        assertArrayEquals(Arrays.catenate(C,B), B);
    }

    @Test
    public void testremove() {
        int[] A = {1,2,3,4,5};
        int[] B = {};
        int[] res = {1,2,5};
        int[] res1 = {3,4,5};
        assertArrayEquals(Arrays.remove(B, 3,4), B);
        assertArrayEquals(Arrays.remove(A, 2,2), res);
        assertArrayEquals(Arrays.remove(A, 0,2), res1);

    }

    @Test
    public void testnaturalRuns() {
        int[][] A = {{1, 2, 5}, {3}, {1, 6, 7}};
        int[] B = {1, 2, 5, 3, 1, 6, 7};
        int[][] C = {{1,6,7}};
        int[] D = {1,6,7};
        int[] emp = null;
        int[][] E = null;

        assertArrayEquals(Arrays.naturalRuns(B), A);
        assertArrayEquals(Arrays.naturalRuns(D),C);
        assertArrayEquals(Arrays.naturalRuns(emp),E);
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
