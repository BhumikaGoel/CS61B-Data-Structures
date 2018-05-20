
/** A partition of a set of contiguous integers that allows (a) finding whether
 *  two integers are in the same partition set and (b) replacing two partitions
 *  with their union.  At any given time, for a structure partitioning
 *  the integers 1-N, each partition is represented by a unique member of that
 *  partition, called its representative.
 *  @author Bhumika Goel
 */
public class UnionFind {

    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        arr = new int[N+1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
    }

    /** Return the representative of the partition currently containing V.
     *  Assumes V is contained in one of the partitions.  */
    public int find(int v) {
        int store = v;
        while (store != arr[store]) {
            store = arr[store];
        }

        while (v != store) {
            int temp = arr[v];
            arr[v] = store;
            v = temp;
        }
        return store;
    }

    /** Return true iff U and V are in the same partition. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single partition, returning its representative. */
    public int union(int u, int v) {
        int rep1 = find(u);
        int rep2 = find(v);
        arr[rep1] = rep2;
        return rep2;
    }

    public int[] arr;
}
