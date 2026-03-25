package org.example;
import java.util.ArrayList;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

public class Main {
    static int[] vertexNumbers = {100, 500, 1000, 5000};
    static int MAX_FAIL_PERCENT = 75;
    public static void main(String[] args) {
        ArrayList<Double> estimates = new ArrayList<>();
        for (int nOfVerticies : vertexNumbers) {

            //iterated approximation -- we will 'fail' rc if it's corresponding r doesn't connect 25% of random graphs
            //In almost all random cases we will have r_c < 3, and when I had maxRc set to a much larger number (namely sqrt(2) times sqrt(n), which makes total graph)
            //to guarantee correctness, I found that this contains most of the computational time, so I changed it slightly.
            double maxRc = 3;
            double minRc = 0;

            while (maxRc - minRc > 0.0001) {

                //we will test the middle point of our bounds
                double estimateRc = (maxRc + minRc) / 2;
                double r = estimateRc / (Math.sqrt(nOfVerticies));
                int failCount = 0;

                for (int i = 0; i < 100; i++) {
                    Graph<EuclidPair, DefaultEdge> graph = constructRandomGraph(nOfVerticies, r);

                    if(!GraphTests.isConnected(graph)) {
                        failCount++;
                        if(failCount > MAX_FAIL_PERCENT) {
                            //no point checking further
                            break;
                        }
                    }
                }

                if(failCount > MAX_FAIL_PERCENT) {
                    //our current r is too small, fails too often
                    minRc = estimateRc;
                } else {
                    //This rc connects often, but could it be smaller?
                    maxRc = estimateRc;
                }
            }
            estimates.add(maxRc);
            System.out.println(nOfVerticies + ": \n 25% confidence of connection:" + maxRc);
        }

        //Find average estimate
        double sum = 0;
        for (Double d : estimates) {
            sum += d;
        }
        double average = sum/4;
        System.out.println("Final estimate: " + average);



    }


    /**
     * This is like the helper function from the pseudocode:
     * It makes a random graph of n vertices, with edges if they are within some distance
     * **/
    static Graph<EuclidPair, DefaultEdge> constructRandomGraph(int n, double distance) {
        Graph<EuclidPair, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        //make n random points in [0,1]^2:
        for (int i = 0; i < n; i++) {
            double x = Math.random();
            double y = Math.random();

            EuclidPair pair = new EuclidPair(x, y);

            //add random vertex to graph
            graph.addVertex(pair);

            //Add edges to this new vertex
            for (EuclidPair otherVertex : graph.vertexSet()) {
                if(pair.distanceTo(otherVertex) < distance) {
                    graph.addEdge(pair, otherVertex);
                }
            }

        }
        return graph;
    }

    private static class EuclidPair {
        double x;
        double y;

        public EuclidPair(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public double distanceTo(EuclidPair other) {
            return Math.sqrt((x-other.x)*(x - other.x) + (y-other.y)*(y - other.y));
        }
    }
}