package ru.matveev.model.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class MatrixCountHelper {

    public static int countEdges(double[][] matrix) {
        int sum = 0;
        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix.length; j++) {
                if (matrix[i][j] > 0) {
                    sum++;
                }
            }
        }
        return sum;
    }

    public static Pair<Integer, Double> findColumnWithMinMedian(double[][] matrix, double minBorder) {
        double min = 10000d;
        int minCol = -1;
        for (int i=0; i<matrix.length; i++) {
            double[] col = MatrixUtils.getColumn(matrix, i);
            double median = countMedian(col);
            if (min > median && median > minBorder) {
                min = median;
                minCol = i;
            }
        }
        return Pair.of(minCol, min);
    }

    public static Pair<Integer, Double> findMin(double[] row, double minBorder) {
        double min = 10000d;
        int minI = -1;
        for (int i=0; i<row.length; i++) {
            if (min > row[i] && row[i] < 1 && row[i] > minBorder) {
                min = row[i];
                minI = i;
            }
        }
        return Pair.of(minI, min);
    }

    public static double countMedian(double[] row) {
        List<Double> sorted =  Arrays.stream(row).filter(d -> d > 0 && d < 1).sorted().boxed().collect(Collectors.toList());
        if (sorted.size() % 2 > 0) {
            return sorted.get(sorted.size() / 2);
        } else {
            return (sorted.get(sorted.size() / 2 - 1) + sorted.get(sorted.size() / 2)) / 2d;
        }
    }

    public static Pair<Integer, Integer> findMinPath(double[][] matrix, double[][] initMatrix) {
        int x = -1;
        int y = -1;
        double min = 1000000d;
        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                double newMin = countIntellectMin(matrix, initMatrix, i, j);
                if (i != j && initMatrix[i][j] == 0 && newMin > 0 && newMin < min) {
                    min = newMin;
                    x = i;
                    y = j;
                }
            }
        }
        return Pair.of(x, y);
    }

    private static double countIntellectMin(double[][] matrix, double[][] initMatrix, int x, int y) {
        return matrix[x][y] + matrix[y][x];// - foundNotZero(initMatrix, x) - foundNotZero(initMatrix, y);
    }

    public static double countCloseness(double[][] matrix) {
        double cMax = 1 / ((double) matrix.length*(matrix.length-1));

        List<Integer>[][] paths = countPaths(matrix);
        int count = 0;
        for (int i=0; i<paths.length; i++) {
            for (int j=0; j<paths.length; j++) {
                if (i != j) {
                    count += paths[i][j].size();
                }
            }
        }

        double c = 1 / (double) count;

        return c/cMax;
    }

    public static double countBetweenness(double[][] matrix) {
        Map<Integer, Integer> betweenness = new HashMap<>();
        List<Integer>[][] paths = countPaths(matrix);
        for (int i=0; i<paths.length; i++) {
            for (int j = 0; j < paths.length; j++) {
                if (i != j && paths[i][j].size() > 1) {
                    for (int k=0; k<paths[i][j].size()-1; k++) {
                        betweenness.merge(paths[i][j].get(k), 1, Integer::sum);
                    }
                }
            }
        }

        log.debug("{}", betweenness);
        return 0;
    }

    public static List<Integer>[][] countPaths(double[][] nearMatrix) {
        List<Integer>[][] paths = new List[nearMatrix.length][nearMatrix.length];

        double[][] matrixFW = MatrixUtils.copyMatrix(nearMatrix);
        for (int i=0; i<matrixFW.length-1; i++) {
            for (int j=i+1; j<matrixFW.length; j++) {
                if (nearMatrix[i][j] > 0) {
                    paths[i][j] = Stream.of(j).collect(Collectors.toList());
                    paths[j][i] = Stream.of(i).collect(Collectors.toList());
                }
            }
        }

        for (int k=0; k<matrixFW.length; k++) {
            for (int i=0; i<matrixFW.length; i++) {
                for (int j=0; j<matrixFW.length; j++) {
                    if (i != k && j != k
                            && matrixFW[i][k] > 0d
                            && matrixFW[k][j] > 0d
                            && matrixFW[i][j] < matrixFW[k][j] * matrixFW[i][k]) {
                        matrixFW[i][j] = matrixFW[k][j] * matrixFW[i][k];
                        paths[i][j] = new ArrayList<>();
                        paths[i][j].addAll(paths[i][k]);
                        paths[i][j].addAll(paths[k][j]);
                    }
                }
            }
        }

        return paths;
    }

    public static double countAMax(double[][] initNearMatrix) {
        double[][] matrixH = countMatrixHMax(initNearMatrix);
        double[][] matrixFW = countMatrixFWMax(initNearMatrix, matrixH);
        setOneToMainDiagonal(matrixFW);
        double dMax = normDistKolmagorovP1(matrixFW);
        return countAvailabilityLinearRight(dMax, initNearMatrix.length);
    }

    public static double[][] countMatrixHMax(double[][] initNearMatrix) {
        double[][] matrixH = MatrixUtils.copyMatrix(initNearMatrix);
        for (int i=0; i<initNearMatrix.length; i++) {
            for (int j=0; j<initNearMatrix[0].length; j++) {
                if (initNearMatrix[i][j] > 0 && initNearMatrix[i][j] < 1) {
                    matrixH[i][j] = j+1;
                } else {
                    matrixH[i][j] = 0;
                }
            }
        }

        return matrixH;
    }

    public static double[][] countMatrixFWMax(double[][] nearMatrix, double[][] matrixH) {
        double[][] matrixFW = MatrixUtils.copyMatrix(nearMatrix);

        for (int k=0; k<matrixFW.length; k++) {
            for (int i=0; i<matrixFW.length; i++) {
                for (int j=0; j<matrixFW.length; j++) {
                    if (i != k && j != k
                            && matrixFW[i][k] > 0d
                            && matrixFW[k][j] > 0d
                            && matrixFW[i][j] < matrixFW[k][j] * matrixFW[i][k]) {
                        matrixFW[i][j] = matrixFW[k][j] * matrixFW[i][k];
                        matrixH[i][j] = k;
                    }
                }
            }
        }

        return matrixFW;
    }

    public static double countAMin(double[][] initNearMatrix) {
        initNearMatrix = clearInitMinMatrix(initNearMatrix);
        double[][] matrixH = countMatrixHMin(initNearMatrix);
        double[][] matrixG = countMatrixGMin(initNearMatrix);
        double[][] matrixFW = countMatrixFWMin(initNearMatrix, matrixH, matrixG);
        setOneToMainDiagonal(matrixFW);
        double dMin = normDistKolmagorovP1(matrixFW);
        return countAvailabilityLinearRight(dMin, initNearMatrix.length);
    }

    public static double[][] clearInitMinMatrix(double[][] matrix) {
        double[][] clearedMatrix = MatrixUtils.copyMatrix(matrix);
        for (int i=0; i<clearedMatrix.length; i++) {
            for (int j = 0; j < clearedMatrix[0].length; j++) {
                if (i == j) {
                    clearedMatrix[i][j] = 0;
                } else if (clearedMatrix[i][j] == 0d) {
                    clearedMatrix[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        return clearedMatrix;
    }

    public static double[][] countMatrixHMin(double[][] initNearMatrix) {
        double[][] matrixH = MatrixUtils.copyMatrix(initNearMatrix);
        for (int i=0; i<initNearMatrix.length; i++) {
            for (int j=0; j<initNearMatrix[0].length; j++) {
                if (initNearMatrix[i][j] != 0d && initNearMatrix[i][j] != 1d && initNearMatrix[i][j] != Double.POSITIVE_INFINITY) {
                    matrixH[i][j] = j+1;
                } else {
                    matrixH[i][j] = 0;
                }
            }
        }
        return matrixH;
    }

    public static double[][] countMatrixGMin(double[][] initNearMatrix) {
        double[][] matrixG = MatrixUtils.copyMatrix(initNearMatrix);
        for (int i=0; i<initNearMatrix.length; i++) {
            for (int j=0; j<initNearMatrix[0].length; j++) {
                if (initNearMatrix[i][j] == 0) {
                    matrixG[i][j] = 0;
                } else if (initNearMatrix[i][j] < Double.POSITIVE_INFINITY) {
                    matrixG[i][j] = 1;
                } else {
                    matrixG[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
        return matrixG;
    }

    public static double[][] countMatrixFWMin(double[][] nearMatrix, double[][] matrixH, double[][] matrixG) {
        double[][] matrixFW = MatrixUtils.copyMatrix(nearMatrix);
        for (int k=0; k<nearMatrix.length; k++) {
            for (int i=0; i<nearMatrix.length; i++) {
                for (int j=0; j<nearMatrix.length; j++) {
                    if (i != k && j != k
                            && matrixG[i][k] < Double.POSITIVE_INFINITY
                            && matrixG[k][j] < Double.POSITIVE_INFINITY
                            && matrixG[i][j] > matrixG[k][j] * matrixG[i][k]) {
                        matrixG[i][j] = matrixG[k][j] * matrixG[i][k];
                        matrixH[i][j] = matrixH[i][k];
                        matrixFW[i][j] = matrixFW[k][j] * matrixFW[i][k];
                    }
                }
            }
        }

        return matrixFW;
    }

    public static double normDistKolmagorovP2(double[][] matrix) {
        double[][] aGlobMatrix = new double[matrix.length][matrix[0].length];
        setOneToAllMatrix(aGlobMatrix);
        double[][] matrixV = MatrixUtils.copyMatrix(aGlobMatrix);
        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrixV[i][j] = aGlobMatrix[i][j] - matrix[i][j];
            }
        }

        double sumI = 0;

        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sumI += (1d / 3d) * matrixV[i][j];
            }
        }

        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (j != matrix.length) {
                    for (int k = 0; k < matrix.length; k++) {
                        if (k> j) {
                            sumI += (1d / 2d) * matrixV[i][j] * matrixV[i][k];
                        }
                    }
                }
            }
        }

        return Math.sqrt(sumI);
    }

    public static double normDistKolmagorovP1(double[][] matrix) {
        double[][] aGlobMatrix = new double[matrix.length][matrix[0].length];
        setOneToAllMatrix(aGlobMatrix);
        double[][] matrixV = MatrixUtils.copyMatrix(aGlobMatrix);
        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrixV[i][j] = aGlobMatrix[i][j] - matrix[i][j];
            }
        }

        double sumI = 0;

        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sumI += (1d / 2d) * matrixV[i][j];
            }
        }

        return sumI;
    }

    public static double[][] getBestRemovingSpanningTree(double[][] matrix) {
        matrix = MatrixUtils.copyMatrix(matrix);
        int spanningTreeSize = matrix.length-1;
        int i = MatrixCountHelper.countEdges(matrix);
        while (i > spanningTreeSize) {
            List<Triple<Integer, Integer,Double>> edges = getPossibleNextEdges(matrix);
            double[][] maxMatrix = null;
            double aMax = -1000;
            for (int j=0; j<edges.size(); j++) {
                double[][] matrix1 = MatrixUtils.copyMatrix(matrix);
                matrix1[edges.get(j).getLeft()][edges.get(j).getMiddle()] = 0;
                matrix1[edges.get(j).getMiddle()][edges.get(j).getLeft()] = 0;
                double currentAmax = MatrixCountHelper.countAMax(matrix1);
                if (currentAmax > aMax) {
                    aMax = currentAmax;
                    maxMatrix = matrix1;
                }
            }
            if (maxMatrix == null) {
                return null;
            }
            matrix = maxMatrix;
            i = MatrixCountHelper.countEdges(matrix);
        }
        return matrix;
    }

    public static double[][] getMaxSpanningTree(double[][] matrix) {
        List<Triple<Integer, Integer,Double>> edges = getPossibleNextEdges(matrix);
        edges.sort(Comparator.comparing((Function<Triple<Integer, Integer, Double>, Double>) Triple::getRight).reversed());
        return getSpanningTree(matrix, edges);
    }

    public static double[][] getSpanningTree(double[][] matrix, List<Triple<Integer, Integer, Double>> possibleNextEdges) {
        double[][] newMatrix = MatrixUtils.copyMatrix(matrix);
        List<Triple<Integer, Integer,Double>> notSpanningTreeEdges = getNotSpanningTreeEdges(possibleNextEdges);
        for (Triple<Integer, Integer,Double> edge: notSpanningTreeEdges) {
            newMatrix[edge.getLeft()][edge.getMiddle()] = 0;
            newMatrix[edge.getMiddle()][edge.getLeft()] = 0;
        }
        return newMatrix;
    }

    public static double[][] getBestSpanningTreeByTries(double[][] matrix, int randomCount) {
        double[][] spanningTree = getMaxSpanningTree(matrix);

        List<Triple<Integer, Integer, Double>> possibleNextEdges = getPossibleNextEdges(matrix);

        double maxAMax = countAMax(spanningTree);
        //0.8282402743390715
        for (int i=0; i<randomCount; i++) {
            List<Triple<Integer, Integer, Double>> currentEdges = new ArrayList<>(possibleNextEdges);
            Collections.shuffle(currentEdges);
            double[][] currentSpanningTree = getSpanningTree(matrix, currentEdges);
            double currentAMax = countAMax(currentSpanningTree);
            if (currentAMax > maxAMax) {
                maxAMax = currentAMax;
                spanningTree = currentSpanningTree;
            }
        }

        return spanningTree;
    }

    private static Pair<ArrayList<Triple<Integer, Integer, Double>>, List<Triple<Integer, Integer, Double>>> countSpanningTree(List<Triple<Integer, Integer,Double>> nextEdges) {
        Map<Integer, Set<Triple<Integer, Integer, Double>>> groups = new HashMap<>();
        Map<Integer, Set<Integer>> groupsVertex = new HashMap<>();

        int nextGroup = 0;

        List<Triple<Integer, Integer,Double>> notSpanningTree = new ArrayList<>();

        for (Triple<Integer, Integer, Double> edge: nextEdges) {
            Integer xgroupNum = groupsVertex.entrySet().stream().filter(entry -> entry.getValue().contains(edge.getLeft())).map(Map.Entry::getKey).findFirst().orElse(null);
            Integer ygroupNum = groupsVertex.entrySet().stream().filter(entry -> entry.getValue().contains(edge.getMiddle())).map(Map.Entry::getKey).findFirst().orElse(null);
            if (xgroupNum == null && ygroupNum == null) {
                nextGroup++;
                groups.put(nextGroup, Stream.of(edge).collect(Collectors.toSet()));
                groupsVertex.put(nextGroup, Stream.of(edge.getLeft(), edge.getMiddle()).collect(Collectors.toSet()));
            } else if (xgroupNum != null && ygroupNum == null) {
                groups.get(xgroupNum).add(edge);
                groupsVertex.get(xgroupNum).addAll(Set.of(edge.getLeft(), edge.getMiddle()));
            } else if (xgroupNum == null && ygroupNum != null) {
                groups.get(ygroupNum).add(edge);
                groupsVertex.get(ygroupNum).addAll(Set.of(edge.getLeft(), edge.getMiddle()));
            } else if (!xgroupNum.equals(ygroupNum)) {
                groups.get(xgroupNum).addAll(groups.get(ygroupNum));
                groups.remove(ygroupNum);
                groupsVertex.get(xgroupNum).addAll(groupsVertex.get(ygroupNum));
                groupsVertex.remove(ygroupNum);
            } else if (xgroupNum.equals(ygroupNum)) {
                notSpanningTree.add(edge);
            }
        }

        Set<Triple<Integer, Integer, Double>> spanningTreeEdges = groups.values().stream().findFirst().orElse(null);

        return Pair.of(new ArrayList<>(spanningTreeEdges), notSpanningTree);
    }

    private static List<Triple<Integer, Integer,Double>> getPossibleNextEdges(double[][] matrix) {
        List<Triple<Integer, Integer,Double>> edges = new ArrayList<>();
        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix[0].length; j++) {
                if (matrix[i][j] > 0) {
                    edges.add(Triple.of(i, j, matrix[i][j] + matrix[j][i]));
                }
            }
        }
        return edges;
    }

    public static List<Triple<Integer, Integer,Double>> getNotSpanningTreeEdges(List<Triple<Integer, Integer,Double>> edges) {
        return countSpanningTree(edges).getRight();
    }

    private static void setOneToAllMatrix(double[][] matrix) {
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                matrix[i][j] = 1;
            }
        }
    }

    private static void setOneToMainDiagonal(double[][] matrix) {
        for (int i=0; i<matrix.length; i++) {
            matrix[i][i] = 1;
        }
    }

    public static void setZeroToMainDiagonal(double[][] matrix) {
        for (int i=0; i<matrix.length; i++) {
            matrix[i][i] = 0;
        }
    }

    private static double countDSmall(double length) {
        return -0.000305962 * Math.pow(length, 3) + 0.0628791 * Math.pow(length, 2) + 1.20093 * length - 1.8333;
    }

    private static double countDBig(double length) {
        return 3.38745 * Math.pow(10, -6) * Math.pow(length, 3) + 0.0140894 * Math.pow(length, 2) + 5.2436 * length - 146.096;
    }

    private static double countDMax(int length) {
        double[][] zeroMatrix = new double[length][length];
        return normDistKolmagorovP1(zeroMatrix);
    }

    private static double countAvailabilityExpRight(double d, int length) {
        double dMax = countDMax(length);
        return Math.pow(Math.E, (-d / dMax));
    }

    private static double countAvailabilitySqrtRight(double d, int length) {
        double dMax = countDMax(length);
        return Math.sqrt(1 - Math.pow(d/dMax, 2));
    }

    public static double countAvailabilityLinearRight(double d, int length) {
        double dMax = countDMax(length);
        return 1 - (d/dMax);
    }

    private static double countAvailabilityExp(double d, double length) {
        if (length < 50) {
            double dSmall = countDSmall(length);
            return Math.pow(Math.E, (-d / dSmall));
        } else {
            double dBig = countDBig(length);
            return Math.pow(Math.E, (-d / dBig));
        }
    }

    private static double countAvailabilityLinear(double d, double length) {
        if (length < 50) {
            double dSmall = countDSmall(length);
            return d/dSmall;
        } else {
            double dBig = countDBig(length);
            return d/dBig;
        }
    }

}
