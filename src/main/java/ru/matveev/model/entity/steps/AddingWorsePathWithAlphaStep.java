package ru.matveev.model.entity.steps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

/**
 * Шаг по заполнению остовного дерева в наихудший путь с учетом приращения alpha
 */
@Slf4j
@RequiredArgsConstructor
public class AddingWorsePathWithAlphaStep implements Step {

    private final double alpha;

    @Override
    public double[][] make(StepData data) {
        double beforeAMax = MatrixCountHelper.countAMax(data.getMatrix());
        double[][] matrix = MatrixUtils.copyMatrix(data.getMatrix());
        double[][] matrixH = MatrixCountHelper.countMatrixHMax(matrix);
        double[][] filledMatrix = MatrixCountHelper.countMatrixFWMax(matrix, matrixH);

        Pair<Integer, Integer> minPathResult = MatrixCountHelper.findMinPath(filledMatrix, matrix);
        if (minPathResult.getLeft() == -1) {
            throw new EarlyEndException();
        }

        matrix[minPathResult.getLeft()][minPathResult.getRight()] = MatrixUtils.foundAnyRowValue(matrix, minPathResult.getLeft());
        matrix[minPathResult.getRight()][minPathResult.getLeft()] = MatrixUtils.foundAnyRowValue(matrix, minPathResult.getRight());
        double aMax = MatrixCountHelper.countAMax(matrix);

        if (aMax < beforeAMax || aMax - beforeAMax < alpha) {
            throw new EarlyEndException();
        }
        return matrix;
    }

}
