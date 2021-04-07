package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class MatrixHelperTest {

    private Random rand = new Random();

    private double[][] matrix = new double[][]{
            {1.00, 0.95, 0.92, 0.00, 0.00, 0.97},
            {0.98, 1.00, 0.98, 0.00, 0.00, 0.00},
            {0.95, 0.00, 1.00, 0.95, 0.00, 0.00},
            {0.00, 0.00, 0.90, 1.00, 0.94, 0.00},
            {0.00, 0.00, 0.00, 0.99, 1.00, 0.00},
            {0.90, 0.00, 0.00, 0.00, 0.90, 1.00}
    };

    private double[][] matrix1 = new double[][]{
            {1.00, 0.95, 0.95, 0.00, 0.00, 0.95},
            {0.98, 1.00, 0.98, 0.00, 0.00, 0.00},
            {0.94, 0.94, 1.00, 0.94, 0.94, 0.00},
            {0.00, 0.00, 0.90, 1.00, 0.90, 0.00},
            {0.00, 0.00, 0.99, 0.99, 1.00, 0.99},
            {0.90, 0.00, 0.00, 0.00, 0.90, 1.00}
    };

    private double[][] matrix2 = new double[][] {
            {1.0000, 0.8672, 0.0000, 0.0000, 0.0000, 0.8672, 0.0000, 0.0000, 0.0000, 0.8672, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.8946, 1.0000, 0.8946, 0.8946, 0.0000, 0.8946, 0.8946, 0.0000, 0.8946, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.8871, 1.0000, 0.8871, 0.0000, 0.0000, 0.8871, 0.0000, 0.0000, 0.0000, 0.8871, 0.8871, 0.8871, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.8791, 0.8791, 1.0000, 0.8791, 0.8791, 0.0000, 0.0000, 0.0000, 0.0000, 0.8791, 0.0000, 0.8791, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.0000, 0.7953, 1.0000, 0.0000, 0.0000, 0.7953, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.7953, 0.7953, 0.7953},
            {0.9236, 0.9236, 0.0000, 0.9236, 0.0000, 1.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.9302, 0.9302, 0.0000, 0.0000, 0.0000, 1.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.9302, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.0000, 0.0000, 0.9331, 0.0000, 0.0000, 1.0000, 0.0000, 0.9331, 0.0000, 0.0000, 0.0000, 0.9331, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.9681, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.9775, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.9775, 0.0000, 1.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.9775, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.8999, 0.8999, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000, 0.0000, 0.0000, 0.0000, 0.8999, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.9493, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.9250, 0.9250, 0.0000, 0.0000, 0.9250, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000, 0.0000, 0.9250, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.8654, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000, 0.8654, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.0000, 0.0000, 0.9093, 0.0000, 0.0000, 0.0000, 0.0000, 0.9093, 0.9093, 0.0000, 0.9093, 0.9093, 1.0000, 0.0000, 0.0000},
            {0.0000, 0.0000, 0.0000, 0.0000, 0.8642, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000, 0.0000},
            {0.0000, 0.0000, 0.0000, 0.0000, 0.9713, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 1.0000}
    };

    @Test
    public void testPrint() {
        log.debug(MatrixHelper.matrixStr(matrix2));
    }

    @Test
    public void testGenerateMatrix() {
        double[][] matrix = MatrixHelper.generateMatrix(10, 20, 0.7d, 0.9d);
        log.debug(MatrixHelper.matrixStr(matrix));
        Assert.assertEquals(10, matrix.length);
        int edges = 0;
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix.length; j++) {
                if (i == j && matrix[i][j] < 1) {
                    Assert.fail();
                } else if (i != j && matrix[i][j] > 0 && (matrix[i][j] < 0.7d || matrix[i][j] > 0.9d)) {
                    Assert.fail();
                } else if (i != j && matrix[i][j] > 0) {
                    edges++;
                }
            }
        }
        Assert.assertEquals(20, edges / 2);
    }

    @Test
    public void test() throws IOException {
        new ExperimentModel().experiment001();
    }

    @Test
    public void test2() throws IOException {
        new ExperimentModel().experiment002();
    }

    @Test
    public void testCountAccesibility() {


        double aMin = MatrixHelper.countAMin(matrix1);
        double aMax = MatrixHelper.countAMax(matrix1);
        log.debug("Amin = {}", aMin); //0.8734
        log.debug("Amax = {}", aMax); //0.9164
        //0.8599113624379323
        //0.85493776825633
    }

    @Test
    public void testPng() {
        MatrixHelper.addRandomPoint(matrix, 3);
    }

    @Test
    public void testPng2() throws IOException {
        double[][] resultMatrix = MatrixHelper.copyMatrix(matrix);
        Map<Double, Double> aMaxResult = new HashMap<>();
        Map<Double, Double> aMinResult = new HashMap<>();
        aMaxResult.put((double) resultMatrix.length, MatrixHelper.countAMax(resultMatrix));
        aMinResult.put((double) resultMatrix.length, MatrixHelper.countAMin(resultMatrix));
        for (int i=0; i<100; i++) {
            resultMatrix = MatrixHelper.addRandomPoint(resultMatrix, 1);
            aMaxResult.put((double) resultMatrix.length, MatrixHelper.countAMax(resultMatrix));
            aMinResult.put((double) resultMatrix.length, MatrixHelper.countAMin(resultMatrix));
        }
        ChartHelper.saveChart(aMinResult, aMaxResult, "result");
    }

    @Test
    public void testPng3() throws IOException {
        double[][] resultMatrix = MatrixHelper.copyMatrix(matrix1);
        for (int i=0; i<15; i++) {
            resultMatrix = MatrixHelper.addRandomPoint(resultMatrix, 1);
        }

        Map<Double, Double> aMaxResult = new HashMap<>();
        Map<Double, Double> aMinResult = new HashMap<>();

        aMaxResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMax(resultMatrix));
        aMinResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMin(resultMatrix));
        while (MatrixHelper.existsZeros(resultMatrix)) {
            resultMatrix = MatrixHelper.addRandomEdge(resultMatrix);
            aMaxResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMax(resultMatrix));
            aMinResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMin(resultMatrix));
        }
//        for (int i=0; i<200; i++) {
//            resultMatrix = MatrixHelper.addRandomEdge(resultMatrix);
//            aMaxResult.put(MatrixHelper.countEdges(resultMatrix), MatrixHelper.countAMax(resultMatrix));
//            aMinResult.put(MatrixHelper.countEdges(resultMatrix), MatrixHelper.countAMin(resultMatrix));
//        }

        ChartHelper.saveChart(aMinResult, aMaxResult, "result");
    }


    @Test
    public void testPng4() throws IOException {
        double[][] resultMatrix = MatrixHelper.copyMatrix(matrix1);
        for (int i=0; i<15; i++) {
            resultMatrix = MatrixHelper.addRandomPoint(resultMatrix, 1);
        }

        Map<Double, Double> aMaxResult = new HashMap<>();
        Map<Double, Double> aMinResult = new HashMap<>();

        aMaxResult.put(0d, MatrixHelper.countAMax(resultMatrix));
        aMinResult.put(0d, MatrixHelper.countAMin(resultMatrix));
        for (int i=1; i<200; i++) {
            resultMatrix = makeMove(resultMatrix);
            aMaxResult.put((double)i, MatrixHelper.countAMax(resultMatrix));
            aMinResult.put((double)i, MatrixHelper.countAMin(resultMatrix));
        }

        ChartHelper.saveChart(aMinResult, aMaxResult, "result");
    }

    private double[][] makeMove(double[][] matrix) {
        if (rand.nextDouble() > 0.5) {
            return MatrixHelper.addRandomEdge(matrix);
        } else {
            return MatrixHelper.addRandomPoint(matrix, 1);
        }
    }

    @Test
    public void testPng41() throws IOException {
        double[][] resultMatrix = MatrixHelper.copyMatrix(matrix1);
        for (int i=0; i<15; i++) {
            resultMatrix = MatrixHelper.addRandomPoint(resultMatrix, 1);
        }

        Map<Double, Double> aMaxResult = new HashMap<>();
        Map<Double, Double> aMinResult = new HashMap<>();

        aMaxResult.put(0d, MatrixHelper.countAMax(resultMatrix));
        aMinResult.put(0d, MatrixHelper.countAMin(resultMatrix));
        for (int i=1; i<200; i++) {
            resultMatrix = makeMove(resultMatrix);
            aMaxResult.put((double)i, MatrixHelper.countAMax(resultMatrix));
            aMinResult.put((double)i, MatrixHelper.countAMin(resultMatrix));
        }

        ChartHelper.saveChart(aMinResult, aMaxResult, "result");
    }


    @Test
    public void testPng5() throws Exception {
        double[][] resultMatrix = MatrixHelper.copyMatrix(matrix1);
        log.debug(MatrixHelper.matrixStr(resultMatrix));
        for (int i=0; i<15; i++) {
            resultMatrix = MatrixHelper.addOneRandomPoint(resultMatrix, -1d);
        }

        Map<Double, Double> aMaxResult = new HashMap<>();
        Map<Double, Double> aMinResult = new HashMap<>();

        aMaxResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMax(resultMatrix));
        aMinResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMin(resultMatrix));
        for (int i=0; i<28; i++) {
            try {
                while (MatrixHelper.existsZeros(resultMatrix)) {
                    double[][] matrixH = MatrixHelper.countMatrixHMin(resultMatrix);
                    double[][] filledMatrix = MatrixHelper.countMatrixFWMin(resultMatrix, matrixH);
                    //log.debug(MatrixHelper.matrixStr(filledMatrix));
                    //resultMatrix = MatrixHelper.addRandomEdge(resultMatrix);
                    resultMatrix = MatrixHelper.addIntellectEdge(filledMatrix, resultMatrix);
                    //log.debug(MatrixHelper.matrixStr(resultMatrix));
                    aMaxResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMax(resultMatrix));
                    aMinResult.put(MatrixHelper.countEdges(resultMatrix) / 2d, MatrixHelper.countAMin(resultMatrix));
                }
            } catch (IllegalArgumentException e) {
                log.debug("End");
            }
            resultMatrix = MatrixHelper.addOneRandomPoint(resultMatrix, -1d);
        }

        ChartHelper.saveChart(aMinResult, aMaxResult, "result");
    }

}
