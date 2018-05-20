import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.

        assertEquals(0, 0); */
        assertEquals(0, CompoundInterest.numYears(2015));
        assertEquals(5, CompoundInterest.numYears(2020));
    }

    @Test
    public void testFutureValue() {
        double tolerance = 0.01;

        assertEquals(100, CompoundInterest.futureValue(100, 5, 2015), tolerance);
        assertEquals(105, CompoundInterest.futureValue(100, 5, 2016), tolerance);
        assertEquals(121.551, CompoundInterest.futureValue(100, 5, 2019), tolerance);
        assertEquals(90.25, CompoundInterest.futureValue(100, -5, 2017), tolerance);
    }
    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(100, CompoundInterest.futureValueReal(100, 5, 2015, 3), tolerance);
        assertEquals(101.85, CompoundInterest.futureValueReal(100, 5, 2016, 3), tolerance);
        assertEquals(107.608, CompoundInterest.futureValueReal(100, 5, 2019, 3), tolerance);
        assertEquals(84.916, CompoundInterest.futureValueReal(100, -5, 2017, 3), tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(100, CompoundInterest.totalSavings(100, 2015, 5), tolerance);
        assertEquals(205, CompoundInterest.totalSavings(100, 2016, 5), tolerance);
        assertEquals(552.563, CompoundInterest.totalSavings(100, 2019, 5), tolerance);
        assertEquals(285.25, CompoundInterest.totalSavings(100, 2017, -5), tolerance);


    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(100, CompoundInterest.totalSavingsReal(100, 2015, 5, 3), tolerance);
        assertEquals(202.529, CompoundInterest.totalSavingsReal(101.85, 2016, 5, 3), tolerance);
        assertEquals(489.180, CompoundInterest.totalSavingsReal(100, 2019, 5, 3), tolerance);
        assertEquals(268.392, CompoundInterest.totalSavingsReal(100, 2017, -5, 3), tolerance);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}

/*  public void testProduct() {
        /* assertEquals for comparison of doubles takes three arguments:
         *      assertEquals(expected, actual, DELTA).
         *  + if Math.abs(expected - actual) < DELTA, then the test succeeds.
         *  + Otherwise, the test fails.
         *
         *  See http://junit.sourceforge.net/javadoc/org/junit/ \
         *             Assert.html#assertEquals(double, double, double)
         *  for more. */

   /* assertEquals(30, Arithmetic.product(5, 6), DELTA);
        assertEquals(-30, Arithmetic.product(5, -6), DELTA);
        assertEquals(0, Arithmetic.product(0, -6), DELTA);
        }
* */