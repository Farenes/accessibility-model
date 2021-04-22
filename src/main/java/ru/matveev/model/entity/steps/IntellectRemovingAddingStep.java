package ru.matveev.model.entity.steps;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Алгоритм работет следующим образом:
 * Находит 2 узла (XY) с наихудшим путем между ними
 * Находит 2 смежных узла (IJ) с наименьшим средним их связей
 * Заменяет связь между XY на связь между IJ
 */
@Slf4j
public class IntellectRemovingAddingStep implements Step {

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

        List<Triple<Integer, Integer, Double>> edges = new ArrayList<>();
        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix.length; j++) {
                if (matrix[i][j] > 0) {
                    edges.add(Triple.of(i, j, (matrix[i][j] + matrix[j][i])/2));
                }
            }
        }

        edges.sort(Comparator.comparing(Triple::getRight));

        for (Triple<Integer, Integer, Double> edge: edges) {
            matrix = MatrixUtils.copyMatrix(data.getMatrix());
            matrix[edge.getLeft()][edge.getMiddle()] = 0;
            matrix[edge.getMiddle()][edge.getLeft()] = 0;
            matrix[minPathResult.getLeft()][minPathResult.getRight()] = MatrixUtils.foundAnyRowValue(matrix, minPathResult.getLeft());
            matrix[minPathResult.getRight()][minPathResult.getLeft()] = MatrixUtils.foundAnyRowValue(matrix, minPathResult.getRight());
            double aMax = MatrixCountHelper.countAMax(matrix);
            if (MatrixUtils.isConnected(matrix) && aMax > beforeAMax) {
                log.debug("aMax: {}", aMax);
                return matrix;
            }
        }

        throw new EarlyEndException();
    }

}
