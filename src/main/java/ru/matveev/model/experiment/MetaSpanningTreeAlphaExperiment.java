package ru.matveev.model.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;
import ru.matveev.model.entity.*;
import ru.matveev.model.entity.generators.MatrixGenerator;
import ru.matveev.model.entity.generators.SpanningTreeCounter;
import ru.matveev.model.entity.steps.Step;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.utils.GraphHelper;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.DoubleStream;

@Slf4j
@RequiredArgsConstructor
public class MetaSpanningTreeAlphaExperiment implements Experiment {

    private final String name;
    private final String description;
    private final int count;
    private final double alpha;
    private final MatrixGenerator matrixGenerator;
    private final SpanningTreeCounter spanningTreeCounter;
    private final Step step;
    private final EndingCondition endingCondition;

    @Override
    public ExperimentResult make() {
        List<double[][]> resultMatrixes = new ArrayList<>();
        int vertexes = 0;
        int edges = 0;
        int fails = 0;
        List<MetaExperimentResult> metaExperimentResults = new ArrayList<>();
        for (int i=0; i<count; i++) {
            MetaExperimentResult result = null;
            while (result == null || result.getDeltaAMax() < 0) {
                result = makeOneExperiment();
                if (result.getDeltaAMax() < 0) {
                    fails++;
                }
            }
            metaExperimentResults.add(result);

            vertexes = result.getVertexes();
            edges = result.getEdges();
            resultMatrixes.add(result.getResultMatrix());
        }

        log.debug("Spanning tree size: {}", vertexes-1);
        log.debug("Spanning tree average aMax: {}", metaExperimentResults.stream().mapToDouble(MetaExperimentResult::getSpanningAMax).average().orElse(0));
        log.debug("Max steps from spanning tree to init size: {}", edges - (vertexes-1));
        double maxAMaxGrow = metaExperimentResults.stream().mapToDouble(MetaExperimentResult::getDeltaAMax).max().orElse(0)*100;
        log.debug("Max aMax grow: {} %", maxAMaxGrow);
        log.debug("Average aMax grow: {} %", metaExperimentResults.stream().mapToDouble(MetaExperimentResult::getDeltaAMax).average().orElse(0)*100);
        log.debug("Average step for equal: {}", Math.round(metaExperimentResults.stream().mapToInt(MetaExperimentResult::getIncreaseStep).average().orElse(0)));
        log.debug("Average steps for alpha: {}", Math.round(metaExperimentResults.stream().mapToInt(MetaExperimentResult::getFinalStep).average().orElse(0)));
        log.debug("Fails: {} on {} experiments", fails, count);

        XYSeries aMinSeries = new XYSeries("AMin");
        XYSeries aMaxSeries = new XYSeries("AMax");

        int aMinMax = metaExperimentResults.stream().flatMap(exp -> exp.getAMinMap().keySet().stream()).mapToInt(Integer::intValue).max().orElse(0);
        int aMaxMax = metaExperimentResults.stream().flatMap(exp -> exp.getAMaxMap().keySet().stream()).mapToInt(Integer::intValue).max().orElse(0);

        double min = 10000;
        double max = -10000;

        for (int i=0; i<Math.max(aMinMax, aMaxMax)+1; i++) {
            int key = i;
            double aMax = metaExperimentResults.stream().map(exp -> exp.getAMaxMap().getOrDefault(key, null)).filter(d -> d != null && !d.isNaN()).mapToDouble(Double::doubleValue).average().orElse(0);
            double aMin = metaExperimentResults.stream().map(exp -> exp.getAMinMap().getOrDefault(key, null)).filter(d -> d != null && !d.isNaN()).mapToDouble(Double::doubleValue).average().orElse(0);
            aMinSeries.add(key, aMin);
            aMaxSeries.add(key, aMax);
            min = DoubleStream.of(min, aMin, aMax).min().orElse(min);
            max = DoubleStream.of(max, aMin, aMax).max().orElse(max);
        }

        return new ExperimentResult()
                .setExperiments(metaExperimentResults)
                .setMaxAMaxGrow(maxAMaxGrow)
                .setResultMatrix(resultMatrixes)
                .setAMinSeries(List.of(aMinSeries))
                .setAMaxSeries(List.of(aMaxSeries))
                .setMinAxesVal(min)
                .setMaxAxesVal(max);
    }

    private MetaExperimentResult makeOneExperiment() {
        double[][] initMatrix = matrixGenerator.generate();

        double startAMax = MatrixCountHelper.countAMax(initMatrix);
        double startAMin = MatrixCountHelper.countAMin(initMatrix);

        Map<Integer, Double> aMinMap = new HashMap<>();
        Map<Integer, Double> aMaxMap = new HashMap<>();

        aMaxMap.put(0, startAMax);
        aMinMap.put(0, startAMin);

        double[][] matrix = spanningTreeCounter.count(initMatrix);
        double spanningTreeAMax = MatrixCountHelper.countAMax(matrix);

        aMaxMap.put(1, spanningTreeAMax);
        aMinMap.put(1, MatrixCountHelper.countAMin(matrix));

        int step = 2;

        double preAMax = MatrixCountHelper.countAMax(matrix);
        double[][] preMatrix = matrix;

        int increaseStep = -1;
        while (!endingCondition.isEnd(new StepResult().setStep(step).setInitMatrix(initMatrix).setMatrix(matrix))) {
            try {
                matrix = this.step.make(new StepData().setMatrix(matrix));
            } catch (EarlyEndException e) {
                break;
            }
            double aMin = MatrixCountHelper.countAMin(matrix);
            double aMax = MatrixCountHelper.countAMax(matrix);
            if (aMax > startAMax && increaseStep == -1) {
                increaseStep = step;
            }
            if (aMax - preAMax < alpha) {
                matrix = preMatrix;
                break;
            }
            preMatrix = matrix;
            preAMax = aMax;
            aMinMap.put(step, aMin);
            aMaxMap.put(step, aMax);
            step++;
        }

        double finishAMax = MatrixCountHelper.countAMax(matrix);
        double finishAMin = MatrixCountHelper.countAMin(matrix);

        double deltaAMax = (finishAMax - startAMax) / startAMax;
        double deltaAMin = (finishAMin - startAMin) / startAMin;

        return new MetaExperimentResult()
                .setStartAMax(startAMax)
                .setResultAMax(finishAMax)
                .setStartAMin(startAMin)
                .setResultAMin(finishAMin)
                .setStartMatrix(initMatrix)
                .setResultMatrix(matrix)
                .setVertexes(initMatrix.length)
                .setEdges(MatrixCountHelper.countEdges(initMatrix))
                .setSpanningAMax(spanningTreeAMax)
                .setAMaxMap(aMaxMap)
                .setAMinMap(aMinMap)
                .setDeltaAMax(deltaAMax)
                .setDeltaAMin(deltaAMin)
                .setIncreaseStep(increaseStep)
                .setFinalStep(step-1);
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