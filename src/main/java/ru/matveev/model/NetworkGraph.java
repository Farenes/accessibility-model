package ru.matveev.model;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.List;
import java.util.Random;

public class NetworkGraph {

    private static final Random rand = new Random();

    private List<Switcher> switchers;
    private Graph<Switcher, AccessEdge> graph;

    public NetworkGraph(List<Switcher> switchers, double[][] matrix) {
        this.switchers = switchers;
        graph = new DefaultDirectedWeightedGraph<>(AccessEdge.class);

        switchers.forEach(graph::addVertex);

        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                if (matrix[i][j] > 0) {
                    graph.addEdge(switchers.get(i), switchers.get(j), new AccessEdge(matrix[i][j]));
                }
            }
        }
    }

    public List<Switcher> getPath(Switcher from, Switcher to) {
        FloydWarshallShortestPaths<Switcher, AccessEdge> path = new FloydWarshallShortestPaths<>(graph);
        GraphPath<Switcher, AccessEdge> path1 = path.getPath(from, to);
        return path1.getVertexList();
    }

    public Switcher getRandSwitcher(Switcher notThis) {
        Switcher sw = switchers.get(rand.nextInt(switchers.size()));
        while (notThis.equals(sw)) {
            sw = switchers.get(rand.nextInt(switchers.size()));
        }
        return sw;
    }

}
