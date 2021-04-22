package ru.matveev.model.entity.steps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Добавление связи, которая дает наибольший aMax при добавлении, но рекурсивный
 * Очень долгий и падает по памяти на больших гарфах
 */
@Slf4j
@RequiredArgsConstructor
public class AddingBestAmaxEdgesRecursionStep implements Step {

    private final int edges;

    @Override
    public double[][] make(StepData data) {
        double beforeAMax = MatrixCountHelper.countAMax(data.getMatrix());
        double[][] matrix = MatrixUtils.copyMatrix(data.getMatrix());

        double[] access = new double[matrix.length];

        for (int i=0; i<matrix.length; i++) {
            access[i] = Arrays.stream(matrix[i]).filter(d -> d < 1 && d > 0).findFirst().orElse(0);
        }

        List<double[][]> arrs = new ArrayList<>();

        List<Pair<Integer, Integer>> nextPairs = new ArrayList<>();

        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix.length; j++) {
                if (matrix[i][j] == 0) {
                    double[][] countMatrix = MatrixUtils.copyMatrix(matrix);
                    countMatrix[i][j] = access[i];
                    countMatrix[j][i] = access[j];
                    nextPairs.add(Pair.of(i, j));
                }
            }
        }

        addPoint(arrs, nextPairs, access, matrix, edges);

        double[][] maxMatrix = null;
        double maxAMax = -10000;

        for (int i=0; i<arrs.size(); i++) {
            double aMax = MatrixCountHelper.countAMax(arrs.get(i));
            if (aMax > maxAMax) {
                maxAMax = aMax;
                maxMatrix = arrs.get(i);
            }
        }

        if (maxAMax < beforeAMax) {
            throw new EarlyEndException();
        }
        return maxMatrix;
    }

    private void addPoint(List<double[][]> matrixes, List<Pair<Integer, Integer>> nextPoints, double[] access, double[][] matrix, int edges) {
        if (MatrixCountHelper.countEdges(matrix) >= edges) {
            matrixes.add(matrix);
            return;
        }
        for (int i=0; i<nextPoints.size(); i++) {
            Pair<Integer, Integer> nextPoint = nextPoints.get(i);
            List<Pair<Integer, Integer>> newPoints = new ArrayList<>(nextPoints);
            newPoints.remove(nextPoint);
            double[][] newMatrix = MatrixUtils.copyMatrix(matrix);
            newMatrix[nextPoint.getLeft()][nextPoint.getRight()] = access[nextPoint.getLeft()];
            newMatrix[nextPoint.getRight()][nextPoint.getLeft()] = access[nextPoint.getRight()];
            addPoint(matrixes, newPoints, access, newMatrix, edges);
        }
    }

}
