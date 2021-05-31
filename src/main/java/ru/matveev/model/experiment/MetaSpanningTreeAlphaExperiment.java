package ru.matveev.model.experiment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;
import ru.matveev.model.entity.*;
import ru.matveev.model.entity.generators.MatrixGenerator;
import ru.matveev.model.entity.generators.SpanningTreeCounter;
import ru.matveev.model.entity.steps.Step;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

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
        List<Double> aMaxDeltas = new ArrayList<>();
        List<Double> spanningTreesAMax = new ArrayList<>();
        List<Double> lgAMaxDeltas = new ArrayList<>();
        List<Map<Integer, Double>> aMinExps = new ArrayList<>();
        List<Map<Integer, Double>> aMaxExps = new ArrayList<>();
        List<Integer> increaseSteps = new ArrayList<>();
        List<Integer> finalSteps = new ArrayList<>();
        List<double[][]> resultMatrixes = new ArrayList<>();
        int vertexes = 0;
        int edges = 0;
        int fails = 0;
        for (int i=0; i<count; i++) {
            MetaExperimentResult result = null;
            while (result == null || result.getDeltaAMax() < 0) {
                result = oneExp();
                if (result.getDeltaAMax() < 0) {
                    fails++;
                }
            }

            aMaxDeltas.add(result.getDeltaAMax());
            lgAMaxDeltas.add(result.getLgDeltaAMax());
            spanningTreesAMax.add(result.getSpanningAMax());
            aMinExps.add(result.getAMinMap());
            aMaxExps.add(result.getAMaxMap());
            increaseSteps.add(result.getIncreaseStep());
            finalSteps.add(result.getFinalStep());
            vertexes = result.getVertexes();
            edges = result.getEdges();
            resultMatrixes.add(result.getResultMatrix());
        }

        log.debug("Spanning tree size: {}", vertexes-1);
        log.debug("Spanning tree average aMax: {}", spanningTreesAMax.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        log.debug("Max steps: {}", edges - (vertexes-1));
        aMaxDeltas.sort(Comparator.comparing(Double::doubleValue).reversed());
        log.debug("Max aMax grow: {} %", aMaxDeltas.stream().mapToDouble(Double::doubleValue).max().orElse(0)*100);
        log.debug("Average aMax grow: {} %", aMaxDeltas.stream().mapToDouble(Double::doubleValue).average().orElse(0)*100);
        log.debug("Average Lg aMax grow: {} %", lgAMaxDeltas.stream().mapToDouble(Double::doubleValue).average().orElse(0)*100);
        log.debug("Average step for equal: {}", Math.round(increaseSteps.stream().mapToInt(Integer::intValue).average().orElse(0)));
        log.debug("Average steps for alpha: {}", Math.round(finalSteps.stream().mapToInt(Integer::intValue).average().orElse(0)));
        log.debug("Fails: {} on {} experiments", fails, count);

        XYSeries aMinSeries = new XYSeries("AMin");
        XYSeries aMaxSeries = new XYSeries("AMax");

        int aMinMax = aMinExps.stream().flatMap(map -> map.keySet().stream()).mapToInt(Integer::intValue).max().orElse(0);
        int aMaxMax = aMaxExps.stream().flatMap(map -> map.keySet().stream()).mapToInt(Integer::intValue).max().orElse(0);

        double min = 10000;
        double max = -10000;

        for (int i=0; i<Math.max(aMinMax, aMaxMax)+1; i++) {
            int key = i;
            double aMax = aMaxExps.stream().map(map -> map.getOrDefault(key, null)).filter(d -> d != null && !d.isNaN()).mapToDouble(Double::doubleValue).average().orElse(0);
            double aMin = aMinExps.stream().map(map -> map.getOrDefault(key, null)).filter(d -> d != null && !d.isNaN()).mapToDouble(Double::doubleValue).average().orElse(0);
            aMinSeries.add(key, aMin);
            aMaxSeries.add(key, aMax);
            min = DoubleStream.of(min, aMin, aMax).min().orElse(min);
            max = DoubleStream.of(max, aMin, aMax).max().orElse(max);
        }

        return new ExperimentResult()
                .setResultMatrix(resultMatrixes)
                .setAMinSeries(List.of(aMinSeries))
                .setAMaxSeries(List.of(aMaxSeries))
                .setMinAxesVal(min)
                .setMaxAxesVal(max);
    }

    private MetaExperimentResult oneExp() {
        double[][] initMatrix = matrixGenerator.generate();

        double startAMax = MatrixCountHelper.countAMax(initMatrix);

        Map<Integer, Double> aMinMap = new HashMap<>();
        Map<Integer, Double> aMaxMap = new HashMap<>();

        aMaxMap.put(0, startAMax);
        aMinMap.put(0, MatrixCountHelper.countAMin(initMatrix));

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
        double deltaAMax = (finishAMax - startAMax) / startAMax;
        if (deltaAMax >= 0.11) {
            log.debug(MatrixUtils.printForPaste(initMatrix));
            log.debug("Init Amax: {}" , startAMax);
            log.debug("Finish Amax: {}" , finishAMax);
        }
        double startLogAMax = -Math.log10(1 - startAMax);
        double finishLogAMax = -Math.log10(1 - finishAMax);
        double logDeltaAMax = (finishLogAMax - startLogAMax) / startLogAMax;

        return new MetaExperimentResult()
                .setResultMatrix(matrix)
                .setVertexes(initMatrix.length)
                .setEdges(MatrixCountHelper.countEdges(initMatrix))
                .setSpanningAMax(spanningTreeAMax)
                .setAMaxMap(aMaxMap)
                .setAMinMap(aMinMap)
                .setDeltaAMax(deltaAMax)
                .setLgDeltaAMax(logDeltaAMax)
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