/** Functions to increment and sum the elements of a WeirdList. */
class WeirdListClient {

    /** Return the result of adding N to each element of L. */
    static WeirdList add(WeirdList L, int n) {
        SomeFunc add = new SomeFunc(n);
        return L.map(add);
    }

    /** Return the sum of the elements in L. */
    static int sum(WeirdList L) {
        AddAllElems add = new AddAllElems(0);
        L.map(add);
        return add.result();

    }

    /* As with WeirdList, you'll need to add an additional class or
     * perhaps more for WeirdListClient to work. Again, you may put
     * those classes either inside WeirdListClient as private static
     * classes, or in their own separate files.

     * You are still forbidden to use any of the following:
     *       if, switch, while, for, do, try, or the ?: operator.
     */
    private static class SomeFunc implements IntUnaryFunction {
        private int x;
        public SomeFunc (int x) {
            this.x = x;
        }
        public int apply(int i) {
            return i + x;
        }

    }

    private static class AddAllElems implements IntUnaryFunction {
        private int sum = 0;

        public AddAllElems(int i) {
            this.sum = i;
        }

        public int apply(int x) {
            this.sum += x;
            return x;
        }

        public int result() {
            return this.sum;
        }
    }
}
