import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 *  @author BHUMIKA GOEL
 */

public class ListsTest {
    /**
     */

    // It might initially seem daunting to try to set up
    // Intlist2 expected.
    //
    // There is an easy way to get the IntList2 that you want in just
    // few lines of code! Make note of the IntList2.list method that
    // takes as input a 2D array.

    @Test
    public void testnaturalRuns() {
        int[][] A = {{1, 2, 5}, {3}, {1, 6, 7}};
        IntList b1 = new IntList(7, null);
        IntList b2 = new IntList(6, b1);
        IntList b3 = new IntList(1, b2);
        IntList b4 = new IntList(3, b3);
        IntList b5 = new IntList(5, b4);
        IntList b6 = new IntList(2, b5);
        IntList B = new IntList(1, b6);
        int[][] C = {{1,6,7}};
        IntList emp = null;
        int[][] D = null;

        assertEquals(Lists.naturalRuns(B), A);
        assertEquals(Lists.naturalRuns(b3),C);
        assertEquals(Lists.naturalRuns(emp),D);
    }



    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
