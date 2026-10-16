import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Clustering {
    private CC clusters;
    private int m;

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        if (locations == null)
            throw new IllegalArgumentException("null argument");
        m = locations.length;
        if (k < 1 || k > m)
            throw new IllegalArgumentException("invalid number of clusters");

        EdgeWeightedGraph ewg = new EdgeWeightedGraph(m);
        // add all edges between locations with weight equal to their distance
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                ewg.addEdge(new Edge(i, j, locations[i].distanceTo(locations[j])));
            }
        }
        KruskalMST mst = new KruskalMST(ewg);
        MinPQ<Edge> pq = new MinPQ<>();
        for (Edge e : mst.edges()) pq.insert(e);
        EdgeWeightedGraph clusterGraph = new EdgeWeightedGraph(m);
        // only add the m-k least weighted edges in the mst
        for (int i = 0; i < m - k; i++) clusterGraph.addEdge(pq.delMin());
        clusters = new CC(clusterGraph);
    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        if (i < 0 || i > m - 1) throw new IllegalArgumentException("outside of range");

        return clusters.id(i);
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {
        if (input == null) throw new IllegalArgumentException("null argument");
        if (input.length != m) throw new IllegalArgumentException(
                "input length does not equal the number of locations");

        int[] reduced = new int[clusters.count()];
        // keys are cluster ids, values are indices in reduced[]
        SeparateChainingHashST<Integer, Integer> st
                = new SeparateChainingHashST<Integer, Integer>(clusters.count());
        int count = 0;
        for (int i = 0; i < input.length; i++) {
            if (!st.contains(clusters.id(i))) st.put(clusters.id(i), count++);
            reduced[st.get(clusters.id(i))] += input[i];
        }
        return reduced;
    }

    // check that center is at least distance 4 from all other centers
    private static boolean checkCenter(Point2D center, Point2D[] centers, int i) {
        for (int j = 0; j < i; j++) {
            if (center.distanceTo(centers[j]) < 4) {
                return false;
            }
        }
        return true;
    }


    // unit testing (required)
    public static void main(String[] args) {
        int c = Integer.parseInt(args[0]);
        int p = Integer.parseInt(args[1]);
        Point2D[] centers = new Point2D[c];
        for (int i = 0; i < c; i++) { // creating centers
            Point2D center = new Point2D(StdRandom.uniformDouble(0, 1000),
                                         StdRandom.uniformDouble(0, 1000));
            if (checkCenter(center, centers, i)) centers[i] = center;
            else i--;
        }
        Point2D[] locations = new Point2D[c * p];
        for (int i = 0; i < c; i++) { // creating locations
            for (int j = 0; j < p; j++) {
                Point2D location = new Point2D(centers[i].x() + StdRandom.uniformDouble(),
                                               centers[i].y() + StdRandom.uniformDouble());
                if (location.distanceTo(centers[i]) > 1) j--;
                else locations[i * p + j] = location;
            }
        }
        Clustering test = new Clustering(locations, c);
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < p - 1; j++) {
                if (test.clusterOf(i * p + j) != test.clusterOf(i * p + j + 1))
                    StdOut.print("Error!");
            }
        }
    }
}
