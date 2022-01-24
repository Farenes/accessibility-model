package ru.matveev.model;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.junit.Test;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.generators.MatrixGenerator;
import ru.matveev.model.entity.generators.MaxSpanningTreeCounter;
import ru.matveev.model.entity.generators.PreInitMatrixGenerator;
import ru.matveev.model.entity.generators.RandomMatrixGenerator;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.experiment.CompareExperiment;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.MetaSpanningTreeAlphaExperiment;
import ru.matveev.model.utils.ExperimentHelper;
import ru.matveev.model.utils.GraphHelper;
import ru.matveev.model.utils.InterestingMatrix;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixForShowing;

import java.util.ArrayList;
import java.util.List;

public class RunningExperiments {

    @Test
    public void firstTask() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        int count = 1000;

        // 1 задание: определить свой matrixGenerator, который будет выдавать матрицы с определенными характеристиками
        MatrixGenerator matrixGenerator = new PreInitMatrixGenerator(count, vertexes, edges);
        Experiments.makeExperiment(new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 010. С лучшей новой связью с макс", // влияет на название файла с графиками
                "",
                count, alpha,
                matrixGenerator, // отставить как есть
                new MaxSpanningTreeCounter(), // оставить как есть этот
                new AddingBestAmaxEdgeStep(), // оставить как есть, это лучший алгоритм
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()) // оставить как есть, заканчивает когда количество связей становится как в изначальной матрице
        ));
    }

    @Test
    public void secondTask() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        int count = 1000;

        // 2 задание: определить свой matrixGenerator, который будет выдавать матрицы с определенными характеристиками
        // Создать несколько классов matrixGenerator с передачей разных параметров
        // Например, сделать какой-то генератор типа ConnectedMatrixGenerator(double connected)
        // connected - связность, от 0 до 1, 1 - полносвязный, 0 - остов или несвязный
        // Создать генераторы ConnectedMatrixGenerator(0.4), ConnectedMatrixGenerator(0.5), ConnectedMatrixGenerator(0.6)
        // Создать эксперименты с каждым видом генератора, примерно как ниже
        MatrixGenerator matrixGenerator1 = new RandomMatrixGenerator(vertexes, edges); //подставить свой с 0.4
        Experiment experiment1 = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 012. Связность 0.4", // влияет на название файла с графиками
                "",
                count, alpha,
                matrixGenerator1, // отставить как есть
                new MaxSpanningTreeCounter(), // оставить как есть этот
                new AddingBestAmaxEdgeStep(), // оставить как есть, это лучший алгоритм
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()) // оставить как есть, заканчивает когда количество связей становится как в изначальной матрице
        );
        MatrixGenerator matrixGenerator2 = new RandomMatrixGenerator(vertexes, edges); //подставить свой с 0.5
        Experiment experiment2 = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 012. Связность 0.4", // влияет на название файла с графиками
                "",
                count, alpha,
                matrixGenerator2, // отставить как есть
                new MaxSpanningTreeCounter(), // оставить как есть этот
                new AddingBestAmaxEdgeStep(), // оставить как есть, это лучший алгоритм
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()) // оставить как есть, заканчивает когда количество связей становится как в изначальной матрице
        );
        // сравниваем
        Experiments.makeExperiment(new CompareExperiment(
                "Эксперимент 013. Сравнение по связности", // будет названием файла
                "",
                List.of(experiment1, experiment2)));
    }

    /*
     * Тест на проверку зависимости изменения aMax от количества связей с растущим количеством узлов
     * 20.12.2021
     */
    @Test
    public void testLinks() throws Exception {
        double alpha = 0.00001;
        int vertexes = 9;
        int count = 1000;

        List<XYSeries> aMaxSeries = new ArrayList<>();
        for (int i=0; i<4; i++) {
            XYSeries series = makeExperimentByVertexes(vertexes, count, alpha);
            aMaxSeries.add(series);
            vertexes += 3;
        }

        ExperimentResult fullResult = new ExperimentResult()
                .setAMaxSeries(aMaxSeries)
                .setMinAxesVal(findMin(aMaxSeries))
                .setMaxAxesVal(findMax(aMaxSeries));

        ExperimentHelper.saveResultToFile("Зависимость", fullResult);
    }

    private XYSeries makeExperimentByVertexes(int vertexes, int count, double alpha) {
        XYSeries aMaxSeries = new XYSeries("n=" + vertexes);

        int minEdges = vertexes-1;
        int maxEdges = (vertexes-1) * vertexes / 2;
        int currentEdges = minEdges;
        for (int i=0; i<10; i++) {
            double proc = currentEdges / (double) maxEdges;
            double aMax = makeExperimentByEdge(vertexes, currentEdges, count, alpha);
            aMaxSeries.add(proc*100, aMax);
            currentEdges = Math.min(maxEdges, currentEdges + (maxEdges / 10));
        }

        return aMaxSeries;
    }

    private double makeExperimentByEdge(int vertexes, int edges, int count, double alpha) {
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                count, alpha,
                new RandomMatrixGenerator(vertexes, edges),
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        ExperimentResult result = spanningTreeMaxExperiment.make();
        return result.getMaxAMaxGrow();
    }

    private double findMax(List<XYSeries> series) {
        double max = -10000;
        for (XYSeries s: series) {
            for (Object i: s.getItems()) {
                XYDataItem k = (XYDataItem) i;
                if (k.getYValue() > max) {
                    max = k.getYValue();
                }
            }
        }
        return max;
    }

    private double findMin(List<XYSeries> series) {
        double min = 10000;
        for (XYSeries s: series) {
            for (Object i: s.getItems()) {
                XYDataItem k = (XYDataItem) i;
                if (k.getYValue() < min) {
                    min = k.getYValue();
                }
            }
        }
        return min;
    }

}
