package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;
import org.junit.Assert;
import org.junit.Test;
import ru.matveev.model.entity.ChartData;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.generators.*;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.MetaSpanningTreeAlphaExperiment;
import ru.matveev.model.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MatrixCountHelperTest {

    @Test
    public void testNorm() {
        MatrixGenerator generator = new RandomMatrixGenerator(10, 25);
        for (int i=0; i<10; i++) {
            log.debug("{}", MatrixCountHelper.countAMax(generator.generate()));
        }

        double[][] matrix = new double[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
        };
        log.debug("{}", MatrixCountHelper.normDistKolmagorovP2(matrix));

        matrix = new double[][] {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
        };
        log.debug("{}", MatrixCountHelper.normDistKolmagorovP2(matrix));

        matrix = new double[][] {
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
        };
        log.debug("{}", MatrixCountHelper.normDistKolmagorovP2(matrix));

    }

    @Test
    public void bestAlg() {
        double alpha = 0.00001;
        int vertexes = 20;
        int edges = 100;
        int count = 1000;
        PreInitMatrixGenerator preGen = new PreInitMatrixGenerator(count, vertexes, edges);
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                count, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiments.makeExperiment(spanningTreeMaxExperiment);
    }

    @Test
    public void testCloseness() throws IOException {

        ClosenessMatrixGenerator matrixGenerator = new ClosenessMatrixGenerator(10, 11, 0.57);
        double[][] matrix = matrixGenerator.generate();
        GraphHelper.visualizeGraph(matrix, "graph");
        log.debug(MatrixUtils.printForPaste(matrix));
    }

    @Test
    public void ttt22() throws IOException {
        GraphHelper.visualizeGraph(InterestingMatrix.superBestMatrix3, "graph");
    }

    @Test
    public void testClosenessMinMax() throws IOException {

        ClosenessMatrixGenerator matrixGenerator = new ClosenessMatrixGenerator(10, 11, 0.8);
        log.debug("{}", matrixGenerator.getMinMaxCloseness());
    }

    @Test
    public void testT() {
        double[][] matrixHMax = MatrixCountHelper.countMatrixHMax(InterestingMatrix.matrix0);
        double[][] fwMax = MatrixCountHelper.countMatrixFWMax(InterestingMatrix.matrix0, matrixHMax);
        System.out.println(MatrixUtils.print(fwMax));

        double[][] initNearMatrix = MatrixCountHelper.clearInitMinMatrix(InterestingMatrix.matrix0);
        double[][] matrixH = MatrixCountHelper.countMatrixHMin(initNearMatrix);
        double[][] matrixG = MatrixCountHelper.countMatrixGMin(initNearMatrix);
        double[][] fwMin = MatrixCountHelper.countMatrixFWMin(initNearMatrix, matrixH, matrixG);
        System.out.println(MatrixUtils.print(fwMin));
    }

    @Test
    public void testGenerateMatrix_rightEdges() {
        double[][] matrix = MatrixEditorHelper.generateMatrix(10, 20, 0.7d, 0.9d);
        log.debug(MatrixUtils.print(matrix));
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
    public void ttt() throws IOException {
        double init = 0.85;
        XYSeries series = new XYSeries("gr");
        //series.add(1, init);
        for (int i=2; i<500; i++) {
            double k = 1d/(i);
            series.add(i, k);
        }
//        init = 0;
//        for (int i=100; i>=2; i--) {
//            init = init + 10*i/(Math.pow(Math.E, 10));
//            series.add(i, init);
//        }
        ChartData chartData = new ChartData()
                .setMinAxesVal(-3).setMaxAxesVal(1).setFileName("my.png")
                .setSeries(List.of(series));
        ChartHelper.saveChartToFile(chartData);
    }

    @Test
    public void testVisualize() throws IOException {
        log.debug("{}", MatrixCountHelper.countCloseness(InterestingMatrix.superBestMatrix1));
        MatrixCountHelper.countBetweenness(InterestingMatrix.superBestMatrix1);
        //GraphHelper.visualizeGraph(InterestingMatrix.superBestMatrix1);
        //GephiHelper.visualizeGraph(InterestingMatrix.superBestMatrix3);
        MatrixUtils.toGephiFormat(InterestingMatrix.superBestMatrix3, "gephi1_n.csv", "gephi1_e.csv");
    }

    @Test
    public void allTest() throws IOException {
        Experiments.all();
    }

    @Test
    public void testRemovingBestSpanningTree() {
        double alpha = 0.00001;
        int vertexes = 8;
        int edges = 15;
        int count = 1000;
        PreInitMatrixGenerator preGen = new PreInitMatrixGenerator(count, vertexes, edges);
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                count, alpha,
                preGen,
                MatrixCountHelper::getBestRemovingSpanningTree,
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        ExperimentResult result = spanningTreeMaxExperiment.make();
    }

    @Test
    public void testRun1() throws Exception {
        double alpha = 0.00001;
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                1, alpha,
                () -> InterestingMatrix.superBestMatrix20Percent8,
                MatrixCountHelper::getBestRemovingSpanningTree,
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        ExperimentResult result = spanningTreeMaxExperiment.make();
        //GephiHelper.visualizeGraph(InterestingMatrix.superBestMatrix20Percent);
        MatrixCountHelper.setZeroToMainDiagonal(InterestingMatrix.superBestMatrix20Percent8);
        System.out.println(MatrixUtils.print(InterestingMatrix.superBestMatrix20Percent8));
        double[][] resultM = result.getResultMatrix().get(0);
        MatrixCountHelper.setZeroToMainDiagonal(resultM);
        System.out.println(MatrixUtils.print(resultM));
    }

    @Test
    public void testGist() throws IOException {
        double alpha = 0.0000001;
        int vertexes = 20;
        int edges = 100;
        int count = 1000;
        PreInitMatrixGenerator preGen = new PreInitMatrixGenerator(count, vertexes, edges);
        List<double[][]> matrixes = new ArrayList<>();
        for (int i=0; i<count; i++) {
            matrixes.add(preGen.generate());
        }
        ChartHelper.getGist("gist1.png", matrixes, 30);
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                count, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        ExperimentResult result = spanningTreeMaxExperiment.make();
        ChartHelper.getGist("gist2.png", result.getResultMatrix(), 30);
    }

    @Test
    public void show() {
        double alpha = 0.00001;
        int vertexes = 9;
        int count = 1000;
        int edges = 11;

        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                count, alpha,
                () -> new RandomMatrixGenerator(8, 11).generate(),
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        ExperimentResult result = spanningTreeMaxExperiment.make();
        // 1. 9 при 30%
        // 2. 12 при 30%
        // 3. 15 при 30%
    }


}
