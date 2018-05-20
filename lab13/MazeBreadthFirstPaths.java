import java.util.Observable;
import java.util.PriorityQueue;
/**
 *  @author Josh Hug
 */

public class MazeBreadthFirstPaths extends MazeExplorer {
   /* Inherits public fields:*/
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    private int source_1D;
    private int target_1D;
    private boolean finish = false;
    private Maze maze;


    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        source_1D = maze.xyTo1D(sourceX, sourceY);
        distTo[source_1D] = 0;
        edgeTo[source_1D] = source_1D;
        target_1D = maze.xyTo1D(targetX, targetY);

    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        // TODO: Your code here. Don't forget to update distTo, edgeTo, and marked, as well as call announce()
        /*doing iteratively since I am not able get it recursively :(( */
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.add(source_1D);
        while (!pq.isEmpty()) {
            int i = pq.remove();
            marked[i] = true;
            announce();
            if (i == target_1D) {
                finish = true;
                return;
            }
            for (int neighbour : maze.adj(i)) {
                if (!marked[neighbour]) {
                    edgeTo[neighbour] = i;
                    announce();
                    distTo[neighbour] = distTo[i] + 1;
                    pq.add(neighbour);
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

