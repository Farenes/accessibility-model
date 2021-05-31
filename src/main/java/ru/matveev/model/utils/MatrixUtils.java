package ru.matveev.model.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MatrixUtils {

    public static double foundAnyRowValue(double[][] matrix, int row) {
        return Arrays.stream(matrix[row]).filter(d -> d < 1 && d > 0).findFirst().orElse(0d);
    }

    public static double[] getColumn(double[][] matrix, int y) {
        double[] col = new double[matrix.length];
        for (int i=0; i<matrix.length; i++) {
            col[i] = matrix[i][y];
        }
        return col;
    }

    public static String print(double[][] matrix) {
        StringBuilder s = new StringBuilder("\n");
        for (double[] doubles : matrix) {
            s.append(Arrays.stream(doubles).mapToObj(v -> String.format("%.2f", v)).collect(Collectors.joining(" "))).append("\n");
        }
        return s.toString();
    }

    public static String printForPaste(double[][] matrix) {
        StringBuilder s = new StringBuilder("\n{");
        String result = Arrays.stream(matrix).map(doubles -> {
            String strArr = Arrays.stream(doubles).mapToObj(v -> String.format("%.2f", v).replace(",", ".")).collect(Collectors.joining(", "));
            return String.format("{%s}", strArr);
        }).collect(Collectors.joining(",\n"));
        s.append(result);
        s.append("}");
        return s.toString();
    }

    public static boolean isFullyConnected(double[][] matrix) {
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                if (matrix[i][j] == 0d) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double[][] copyMatrix(double[][] matrix) {
        double[][] result = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return result;
    }

    public static boolean isConnected(double[][] matrix) {
        double[][] matrixH = MatrixCountHelper.countMatrixHMax(matrix);
        double[][] filledMatrix = MatrixCountHelper.countMatrixFWMax(matrix, matrixH);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (filledMatrix[i][j] == 0d) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void toGephiFormat(double[][] matrix, String vertexesFile, String edgesFile) throws IOException {
        File vertexes = new File(vertexesFile);
        File edges = new File(edgesFile);
        BufferedWriter vertexesWriter = new BufferedWriter(new FileWriter(vertexes));
        vertexesWriter.write("id;label\n");
        for (int i=0; i<matrix.length; i++) {
            vertexesWriter.write((i+1) + ";" + (i+1) + "\n");
        }
        vertexesWriter.flush();
        vertexesWriter.close();

        BufferedWriter edgesWriter = new BufferedWriter(new FileWriter(edges));
        edgesWriter.write("Source;Target;Label;Weight\n");
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix.length; j++) {
                if (i != j && matrix[i][j] > 0) {
                    String w = String.format("%.2f", matrix[i][j]).replace(",", ".");
                    edgesWriter.write((i+1) + ";" + (j+1) + ";" + w + ";" + w + "\n");
                }
            }
        }
        edgesWriter.flush();
        edgesWriter.close();
    }

}
