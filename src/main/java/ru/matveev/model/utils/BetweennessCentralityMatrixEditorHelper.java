package ru.matveev.model.utils;

import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.Random;

public class BetweennessCentralityMatrixEditorHelper {

    private static final Random RANDOM = new Random();

    public static double[][] generateMatrix(int points, int edges, double minAccess, double maxAccess) {
        double[][] matrix = null;
        do {
            double accessPoint1 = (maxAccess - minAccess) * RANDOM.nextDouble() + minAccess;
            double accessPoint2 = (maxAccess - minAccess) * RANDOM.nextDouble() + minAccess;
            matrix = new double[][]{{1, accessPoint1}, {accessPoint2, 1}};
            if (points <= 2) {
                return matrix;
            }
            for (int i = 0; i < points - 2; i++) {
                matrix = addOneRandomPoint(matrix, (maxAccess - minAccess) * RANDOM.nextDouble() + minAccess);
            }
            edges = Math.min(edges, matrix.length * (matrix.length - 1));
            int currentEdges = MatrixCountHelper.countEdges(matrix);
            edges = edges - currentEdges;
            for (int i = 0; i < edges; i++) {
                matrix = addOneRandomEdge(matrix);
            }
        } while (!MatrixUtils.isConnected(matrix) || !isBetweennessCentralityCorrect(getGraphFromMatrix((matrix))));
        return matrix;
    }

    public static double[][] addOneRandomPoint(double[][] matrix, double access) {
        double[][] newMatrix = new double[matrix.length+1][matrix[0].length+1];
        newMatrix[matrix.length][matrix.length] = 1;
        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }

        int newPointConnection = RANDOM.nextInt(matrix.length);
        access = access <= 0 ? MatrixCountHelper.countMedian(MatrixUtils.getColumn(matrix, newPointConnection)) : access;
        newMatrix[matrix.length][newPointConnection] = access;
        newMatrix[newPointConnection][matrix.length] = MatrixUtils.foundAnyRowValue(matrix, newPointConnection);

        return newMatrix;
    }

    public static double[][] addIntellectEdge(double[][] matrix, double[][] initMatrix) {
        double[][] newMatrix = MatrixUtils.copyMatrix(initMatrix);
        Pair<Integer, Integer> minPathResult = MatrixCountHelper.findMinPath(matrix, initMatrix);
        int x = minPathResult.getLeft();
        int y = minPathResult.getRight();
        if (y == 0 && x == 0) {
            throw new IllegalArgumentException();
        }
        newMatrix[x][y] = MatrixUtils.foundAnyRowValue(initMatrix, x);
        newMatrix[y][x] = MatrixUtils.foundAnyRowValue(initMatrix, y);
        return newMatrix;
    }

    public static double[][] addOneRandomEdge(double[][] matrix) {
        double[][] newMatrix = MatrixUtils.copyMatrix(matrix);
        int firstPoint = RANDOM.nextInt(newMatrix.length);
        int secondPoint = RANDOM.nextInt(newMatrix.length);
        while (firstPoint == secondPoint || newMatrix[firstPoint][secondPoint] > 0) {
            firstPoint = RANDOM.nextInt(newMatrix.length);
            secondPoint = RANDOM.nextInt(newMatrix.length);
        }
        newMatrix[firstPoint][secondPoint] = MatrixUtils.foundAnyRowValue(matrix, firstPoint);
        newMatrix[secondPoint][firstPoint] = MatrixUtils.foundAnyRowValue(matrix, secondPoint);

        return newMatrix;
    }

    public static double[][] removeOneRandomEdge(double[][] matrix) {
        int firstPoint = RANDOM.nextInt(matrix.length);
        int secondPoint = RANDOM.nextInt(matrix.length);

        while (firstPoint == secondPoint || matrix[firstPoint][secondPoint] <= 0 || matrix[firstPoint][secondPoint] >= 1) {
            firstPoint = RANDOM.nextInt(matrix.length);
            secondPoint = RANDOM.nextInt(matrix.length);
        }

        return removeOneEdge(matrix, firstPoint, secondPoint);
    }

    public static double[][] removeOneEdge(double[][] matrix, int p1, int p2) {
        double[][] newMatrix = MatrixUtils.copyMatrix(matrix);

        newMatrix[p1][p2] = 0;
        newMatrix[p2][p1] = 0;

        return newMatrix;
    }

    public static double[][] removeOneRandomPoint(double[][] matrix) {
        double[][] newMatrix = new double[matrix.length-1][matrix[0].length-1];

        int deletedPoint = RANDOM.nextInt(newMatrix.length);

        int x=0;
        for (int i=0; i<matrix.length; i++) {
            if (i == deletedPoint) {
                continue;
            }
            int y = 0;
            for (int j=0; j<matrix.length; j++) {
                if (j == deletedPoint) {
                    continue;
                }
                newMatrix[y][x] = matrix[j][i];
                y++;
            }
            x++;
        }

        return newMatrix;
    }

    private static Graph<String, DefaultEdge> getGraphFromMatrix(double[][] matrix) {
        Graph<String, DefaultEdge> graph = createStringGraph(matrix.length);
        addEdgesToGraph(graph, matrix);

        return graph;
    }

    private static Graph<String, DefaultEdge> createStringGraph( int length)
    {
        Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        for (int i = 0; i < length; i++ ) {
            g.addVertex(String.valueOf(i));
        }
        return g;
    }

    private static Graph<String, DefaultEdge> addEdgesToGraph (Graph<String, DefaultEdge> g, double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] > 0) {
                    g.addEdge(String.valueOf(i), String.valueOf(j));
                }
            }
        }
        return g;
    }

    private static boolean isBetweennessCentralityCorrect (Graph<String, DefaultEdge> g) {
        BetweennessCentrality bc = new BetweennessCentrality(g);
        Map<String, Double> score = bc.getScores();

        return score == null;
    }
}
