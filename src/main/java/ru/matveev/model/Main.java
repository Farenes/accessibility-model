package ru.matveev.model;

import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.InitData;
import ru.matveev.model.entity.generators.MaxSpanningTreeCounter;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.entity.steps.AddingBestSumEdgesStep;
import ru.matveev.model.entity.steps.AddingIntellectOnlyEdgesStep;
import ru.matveev.model.entity.steps.AddingWorsePathWithAlphaStep;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.SteppingExperiment;
import ru.matveev.model.immit.entity.Runner;
import ru.matveev.model.utils.InterestingMatrix;
import ru.matveev.model.utils.MatrixCountHelper;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        double[][] matrix0 = InterestingMatrix.matrix;

        //XYSeries xySeries = new XYSeries("aMax");
        System.out.println("Original aMax: " + Runner.experiment(matrix0, 27, 10, 150, 20000));
        System.out.println("Improved aMax: " + Runner.experiment(optimizeMatrix(matrix0), 27, 10, 150, 20000));

        //ChartHelper.saveChartToFile(new ChartData().setSeries(List.of(xySeries)).setFileName("newfile.png").setMinAxesVal(0).setMaxAxesVal(1));
    }

    private static double[][] optimizeMatrix(double[][] matrix) {
        double[][] spanningTree = new MaxSpanningTreeCounter().count(matrix);

        Experiment experiment = new SteppingExperiment(
                "Эксперимент",
                "",
                new InitData().setMatrix(spanningTree),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(matrix)
        );

        ExperimentResult result = experiment.make();

        return result.getResultMatrix().get(0);
    }

}
