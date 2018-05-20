import java.util.Arrays;
import java.util.Random;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            sortHelper(array,k,0);
        }

        public void sortHelper(int[] arr, int k, int begin) {
            k = Math.min(k,arr.length);
            begin = Math.max(begin, 0);
            for(int i = begin; i < k; i++) {
                for(int j = i; j > begin; j--){
                    if(arr[j] < arr[j-1]){
                        swap(arr, j-1, j);
                    } else {
                        break;
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            int minimum;
            for(int i = 0; i < k; i++) {
                minimum = i;
                for(int j = i+1; j < k; j++) {
                    //minimum = array[minimum] > array[j] ? minimum = j : minimum;
                    if (array[minimum] > array[j]) {
                        minimum = j;
                    }
                }

                //Now put the smallest element where it should be!
                swap(array, minimum, i);
            }
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        private static InsertionSort insertionSort = new InsertionSort();
        private static Random r = new Random();
        private static final int SAMPLE_COUNT = 3;
        @Override
        public void sort(int[] array, int k) {
            k = Math.min(k,array.length);
            quickSortHelper(array,0,k);
        }
        private void quickSortHelper(int[] array, int start, int k) {
            int temp;
            if (k - start > 5) {
                temp = partArr(array,start,k);
                quickSortHelper(array, start, temp);
                quickSortHelper(array, temp + 1,k);
            }
            else if(k - start > 1)
            {
                insertionSort.sortHelper(array, start, k);
            }
        }

        private int partArr(int[] arr, int start, int k){
            int i = start;
            int j = k - 1;
            int p = sortPivot(arr,start,k);

            while(i < j) {
                if (arr[i] >= arr[j] && (arr[i] >= arr[p] && arr[j] <= arr[p])) {
                    p = sortSwap(arr, i, j, p);
                }
                if (i < p && arr[i] <= arr[p]) {
                    i++;
                }
                if (j > p && arr[j] >= arr[p]) {
                    j--;
                }
            }

            return p;
        }

        private int sortPivot(int[] arr, int start, int k){
            int span = k - start;
            if(span < 1) {
                return -1;
            } else if (span < 4) {
                return k - 1;
            } else {
                int[] tempArr = new int[] {start, (start+k-1)/2, k-1};
                double average = (arr[start] + arr[(start + k - 1) / 2] + arr[k - 1]) / 3;
                int front = tempArr[0];
                for (int i = 1; i < 3; i++) {
                    if(Math.abs(arr[tempArr[i]] - average) < Math.abs(arr[front] - average))
                        front = tempArr[i];
                }
                return k - 1;

            }
        }

        private int sortSwap(int arr[], int i, int j, int p){
            swap(arr,i,j);
            if (i == p) {
                p = j;
            } else if (j == p) {
                p = i;
            }
            return p;

        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            k = Math.min(k, a.length);
            int[] arr = new int[ k];
            for (int i = 1; i != 0; i <<= 1) {
                int z = 0;
                int o = 0;
                for (int b = 0; b < k; b++) {
                    if ((a[b] & i) > 0) {
                        o++;
                    } else {
                        z++;
                    }
                }
                if (o + z != k)
                    break;
                o = z;
                z = 0;
                for (int s = 0; k < a.length; k++) {
                    if ((a[s] & i) > 0)
                        arr[o++] = a[s];
                    else
                        arr[z++] = a[s];
                }

                System.arraycopy(arr, 0, a, 0, k);

            }
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
