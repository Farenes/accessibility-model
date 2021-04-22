package ru.matveev.model.entity.steps;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
 * Было выдвинуто предположение, что если остовное дерево достраивать до топологии типа "Звезда",
 * то такой граф будет иметь наибольшую aMax.
 * Собственно, алгоритм работает так:
 * Ищет узел с максимальным количеством связей (исключая связанные со всеми) и с минимальным количеством связей
 * и устанавливает между ними связь
 */
@Slf4j
public class ToStarsStep implements Step {

    @Override
    public double[][] make(StepData data) {
        double beforeAMax = MatrixCountHelper.countAMax(data.getMatrix());
        double[][] matrix = MatrixUtils.copyMatrix(data.getMatrix());

        double[] access = new double[matrix.length];
        for (int i=0; i<matrix.length; i++) {
            access[i] = Arrays.stream(matrix[i]).filter(d -> d < 1 && d > 0).findFirst().orElse(0);
        }

        List<Pair<Integer, Long>> pointEdges = new ArrayList<>();

        for (int i=0; i<matrix.length; i++) {
            long edges = Arrays.stream(matrix[i]).filter(d -> d < 1 && d > 0).count();
            if (edges < matrix.length-1) {
                pointEdges.add(Pair.of(i, edges));
            }
        }

        pointEdges.sort(Comparator.comparing((Function<Pair<Integer, Long>, Long>) Pair::getRight).reversed());

        if (pointEdges.size() < 2) {
            throw new EarlyEndException();
        }

        int firstPoint = -1;
        int secondPoint = -1;

        for (int i=0; i<matrix.length/2; i++) {
            int x = pointEdges.get(i).getLeft();
            int y = pointEdges.get(pointEdges.size()-i-1).getLeft();
            if (matrix[x][y] == 0) {
                firstPoint = x;
                secondPoint = y;
                break;
            }
        }

        if (firstPoint == -1 || secondPoint == -1) {
            throw new EarlyEndException();
        }

        matrix[firstPoint][secondPoint] = access[firstPoint];
        matrix[secondPoint][firstPoint] = access[secondPoint];

        double aMax = MatrixCountHelper.countAMax(matrix);

        if (aMax < beforeAMax) {
            throw new EarlyEndException();
        }
        return matrix;
    }

}
