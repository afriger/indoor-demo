package com.afrig.graph;
// A Java program for Dijkstra's
// single source shortest path
// algorithm.
import android.graphics.Color;

import com.afrig.plotter.Plotter;
import com.afrig.plotter.AnchorPoint;
import com.afrig.utilities.SaLogger;

import java.util.ArrayList;
import java.util.List;

public class DijkstrasAlgorithm
{
    private final int NO_PARENT = -1;
    private List<Integer> mPath = new ArrayList<>();
    private ArrayList<Double> mShortestDistances = new ArrayList<>();
    double[][] mAdjacencyMatrix = null;
    String tmp;
    SaLogger log = SaLogger.Log;
    public List<AnchorPoint> beacons = new ArrayList<>();
    public static class bea
    {
        public int x, y, alpha;
        public String name;

        public bea(int x, int y, String name)
        {
            this.x = x;
            this.y = y;
            this.name = name;
            alpha = 1;
        }

        public AnchorPoint position()
        {
            return new AnchorPoint(this.x, this.y, 0);
        }
    }

    public DijkstrasAlgorithm()
    {
    }

    public void Init(ArrayList<bea> beas)
    {
        int size = beas.size();
        mAdjacencyMatrix = new double[size][size];
        for (int i = 0; i < size; ++i)
        {
            bea b1 = beas.get(i);
            beacons.add(b1.position());
            for (int j = 0; j < size; ++j)
            {
                bea b2 = beas.get(j);
                if (i == j)
                {
                    mAdjacencyMatrix[i][j] = 0;
                    continue;
                }
                if ((i == 0 && j == 5) || (i == 5 && j == 0))
                {
                    mAdjacencyMatrix[i][j] = 0;
                    continue;
                }
                double d = getDis(b1, b2);
                mAdjacencyMatrix[i][j] = d;
            }
        }
        for (int i = 0; i < size; ++i)
        {
            for (int j = 0; j < size; ++j)
            {
                log.t("(" + i + "," + j + ") = " + mAdjacencyMatrix[i][j]);
            }
        }
    }

    private double getDis(bea b1, bea b2)
    {
        return Math.sqrt((b1.x - b2.x) * (b1.x - b2.x) + (b1.y - b2.y) * (b1.y - b2.y));
    }

    // Function that implements Dijkstra's
    // single source shortest path
    // algorithm for a graph represented
    // using adjacency matrix
    // representation
    public void dijkstra(int startVertex, int endVertex)
    {
        dijkstra(mAdjacencyMatrix, startVertex, endVertex);
    }

    public void dijkstra(double[][] adjacencyMatrix, int startVertex, int endVertex)
    {
        int nVertices = adjacencyMatrix[0].length;
        // mShortestDistances[i] will hold the
        // shortest distance from src to i
        mShortestDistances.clear();
        // added[i] will true if vertex i is
        // included / in shortest path tree
        // or shortest distance from src to
        // i is finalized
        boolean[] added = new boolean[nVertices];
        // Initialize all distances as
        // INFINITE and added[] as false
        for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
        {
            mShortestDistances.add(Double.MAX_VALUE);
            added[vertexIndex] = false;
        }
        // Distance of source vertex from
        // itself is always 0
        mShortestDistances.add(startVertex, (double) 0);
        // Parent array to store shortest path tree
        int[] parents = new int[nVertices];
        // The starting vertex does not
        // have a parent
        parents[startVertex] = NO_PARENT;
        // Find shortest path for all vertices
        for (int i = 1; i < nVertices; i++)
        {
            // Pick the minimum distance vertex
            // from the set of vertices not yet
            // processed. nearestVertex is
            // always equal to startNode in
            // first iteration.
            int nearestVertex = -1;
            double shortestDistance = Double.MAX_VALUE;
            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
            {
                if (!added[vertexIndex] && mShortestDistances.get(vertexIndex) < shortestDistance)
                {
                    nearestVertex = vertexIndex;
                    shortestDistance = mShortestDistances.get(vertexIndex);
                }
            }
            // Mark the picked vertex as processed
            added[nearestVertex] = true;
            // Update dist value of the
            // adjacent vertices of the
            // picked vertex.
            for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
            {
                double edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];
                if (edgeDistance > 0 && ((shortestDistance + edgeDistance) < mShortestDistances.get(vertexIndex)))
                {
                    parents[vertexIndex] = nearestVertex;
                    mShortestDistances.set(vertexIndex, shortestDistance + edgeDistance);
                }
            }
        }
        printSolution(startVertex, parents);
        getSolution(startVertex, endVertex, parents);
    }

    // A utility function to print
    // the constructed distances
    // array and shortest paths
    private void getSolution(int startVertex, int endIndex, int[] parents)
    {
        mPath.clear();
        if (endIndex != startVertex)
        {
            getPath(endIndex, parents);
            log.t("\n(" + startVertex + ")->(" + endIndex + ") dist: " + mShortestDistances.get(endIndex) + "; mPath: " + mPath.toString());
        }
    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    private void getPath(int currentVertex, int[] parents)
    {
        // Base case : Source node has been processed
        if (currentVertex == NO_PARENT)
        {
            return;
        }
        mPath.add(0, currentVertex);
        getPath(parents[currentVertex], parents);
    }

    private void printSolution(int startVertex, int[] parents)
    {
        int nVertices = mShortestDistances.size() - 1;
        for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++)
        {
            if (vertexIndex != startVertex)
            {
                String p = "", res;
                tmp = "";
                res = ("" + startVertex + " -> " + vertexIndex + "; dist: " + mShortestDistances.get(vertexIndex));
                printPath(vertexIndex, parents);
                log.t(res + "; path: " + tmp);
            }
        }
    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    private void printPath(int currentVertex, int[] parents)
    {
        // Base case : Source node has been processed
        if (currentVertex == NO_PARENT)
        {
            return;
        }
        printPath(parents[currentVertex], parents);
        tmp += (currentVertex + " ");
    }

    // Driver Code
    public static void test()
    {
        double[][] adjacencyMatrix = {
                {0, 4, 0, 0, 0, 0, 0, 8, 0},
                {4, 0, 8, 0, 0, 0, 0, 11, 0},
                {0, 8, 0, 7, 0, 4, 0, 0, 2},
                {0, 0, 7, 0, 9, 14, 0, 0, 0},
                {0, 0, 0, 9, 0, 10, 0, 0, 0},
                {0, 0, 4, 0, 10, 0, 2, 0, 0},
                {0, 0, 0, 14, 0, 2, 0, 1, 6},
                {8, 11, 0, 0, 0, 0, 1, 0, 7},
                {0, 0, 2, 0, 0, 0, 6, 7, 0}
        };
        DijkstrasAlgorithm da = new DijkstrasAlgorithm();
        da.dijkstra(adjacencyMatrix, 4, 6);
    }

    public static void test(final Plotter plot)
    {
        ArrayList<DijkstrasAlgorithm.bea> lst = new ArrayList<>();
        lst.add(new DijkstrasAlgorithm.bea(2, 2, "s0"));
        lst.add(new DijkstrasAlgorithm.bea(0, 0, "s1"));
        lst.add(new DijkstrasAlgorithm.bea(0, 4, "s2"));
        lst.add(new DijkstrasAlgorithm.bea(5, 0, "s3"));
        lst.add(new DijkstrasAlgorithm.bea(5, 4, "s4"));
        lst.add(new DijkstrasAlgorithm.bea(2, 6, "s5"));
        DijkstrasAlgorithm da = new DijkstrasAlgorithm();
        da.Init(lst);
        da.dijkstra(0, 5);
        //to plotter
        plot.reset(true);
        plot.invalidate();
        for (AnchorPoint p : da.beacons)
        {
            plot.addAnchorPoint(p, Color.BLUE);
        }
        for (int k = 0; k < da.mPath.size() - 1; ++k)
        {

            AnchorPoint start = da.beacons.get(da.mPath.get(k));
            AnchorPoint end = da.beacons.get(da.mPath.get(k+1));
            if (0 == k)
            {
                plot.addAnchorPoint(start, Color.RED);
            }
            plot.addLine(start, end);
        }
        plot.invalidate();
    }
}
// This code is contributed by Harikrishnan Rajan

