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
 * Добавление связи, которая дает наибольший aMax при добавлении
 */
@Slf4j
public class AddingBestAmaxEdgeStep implements Step {

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
                    double[][] countMatrix = MatrixUtils.copyMatrix(matrix);
                    countMatrix[i][j] = access[i];
                    countMatrix[j][i] = access[j];
                    nextPairs.add(Triple.of(i, j, MatrixCountHelper.countAMax(countMatrix)));
                }
            }
        }

        nextPairs.sort(Comparator.comparing((Function<Triple<Integer, Integer, Double>, Double>) Triple::getRight).reversed());

        if (nextPairs.isEmpty()) {
            throw new EarlyEndException();
        }

        Triple<Integer, Integer, Double> maxAccessResult = nextPairs.get(0);
        if (maxAccessResult.getLeft() == -1) {
            throw new EarlyEndException();
        }

        matrix[maxAccessResult.getLeft()][maxAccessResult.getMiddle()] = access[maxAccessResult.getLeft()];
        matrix[maxAccessResult.getMiddle()][maxAccessResult.getLeft()] = access[maxAccessResult.getMiddle()];
        double aMax = MatrixCountHelper.countAMax(matrix);

        if (aMax < beforeAMax) {
            throw new EarlyEndException();
        }
        return matrix;
    }

}
