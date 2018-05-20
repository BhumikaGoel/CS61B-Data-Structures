import java.util.Arrays;
import java.util.Comparator;

/** Minimal spanning tree utility.
 *  @author Bhumika Goel
 */
public class MST {

    /** Given an undirected, weighted, connected graph whose vertices are
     *  numbered 1 to V, and an array E of edges, returns a list of edges
     *  in E that form a minimal spanning tree of the input graph.
     *  Each edge in E is a three-element int array of the form (u, v, w),
     *  where 0 < u < v <= V are vertex numbers, and 0 <= w is the weight
     *  of the edge. The result is an array containing edges from E.
     *  Neither E nor the arrays in it may be modified.  There may be
     *  multiple edges between vertices.  The objects in the returned array
     *  are a subset of those in E (they do not include copies of the
     *  original edges, just the original edges themselves.) */
    public static int[][] mst(int V, int[][] E) {
        UnionFind bla = new UnionFind(V);
        int[][] deepDuplicateE = new int[E.length][];
        int[][] mst = new int[V-1][];
        int k = 0;
        System.arraycopy(E, 0, deepDuplicateE, 0, E.length);
        Arrays.sort(deepDuplicateE, EDGE_WEIGHT_COMPARATOR);

        for (int i = 0; i < deepDuplicateE.length; i++) {
            //int node1 = deepDuplicateE[i][0];
            //int node2 = deepDuplicateE[i][1];

            if (!bla.samePartition(deepDuplicateE[i][0], deepDuplicateE[i][1])) {
                mst[k] = deepDuplicateE[i];
                k++;
                bla.union(deepDuplicateE[i][0], deepDuplicateE[i][1]);
            }
            if (k == V - 1) {
                break;
            }
        }
        return mst;
    }

    /** An ordering of edges by weight. */
    private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
        new Comparator<int[]>() {
            @Override
            public int compare(int[] e0, int[] e1) {
                return e0[2] - e1[2];
            }
        };

}
