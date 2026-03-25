package org.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

public class Main {

    //tried also doing more but this either overflows the java heap (5000) or is just very, very slow (2000)
    static int[] vertexNumbers = {100, 500, 1000};
    public static void main(String[] args) {
        ArrayList<Double> estimates = new ArrayList<>();
        for (int nOfVerticies : vertexNumbers) {

            //iterated approximation -- we will 'fail' rc if it's corresponding r doesn't connect 95% of random graphs
            double maxRc = Math.sqrt(2*nOfVerticies); // maximal as here all edges should be connected
            double minRc = 0;

            while (maxRc - minRc > 0.0001) {

                double EstimateRc = (maxRc + minRc) / 2;
                double r = EstimateRc / (Math.sqrt(nOfVerticies));
                int failCount = 0;

                for (int i = 0; i < 100; i++) {
                    Graph<EuclidPair, DefaultEdge> graph = constructRandomGraph(nOfVerticies, r);

                    if(!GraphTests.isConnected(graph)) {
                        failCount++;
                    }
                }

                if(failCount > 5) {
                    //our current r is too small, fails too often (> 5%)
                    minRc = EstimateRc;
                } else {
                    //This rc connects often, but could it be smaller?
                    maxRc = EstimateRc;
                }
            }
            estimates.add(maxRc);
            System.out.println(nOfVerticies + ": \n 95% confidence of connection:" + maxRc);
        }

        //Find average estimate
        double sum = 0;
        for (Double d : estimates) {
            sum += d;
        }
        double average = sum/3;
        System.out.println("Final estimate: " + average);



    }


    static Graph<EuclidPair, DefaultEdge> constructRandomGraph(int n, double distance) {
        Graph<EuclidPair, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        //make n random points in [0,1]^2:
        for (int i = 0; i < n; i++) {
            double x = Math.random();
            double y =Math.random();

            EuclidPair pair = new EuclidPair(x, y);

            graph.addVertex(pair);
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