package ru.matveev.model.entity.generators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixEditorHelper;


/**
 * Генератор матрицы по переданному значения степени близости (closeness)
 */
@Slf4j
@RequiredArgsConstructor
public class ClosenessMatrixGenerator implements MatrixGenerator {

    private static final int EXPERIMENTS = 10000;
    private static final double ACCURACY = 0.02;

    private final int vertexes;
    private final int edges;
    private final double closeness;

    @Override
    public double[][] generate() {

        long tries = 1;
        double[][] matrix = MatrixEditorHelper.generateMatrix(vertexes, edges, 0.85, 0.99);

        while (Math.abs(MatrixCountHelper.countCloseness(matrix) - closeness) > ACCURACY) {
            matrix = MatrixEditorHelper.generateMatrix(vertexes, edges, 0.85, 0.99);
            tries++;
        }

        log.debug("Tries: {}", tries);

        return matrix;
    }

    public Pair<Double, Double> getMinMaxCloseness() {
        double min = 1000;
        double max = -1000;
        for (int i=0; i<EXPERIMENTS; i++) {
            double[][] matrix = MatrixEditorHelper.generateMatrix(vertexes, edges, 0.85, 0.99);
            double closeness = MatrixCountHelper.countCloseness(matrix);
            if (closeness > max) {
                max = closeness;
            }
            if (closeness < min) {
                min = closeness;
            }
        }
        return Pair.of(min, max);
    }

}
