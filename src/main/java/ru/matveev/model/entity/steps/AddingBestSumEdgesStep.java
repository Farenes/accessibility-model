package ru.matveev.model.entity.steps;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Добавление связи между узлами с набилольшим средним значением связей
 */
@Slf4j
public class AddingBestSumEdgesStep implements Step {

    @Override
    public double[][] make(StepData data) {
        double beforeAMax = MatrixCountHelper.countAMax(data.getMatrix());
        double[][] matrix = MatrixUtils.copyMatrix(data.getMatrix());

        double[] access = new double[matrix.length];

        for (int i=0; i<matrix.length; i++) {
            access[i] = Arrays.stream(matrix[i]).filter(d -> d < 1 && d > 0).findFirst().orElse(0);
        }

        List<Triple<Integer, Integer, Double>> nextPairs = new ArrayList<>();

        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix.length; j++) {
                if (matrix[i][j] == 0) {
                    nextPairs.add(Triple.of(i, j, (access[i] + access[j]) / 2));
                }
            }
        }

        nextPairs.sort(Comparator.comparing((Function<Triple<Integer, Integer, Double>, Double>) Triple::getRight).reversed());

        if (nextPairs.isEmpty()) {
            throw new EarlyEndException();
        }

        Triple<Integer, Integer, Double> minPathResult = nextPairs.get(0);
        if (minPathResult.getLeft() == -1) {
            throw new EarlyEndException();
        }

        matrix[minPathResult.getLeft()][minPathResult.getMiddle()] = access[minPathResult.getLeft()];
        matrix[minPathResult.getMiddle()][minPathResult.getLeft()] = access[minPathResult.getMiddle()];
        double aMax = MatrixCountHelper.countAMax(matrix);

        if (aMax < beforeAMax) {
            throw new EarlyEndException();
        }
        return matrix;
    }


}
