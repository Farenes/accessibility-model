package ru.matveev.model.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.InitData;
import ru.matveev.model.entity.StepResult;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.EndingCondition;
import ru.matveev.model.entity.steps.Step;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class SteppingExperiment implements Experiment {

    private final String name;
    private final String description;
    private final InitData initData;
    private final Step step;
    private final EndingCondition endingCondition;

    @Override
    public ExperimentResult make() {
        double[][] matrix = MatrixUtils.copyMatrix(initData.getMatrix());

        XYSeries aMinSeries = new XYSeries("AMin");
        XYSeries aMaxSeries = new XYSeries("AMax");
        XYSeries edgesSeries = new XYSeries("Edges");
        XYSeries pointsSeries = new XYSeries("Points");

        int step = 1;
        double min = 10000;
        double max = -10000;

        //log.debug(MatrixUtils.print(matrix));
        while (!endingCondition.isEnd(new StepResult().setStep(step).setMatrix(matrix))) {
            try {
                matrix = this.step.make(new StepData().setMatrix(matrix));
                //log.debug(MatrixUtils.print(matrix));
            } catch (EarlyEndException e) {
                min = 0.7d;
                max = 0.9d;
                break;
            }
            double aMin = MatrixCountHelper.countAMin(matrix);
            double aMax = MatrixCountHelper.countAMax(matrix);
            max = Math.max(aMin, max);
            min = Math.min(aMax, min);
            aMinSeries.add(step, aMin);
            aMaxSeries.add(step, aMax);
            edgesSeries.add(step, MatrixCountHelper.countEdges(matrix));
            pointsSeries.add(step, matrix.length);
            step++;
        }

        List<double[][]> resultMatrix = new ArrayList<>();
        resultMatrix.add(matrix);

        return new ExperimentResult()
                .setResultMatrix(resultMatrix)
                .setAMinSeries(List.of(aMinSeries))
                .setAMaxSeries(List.of(aMaxSeries))
                .setEdgesSeries(edgesSeries)
                .setPointsSeries(pointsSeries)
                .setMinAxesVal(min)
                .setMaxAxesVal(max);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
