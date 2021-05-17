package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.InitData;
import ru.matveev.model.entity.generators.BestRandomSpanningTreeCounter;
import ru.matveev.model.entity.generators.MaxSpanningTreeCounter;
import ru.matveev.model.entity.generators.PreInitMatrixGenerator;
import ru.matveev.model.entity.generators.RandomMatrixGenerator;
import ru.matveev.model.entity.steps.*;
import ru.matveev.model.experiment.CompareExperiment;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.MetaSpanningTreeAlphaExperiment;
import ru.matveev.model.experiment.SteppingExperiment;
import ru.matveev.model.utils.ExperimentHelper;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixEditorHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.List;

@Slf4j
public class Experiments {

    public void runAll() {
        addingEdgesExperiment001();
        addingVertexAndEdges002();
        addingVertexWithOneEdge003();
        randomRemovingEdges004();
        removingWorseEdgesAndAddingToWorsePath005();
        addingEdgesToMaxSpanningTreeWithDifferentAlphas006();
        addingEdgesToMaxSpanningTreeForStarTopology007();
        addingEdgesToMaxSpanningTreeWithWorsePath008();
        addingEdgesToMaxSpanningTreeWithBestAverageEdge009();
        addingEdgesToMaxAndRandomSpanningTreeWithBestAverageEdge010();
        addingEdgesToMaxAndRandomSpanningTreeWithBestAverageEdge011();
    }

    public static void addingEdgesExperiment001() {
        Experiment experiment = new SteppingExperiment(
                "Эксперимент 001. Добавление только связей",
                "",
                new InitData().setMatrix(MatrixEditorHelper.generateMatrix(20, 40, 0.78d, 0.98d)),
                new AddingIntellectOnlyEdgesStep(),
                stepResult -> MatrixUtils.isFullyConnected(stepResult.getMatrix()));
        makeExperiment(experiment);
    }

    public static void addingVertexAndEdges002() {
        Experiment experiment = new SteppingExperiment(
                "Эксперимент 002. Добавление узлов с 1 связью и потом связей до полносвязного графа",
                "",
                new InitData().setMatrix(MatrixEditorHelper.generateMatrix(20, 40, 0.78d, 0.98d)),
                new AddingPointsAndEdgesStep(),
                stepResult -> stepResult.getMatrix().length >= 40 || MatrixUtils.isFullyConnected(stepResult.getMatrix())
        );
        makeExperiment(experiment);
    }

    public static void addingVertexWithOneEdge003() {
        Experiment experiment = new SteppingExperiment(
                "Эксперимент 003. Добавление только узлов с 1 связью",
                "",
                new InitData().setMatrix(MatrixEditorHelper.generateMatrix(20, 40, 0.78d, 0.98d)),
                new AddingOnlyPointsStep(),
                stepResult -> stepResult.getMatrix().length >= 50
        );
        makeExperiment(experiment);
    }

    public static void randomRemovingEdges004() {
        Experiment experiment = new SteppingExperiment(
                "Эксперимент 004. Удаление любой связи, если aMax растет",
                "",
                new InitData().setMatrix(MatrixEditorHelper.generateMatrix(20, 150, 0.78d, 0.98d)),
                new ConsistentRemovingStep(),
                stepResult -> stepResult.getStep() >= 300
        );
        makeExperiment(experiment);
    }

    public static void removingWorseEdgesAndAddingToWorsePath005() {
        Experiment experiment = new SteppingExperiment(
                "Эксперимент 005. Удаление связи с наихудшей суммой, и добавление в наихудший путь",
                "",
                new InitData().setMatrix(MatrixEditorHelper.generateMatrix(20, 150, 0.78d, 0.98d)),
                new IntellectRemovingAddingStep(),
                stepResult -> stepResult.getStep() >= 300);
        makeExperiment(experiment);
    }

    public static void addingEdgesToMaxSpanningTreeWithDifferentAlphas006() {
        double[][] matrix = MatrixEditorHelper.generateMatrix(20, 150, 0.78d, 0.98d);
        matrix = MatrixCountHelper.getMaxSpanningTree(matrix);

        double alpha = 0.00001;
        Experiments.makeExperiment(new SteppingExperiment(
                "Эксперимент 006. Заполнение остовного дерева α=" + alpha,
                "",
                new InitData().setMatrix(matrix),
                new AddingWorsePathWithAlphaStep(alpha),
                stepResult -> stepResult.getStep() >= 300
        ));

        alpha = 0.0001;
        Experiments.makeExperiment(new SteppingExperiment(
                "Эксперимент 006. Заполнение остовного дерева α=" + alpha,
                "",
                new InitData().setMatrix(matrix),
                new AddingWorsePathWithAlphaStep(alpha),
                stepResult -> stepResult.getStep() >= 300
        ));

        alpha = 0.001;
        Experiments.makeExperiment(new SteppingExperiment(
                "Эксперимент 006. Заполнение остовного дерева α=" + alpha,
                "",
                new InitData().setMatrix(matrix),
                new AddingWorsePathWithAlphaStep(alpha),
                stepResult -> stepResult.getStep() >= 300
        ));
    }

    public static void addingEdgesToMaxSpanningTreeForStarTopology007() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        Experiment experiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 007. К звездам",
                "",
                1000, alpha,
                new RandomMatrixGenerator(vertexes, edges),
                new MaxSpanningTreeCounter(),
                new ToStarsStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        makeExperiment(experiment);
    }

    public static void addingEdgesToMaxSpanningTreeWithWorsePath008() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        Experiment experiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 008. С худшей связью",
                "",
                1000, alpha,
                new RandomMatrixGenerator(vertexes, edges),
                new MaxSpanningTreeCounter(),
                new AddingWorsePathWithAlphaStep(alpha),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        makeExperiment(experiment);
    }

    public static void addingEdgesToMaxSpanningTreeWithBestAverageEdge009() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        Experiment experiment = new MetaSpanningTreeAlphaExperiment(
                        "Эксперимент 009. С лучшей суммой",
                        "",
                        1000, alpha,
                        new RandomMatrixGenerator(vertexes, edges),
                        new MaxSpanningTreeCounter(),
                        new AddingBestSumEdgesStep(),
                        stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
                makeExperiment(experiment);
    }

    public static void addingEdgesToMaxAndRandomSpanningTreeWithBestAverageEdge010() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        int count = 1000;
        int spanningTreeCount = 1000;
        PreInitMatrixGenerator preGen = new PreInitMatrixGenerator(count, vertexes, edges);
        Experiments.makeExperiment(new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 010. С лучшей новой связью с рандомом",
                "",
                1000, alpha,
                preGen,
                new BestRandomSpanningTreeCounter(spanningTreeCount),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix())
        ));
        preGen.reset();
        Experiments.makeExperiment(new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 010. С лучшей новой связью с макс",
                "",
                1000, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix())
        ));
    }

    public static void addingEdgesToMaxAndRandomSpanningTreeWithBestAverageEdge011() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        int count = 1000;
        int spanningTreeCount = 1000;
        PreInitMatrixGenerator preGen = new PreInitMatrixGenerator(count, vertexes, edges);
        Experiment spanningTreeRandomExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с рандомом",
                "",
                1000, alpha,
                preGen,
                new BestRandomSpanningTreeCounter(spanningTreeCount),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                1000, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiments.makeExperiment(new CompareExperiment(
                "Эксперимент 011. Сравнение",
                "",
                List.of(spanningTreeRandomExperiment, spanningTreeMaxExperiment)));
    }

    public static void all() {
        double alpha = 0.00001;
        int vertexes = 10;
        int edges = 25;
        int count = 1000;
        int spanningTreeCount = 1000;
        PreInitMatrixGenerator preGen = new PreInitMatrixGenerator(count, vertexes, edges);
        Experiment experiment1 = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 007. К звездам",
                "",
                count, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new ToStarsStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiment experiment2 = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 009. С лучшей суммой",
                "",
                count, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingBestSumEdgesStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiment experiment3 = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 008. С худшей связью",
                "",
                count, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingWorsePathWithAlphaStep(alpha),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiment spanningTreeRandomExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с рандомом",
                "",
                count, alpha,
                preGen,
                new BestRandomSpanningTreeCounter(spanningTreeCount),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiment spanningTreeMaxExperiment = new MetaSpanningTreeAlphaExperiment(
                "Эксперимент 011. С лучшей новой связью с макс",
                "",
                1000, alpha,
                preGen,
                new MaxSpanningTreeCounter(),
                new AddingBestAmaxEdgeStep(),
                stepResult -> MatrixCountHelper.countEdges(stepResult.getMatrix()) >= MatrixCountHelper.countEdges(stepResult.getInitMatrix()));
        Experiments.makeExperiment(new CompareExperiment(
                "Эксперимент 011. Сравнение",
                "",
                List.of(experiment3, experiment1, experiment2, spanningTreeRandomExperiment, spanningTreeMaxExperiment)));
    }

    public static void makeExperiment(Experiment experiment) {
        try {
            log.debug("{}. {}", experiment.getName(), experiment.getDescription());
            ExperimentResult result = experiment.make();
            ExperimentHelper.saveResultToFile(experiment.getName(), result);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
