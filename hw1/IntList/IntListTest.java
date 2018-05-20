import static org.junit.Assert.*;
import org.junit.Test;

public class IntListTest {

    /** Sample test that verifies correctness of the IntList.list static
     *  method. The main point of this is to convince you that
     *  assertEquals knows how to handle IntLists just fine.
     */

    @Test
    public void testList() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList x = IntList.list(3, 2, 1);
        assertEquals(threeTwoOne, x);
    }

    /** Do not use the new keyword in your tests. You can create
     *  lists using the handy IntList.list method.
     *
     *  Make sure to include test cases involving lists of various sizes
     *  on both sides of the operation. That includes the empty list, which
     *  can be instantiated, for example, with
     *  IntList empty = IntList.list().
     *
     *  Keep in mind that dcatenate(A, B) is NOT required to leave A untouched.
     *  Anything can happen to A.
     */

    @Test
    public void testDcatenate() {
        IntList edge1 = new IntList(4, null);
        IntList lst1 = new IntList(2, edge1);
        IntList lst2 = new IntList(5, lst1);


        assertEquals(lst2, IntList.dcatenate(IntList.list(),IntList.list(5,2,4)));
        assertEquals(lst2, IntList.dcatenate(IntList.list(5,2,4),IntList.list()));
        assertEquals(IntList.list(), IntList.dcatenate(IntList.list(), IntList.list()));
        assertEquals(lst1, IntList.dcatenate(IntList.list(2),IntList.list(4)));
        assertEquals(IntList.list(2, 4, 3, 7), IntList.dcatenate(IntList.list(2,4), IntList.list(3,7)));

    }

    /** Tests that subtail works properly. Again, don't use new.
     *
     *  Make sure to test that subtail does not modify the list.
     */

    @Test
    public void testSubtail() {
        IntList edge1 = new IntList(4, null);
        IntList lst1 = new IntList(2, edge1);
        IntList lst2 = new IntList(5, lst1);


        assertNull(IntList.subTail(lst2, -1));
        assertEquals(lst2, IntList.subTail(lst2, 0));
        assertEquals(lst2, IntList.subTail(lst2, 1));

    }

    /** Tests that sublist works properly. Again, don't use new.
     *
     *  Make sure to test that sublist does not modify the list.
     */

    @Test
    public void testSublist() {
        IntList edge1 = new IntList(4, null);
        IntList lst1 = new IntList(2, edge1);
        IntList lst2 = new IntList(5, lst1);


        assertNull(IntList.sublist(lst2, -1,2));
        assertEquals(lst2, IntList.sublist(lst2, 0,3));
        assertNull(IntList.sublist(lst1, 4,6));
        assertNull(IntList.sublist(lst1, 0,0));
        assertEquals(lst1,IntList.sublist(lst2, 1,2) );

    }

    /** Tests that dSublist works properly. Again, don't use new.
     *
     *  As with testDcatenate, it is not safe to assume that list passed
     *  to dSublist is the same after any call to dSublist
     */

    @Test
    public void testDsublist() {
        IntList edge1 = new IntList(4, null);
        IntList lst1 = new IntList(2, edge1);
        IntList lst2 = new IntList(5, lst1);

        assertNull(IntList.dsublist(lst2, -1,0));
        assertNull(IntList.dsublist(lst2, 6,8));
        assertNull(IntList.dsublist(lst2, 0,0));
        assertEquals(lst2, IntList.dsublist(lst2, 0,3));
        assertEquals(lst1, IntList.dsublist(lst2, 1,10));
        assertEquals(lst1, IntList.dsublist(lst2, 1,2));
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(IntListTest.class));
    }
}
