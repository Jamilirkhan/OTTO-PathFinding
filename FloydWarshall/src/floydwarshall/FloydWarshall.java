/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package floydwarshall;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author jamil
 */
/******************************************************************************
 *  Compilation:  javac FloydWarshall.java
 *  Execution:    java FloydWarshall V E
 *  Dependencies: AdjMatrixEdgeWeightedDigraph.java
 *
 *  Floyd-Warshall all-pairs shortest path algorithm.
 *
 *  % java FloydWarshall 100 500
 *
 *  Should check for negative cycles during triple loop; otherwise
 *  intermediate numbers can get exponentially large.
 *  Reference: "The Floyd-Warshall algorithm on graphs with negative cycles"
 *  by Stefan Hougardy
 *
 ******************************************************************************/


/**
 *  The {@code FloydWarshall} class represents a data type for solving the
 *  all-pairs shortest paths problem in edge-weighted digraphs with
 *  no negative cycles.
 *  The edge weights can be positive, negative, or zero.
 *  This class finds either a shortest path between every pair of vertices
 *  or a negative cycle.
 *  <p>
 *  This implementation uses the Floyd-Warshall algorithm.
 *  The constructor takes time proportional to <em>V</em><sup>3</sup> in the
 *  worst case, where <em>V</em> is the number of vertices.
 *  Afterwards, the {@code dist()}, {@code hasPath()}, and {@code hasNegativeCycle()}
 *  methods take constant time; the {@code path()} and {@code negativeCycle()}
 *  method takes time proportional to the number of edges returned.
 *  <p>
 *  For additional documentation,    
 *  see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of    
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class FloydWarshall {
    private boolean hasNegativeCycle;  // is there a negative cycle?
    private double[][] distTo;  // distTo[v][w] = length of shortest v->w path
    private DirectedEdge[][] edgeTo;  // edgeTo[v][w] = last edge on shortest v->w path

    /**
     * Computes a shortest paths tree from each vertex to to every other vertex in
     * the edge-weighted digraph {@code G}. If no such shortest path exists for
     * some pair of vertices, it computes a negative cycle.
     * @param G the edge-weighted digraph
     */
    public FloydWarshall(AdjMatrixEdgeWeightedDigraph G) {
        //this V below is the number of all waypoints including first(0,0) and last (100,100)
        int V = G.V();
        distTo = new double[V][V];
        edgeTo = new DirectedEdge[V][V];

        // initialize distances to infinity
        for (int v = 0; v < V; v++) {
            for (int w = 0; w < V; w++) {
                distTo[v][w] = Double.POSITIVE_INFINITY;
            }
        }

        // initialize distances using edge-weighted digraph's
        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                distTo[e.from()][e.to()] = e.weight();
                edgeTo[e.from()][e.to()] = e;
            }
            // in case of self-loops
            if (distTo[v][v] >= 0.0) {
                distTo[v][v] = 0.0;
                edgeTo[v][v] = null;
            }
        }

        // Floyd-Warshall updates
        for (int i = 0; i < V; i++) {
            // compute shortest paths using only 0, 1, ..., i as intermediate vertices
            for (int v = 0; v < V; v++) {
                if (edgeTo[v][i] == null) continue;  // optimization
                for (int w = 0; w < V; w++) {
                    if (distTo[v][w] > distTo[v][i] + distTo[i][w]) {
                        distTo[v][w] = distTo[v][i] + distTo[i][w];
                        edgeTo[v][w] = edgeTo[i][w];
                    }
                }
                // check for negative cycle
                if (distTo[v][v] < 0.0) {
                    hasNegativeCycle = true;
                    return;
                }
            }
        }
        assert check(G);
    }

    /**
     * Is there a negative cycle?
     * @return {@code true} if there is a negative cycle, and {@code false} otherwise
     */
    public boolean hasNegativeCycle() {
        return hasNegativeCycle;
    }

    /**
     * Returns a negative cycle, or {@code null} if there is no such cycle.
     * @return a negative cycle as an iterable of edges,
     * or {@code null} if there is no such cycle
     */
    public Iterable<DirectedEdge> negativeCycle() {
        for (int v = 0; v < distTo.length; v++) {
            // negative cycle in v's predecessor graph
            if (distTo[v][v] < 0.0) {
                int V = edgeTo.length;
                EdgeWeightedDigraph spt = new EdgeWeightedDigraph(V);
                for (int w = 0; w < V; w++)
                    if (edgeTo[v][w] != null)
                        spt.addEdge(edgeTo[v][w]);
                EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
                assert finder.hasCycle();
                return finder.cycle();
            }
        }
        return null;
    }

    /**
     * Is there a path from the vertex {@code s} to vertex {@code t}?
     * @param s the source vertex
     * @param t the destination vertex
     * @return {@code true} if there is a path from vertex {@code s}
     * to vertex {@code t}, and {@code false} otherwise
     */
    public boolean hasPath(int s, int t) {
        return distTo[s][t] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the length of a shortest path from vertex {@code s} to vertex {@code t}.
     * @param s the source vertex
     * @param t the destination vertex
     * @return the length of a shortest path from vertex {@code s} to vertex {@code t};
     * {@code Double.POSITIVE_INFINITY} if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle
     */
    public double dist(int s, int t) {
        
        double w;
        
        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");
        
        DecimalFormat df = new DecimalFormat("#.###");        
        df.setRoundingMode(df.getRoundingMode().CEILING);
        w=distTo[s][t];
        w=Double.parseDouble(df.format(w));
        
        return w;//distTo[s][t];
    }

    /**
     * Returns a shortest path from vertex {@code s} to vertex {@code t}.
     * @param s the source vertex
     * @param t the destination vertex
     * @return a shortest path from vertex {@code s} to vertex {@code t}
     * as an iterable of edges, and {@code null} if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle
     */
    public Iterable<DirectedEdge> path(int s, int t) {
        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");
        if (!hasPath(s, t)) return null;
        Stack<DirectedEdge> path = new Stack<DirectedEdge>();
        for (DirectedEdge e = edgeTo[s][t]; e != null; e = edgeTo[s][e.from()]) {
            path.push(e);
        }
        return path;
    }

    // check optimality conditions
    private boolean check(AdjMatrixEdgeWeightedDigraph G) {

        // no negative cycle
        if (!hasNegativeCycle()) {
            for (int v = 0; v < G.V(); v++) {
                for (DirectedEdge e : G.adj(v)) {
                    int w = e.to();
                    for (int i = 0; i < G.V(); i++) {
                        if (distTo[i][w] > distTo[i][v] + e.weight()) {
                            System.err.println("edge " + e + " is eligible");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    
    public static void main(String[] args) {
        int V; //number of intermediate waypoints, not including first(0,0) and last (100,100)
       
        int numberOfTestCases=0;
        
        ArrayList outputs = new ArrayList();
        
        try {
            
        VertexList vertices; //waypoints
        AdjMatrixEdgeWeightedDigraph G;
        StdOut.println("Enter the Input in prescribed format");
        
        
        V=(int)Integer.parseInt(StdIn.readLine());
        
        while(V!=0)
        {
            if(!(V>=1 && V<=1000))
                throw new RuntimeException("Number of Waypoints must be 1<=N<=1000 ");
            numberOfTestCases++;
            vertices=new VertexList();            
            vertices.InputVertices(V);
            
            G = new AdjMatrixEdgeWeightedDigraph(V+2);
            
            for(int from=0; from<V+1;from++)
            {
                for(int to=from+1;to<V+2;to++)
                {
                    G.addEdge(new DirectedEdge(from, to,vertices.getWeight(from, to) ));
                }
            }
            
            //  StdOut.println(G);

            // run Floyd-Warshall algorithm
            FloydWarshall spt = new FloydWarshall(G);

            if (spt.hasPath(0, V+1)) {
               // StdOut.printf("%d to %d (%5.2f)  ", 0, V+1, spt.dist(0, V+1));                
                outputs.add(spt.dist(0, V+1));
                //StdOut.printf("%5.3f ", spt.dist(0, V+1));
               // for (DirectedEdge e : spt.path(0, V+1))
                 //   StdOut.print(e + "  ");
            }
            vertices=null;
            G=null;
            V=(int)Integer.parseInt(StdIn.readLine());

        }
        
        for(int i=0;i<numberOfTestCases;i++)
        {
            StdOut.printf("%5.3f ", outputs.get(i));  
            StdOut.println();
        }
        }                
        catch(Exception e)
        { 
            StdOut.printf("Exception --> " + e.getMessage());
        }
        
    }

}
