package org.example;
import java.util.ArrayList;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

public class Main {

    //tried also doing more but this either overflows the java heap (5000) or is just very, very slow (2000)
    static int[] vertexNumbers = {100, 500, 1000};
    public static void main(String[] args) {
        ArrayList<Double> estimates = new ArrayList<>();
        for (int nOfVerticies : vertexNumbers) {

            //iterated approximation -- we will 'fail' rc if it's corresponding r doesn't connect 25% of random graphs
            double maxRc = Math.sqrt(2*nOfVerticies); // maximal as here all edges should be connected
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
                    }
                }

                if(failCount > 75) {
                    //our current r is too small, fails too often (> 75%)
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
        double average = sum/3;
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