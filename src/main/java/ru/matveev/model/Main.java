package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;
import ru.matveev.model.entity.ChartData;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.InitData;
import ru.matveev.model.entity.generators.MaxSpanningTreeCounter;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.SteppingExperiment;
import ru.matveev.model.immit.entity.Runner;
import ru.matveev.model.utils.*;

import java.io.IOException;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        int initIntensity = 42;
        int steps = 40;
        XYSeries xySeries = new XYSeries("goodC");
        for (int i=0; i<steps; i++) {
            int intensity = initIntensity+i;
            double availability = Runner.experiment(InterestingMatrix.closenessGood1, 10, intensity, 150, 10000);
            xySeries.add(intensity, availability);
        }

        XYSeries xySeries1 = new XYSeries("badC");
        for (int i=0; i<steps; i++) {
            int intensity = initIntensity+i;
            double availability = Runner.experiment(InterestingMatrix.closenessBad1, 10, intensity, 150, 10000);
            xySeries1.add(intensity, availability);
        }
        //System.out.println("Improved aMax: " + Runner.experiment(optimizeMatrix(matrix), 27, 10, 150, 20000));

        ChartHelper.saveChartToFile(new ChartData().setShowLegend(true).setSeries(List.of(xySeries, xySeries1)).setFileName("newfile.png").setMinAxesVal(0).setMaxAxesVal(1));
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
