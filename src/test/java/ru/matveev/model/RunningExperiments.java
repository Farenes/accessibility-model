package ru.matveev.model;

import org.junit.Test;
import ru.matveev.model.entity.generators.MatrixGenerator;
import ru.matveev.model.entity.generators.MaxSpanningTreeCounter;
import ru.matveev.model.entity.generators.ConnectedMatrixGenerator;
import ru.matveev.model.entity.generators.RandomMatrixGeneratorWithCentrality;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.experiment.CompareExperiment;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.MetaSpanningTreeAlphaExperiment;
import ru.matveev.model.utils.MatrixCountHelper;

import java.util.List;

public class RunningExperiments {

    @Test
    public void firstTask() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 10;
        int count = 1;

        // 1 задание: определить свой matrixGenerator, который будет выдавать матрицы с определенными характеристиками
        MatrixGenerator matrixGenerator = new RandomMatrixGeneratorWithCentrality(vertexes, edges);
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
        int count = 1000;

        // 2 задание: определить свой matrixGenerator, который будет выдавать матрицы с определенными характеристиками
        // Создать несколько классов matrixGenerator с передачей разных параметров
        // Например, сделать какой-то генератор типа ConnectedMatrixGenerator(double connected)
        // connected - связность, от 0 до 1, 1 - полносвязный, 0 - остов или несвязный
        // Создать генераторы ConnectedMatrixGenerator(0.4), ConnectedMatrixGenerator(0.5), ConnectedMatrixGenerator(0.6)
        // Создать эксперименты с каждым видом генератора, примерно как ниже
        MatrixGenerator matrixGenerator1 = new ConnectedMatrixGenerator(vertexes, 0.4); //подставить свой с 0.4
        Experiment experiment1 = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 012. Связность 0.4", // влияет на название файла с графиками
                "",
                count, alpha,
                matrixGenerator1, // отставить как есть
                new MaxSpanningTreeCounter(), // оставить как есть этот
                new AddingBestAmaxEdgeStep(), // оставить как есть, это лучший алгоритм
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()) // оставить как есть, заканчивает когда количество связей становится как в изначальной матрице
        );
        MatrixGenerator matrixGenerator2 = new ConnectedMatrixGenerator(vertexes, 0.5); //подставить свой с 0.5
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

}
