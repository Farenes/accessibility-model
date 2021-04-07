package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MatrixHelper {

    private static final Random RANDOM = new Random();

    public static double[][] addRandomEdge(double[][] matrix) {
        double[][] newMatrix = copyMatrix(matrix);
        int firstPoint = 0;
        int secondPoint = 0;
        while (firstPoint == secondPoint && newMatrix[firstPoint][secondPoint] > 0d) {
            firstPoint = RANDOM.nextInt(matrix.length);
            secondPoint = RANDOM.nextInt(matrix.length);
        }

        double firstAcc = Arrays.stream(newMatrix[firstPoint]).filter(d -> d > 0 && d < 1).average().orElse(0d);
        double secondAcc = Arrays.stream(newMatrix[secondPoint]).filter(d -> d > 0 && d < 1).average().orElse(0d);
        newMatrix[firstPoint][secondPoint] = firstAcc;
        newMatrix[secondPoint][firstPoint] = secondAcc;

        return newMatrix;
    }

    public static double[][] addIntellectEdge(double[][] matrix, double[][] initMatrix) {
        double[][] newMatrix = copyMatrix(initMatrix);
        int x = 0;
        int y = 0;
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
        if (y == 0 && x == 0) {
            throw new IllegalArgumentException();
        }
        newMatrix[x][y] = foundNotZero(initMatrix, x);
        newMatrix[y][x] = foundNotZero(initMatrix, y);
        return newMatrix;
    }

    private static double countIntellectMin(double[][] matrix, double[][] initMatrix, int x, int y) {
        return matrix[x][y] + matrix[y][x];// - foundNotZero(initMatrix, x) - foundNotZero(initMatrix, y);
    }

    private static double foundNotZero(double[][] initMatrix, int line) {
        return Arrays.stream(initMatrix[line]).filter(d -> d < 1 && d > 0).findFirst().orElse(0d);
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
        access = access <= 0 ? countMediana(getCol(matrix, newPointConnection)) : access;
        newMatrix[matrix.length][newPointConnection] = access;
        newMatrix[newPointConnection][matrix.length] = foundNotZero(matrix, newPointConnection);

        return newMatrix;
    }

    public static double[][] addOneRandomEdge(double[][] matrix) {
        double[][] newMatrix = copyMatrix(matrix);
        int firstPoint = RANDOM.nextInt(newMatrix.length);
        int secondPoint = RANDOM.nextInt(newMatrix.length);
        while (firstPoint == secondPoint || newMatrix[firstPoint][secondPoint] > 0) {
            firstPoint = RANDOM.nextInt(newMatrix.length);
            secondPoint = RANDOM.nextInt(newMatrix.length);
        }
        newMatrix[firstPoint][secondPoint] = foundNotZero(matrix, firstPoint);
        newMatrix[secondPoint][firstPoint] = foundNotZero(matrix, secondPoint);

        return newMatrix;
    }

    private static double[] getCol(double[][] matrix, int y) {
        double[] col = new double[matrix.length];
        for (int i=0; i<matrix.length; i++) {
            col[i] = matrix[i][y];
        }
        return col;
    }

    private static double countMediana(double[] row) {
        List<Double> sorted =  Arrays.stream(row).filter(d -> d > 0 && d < 1).sorted().boxed().collect(Collectors.toList());
        if (sorted.size() % 2 > 0) {
            return sorted.get(sorted.size() / 2);
        } else {
            return (sorted.get(sorted.size() / 2 - 1) + sorted.get(sorted.size() / 2)) / 2d;
        }
    }

    public static double[][] addRandomPoint(double[][] matrix, int newPoints) {
        double[][] newMatrix = new double[matrix.length+1][matrix[0].length+1];
        for (int i=0; i<matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }

        int countNewEdges = RANDOM.nextInt(newPoints) + 1;

        Set<Integer> linkedPoints = new HashSet<>();

        while (linkedPoints.size() < countNewEdges) {
            linkedPoints.add(RANDOM.nextInt(matrix.length));
        }

        double newPointAccessibility = 0.1d;

        linkedPoints.forEach(p -> {
            newMatrix[matrix.length][p] = newPointAccessibility;
            newMatrix[p][matrix.length] = newPointAccessibility;
        });
        newMatrix[matrix.length][matrix.length] = 1d;

        ////log.debug(matrixStr(newMatrix));
        return newMatrix;
    }

    public static double countAMin(double[][] initNearMatrix) {
        double[][] matrixH = countMatrixHMin(initNearMatrix);
        double[][] matrixFW = countMatrixFWMin(initNearMatrix, matrixH);
        setOneToMainDiagonal(matrixFW);
        double dMin = normDistKolmagorov(matrixFW);
        double aMin = countAvailability(dMin, initNearMatrix.length);
        return aMin;
    }

    public static double countAMax(double[][] initNearMatrix) {
        initNearMatrix = clearMaxMatrix(initNearMatrix);
        double[][] matrixH = countMatrixHMax(initNearMatrix);
        double[][] matrixG = countMatrixGMax(initNearMatrix);
        double[][] matrixFW = countMatrixFWMax(initNearMatrix, matrixH, matrixG);
        setOneToMainDiagonal(matrixFW);
        double dMax = normDistKolmagorov(matrixFW);
        double aMax = countAvailability(dMax, initNearMatrix.length);
        return aMax;
    }

    private static double[][] clearMaxMatrix(double[][] matrix) {
        double[][] clearedMatrix = copyMatrix(matrix);
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
        double[][] matrixH = copyMatrix(initNearMatrix);
        for (int i=0; i<initNearMatrix.length; i++) {
            for (int j=0; j<initNearMatrix[0].length; j++) {
                if (initNearMatrix[i][j] > 0 && initNearMatrix[i][j] < 1) {
                    matrixH[i][j] = j+1;
                } else {
                    matrixH[i][j] = 0;
                }
            }
        }
        //////log.debug("matrixHMin: {}", matrixStr(matrixH));
        return matrixH;
    }

    public static double[][] countMatrixFWMin(double[][] nearMatrix, double[][] matrixH) {
        double[][] matrixFW = copyMatrix(nearMatrix);
        //////log.debug("Init matrix: {}", matrixStr(nearMatrix));
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

        //log.debug("matrixFWMin: {}", matrixStr(matrixFW));
        return matrixFW;
    }

    private static double[][] countMatrixHMax(double[][] initNearMatrix) {
        double[][] matrixH = copyMatrix(initNearMatrix);
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

    private static double[][] countMatrixGMax(double[][] initNearMatrix) {
        double[][] matrixG = copyMatrix(initNearMatrix);
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

    private static double[][] countMatrixFWMax(double[][] nearMatrix, double[][] matrixH, double[][] matrixG) {
        double[][] matrixFW = copyMatrix(nearMatrix);
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
        //log.debug("matrixFWMax: {}", matrixStr(matrixFW));
        return matrixFW;
    }

    private static double normDistKolmagorov(double[][] matrix) {
        double[][] aGlobMatrix = new double[matrix.length][matrix[0].length];
        setOneToAllMatrix(aGlobMatrix);
        double[][] matrixV = copyMatrix(aGlobMatrix);
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

    public static double[][] copyMatrix(double[][] matrix) {
        double[][] result = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return result;
    }

    private static double countDSmall(double length) {
        return -0.000305962 * Math.pow(length, 3) + 0.0628791 * Math.pow(length, 2) + 1.20093 * length - 1.8333;
    }

    private static double countDBig(double length) {
        return 3.38745 * Math.pow(10, -6) * Math.pow(length, 3) + 0.0140894 * Math.pow(length, 2) + 5.2436 * length - 146.096;
    }

    private static double countAvailability(double d, double length) {
        if (length < 50) {
            double dSmall = countDSmall(length);
            ////log.debug("dSmall: {}", dSmall);
            return Math.pow(Math.E, (-d / dSmall));
        } else {
            double dBig = countDBig(length);
            return Math.pow(Math.E, (-d / dBig));
        }
    }

    public static String matrixStr(double[][] matrix) {
        StringBuilder s = new StringBuilder("\n");
        for (double[] doubles : matrix) {
            s.append(Arrays.stream(doubles).mapToObj(v -> String.format("%.2f", v)).collect(Collectors.joining(" "))).append("\n");
        }
        return s.toString();
    }

    public static int countEdges(double[][] matrix) {
        int edges = 0;
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                if (matrix[i][j] > 0 && matrix[i][j] < 1) {
                    edges += 1;
                }
            }
        }
        return edges / 2;
    }

    public static boolean existsZeros(double[][] matrix) {
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                if (matrix[i][j] <= 0d) {
                    return true;
                }
            }
        }
        return false;
    }

    public static double[][] generateMatrix(int points, int edges, double minAccess, double maxAccess) {
        double accessPoint1 = (maxAccess - minAccess) * RANDOM.nextDouble() + minAccess;
        double accessPoint2 = (maxAccess - minAccess) * RANDOM.nextDouble() + minAccess;
        double[][] matrix = new double[][] {{1, accessPoint1}, {accessPoint2, 1}};
        if (points <= 2) {
            return matrix;
        }
        for (int i=0; i<points-2; i++) {
            matrix = addOneRandomPoint(matrix, (maxAccess - minAccess) * RANDOM.nextDouble() + minAccess);
        }
        edges = Math.min(edges, matrix.length*(matrix.length-1));
        int currentEdges = countEdges(matrix) / 2;
        edges = edges - currentEdges;
        for (int i=0; i<edges; i++) {
            matrix = addOneRandomEdge(matrix);
        }
        return matrix;
    }

}
