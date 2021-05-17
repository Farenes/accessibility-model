package ru.matveev.model.entity.generators;

import lombok.RequiredArgsConstructor;
import ru.matveev.model.utils.MatrixCountHelper;

/**
 * Поиск остовного дерева из случайного набора остовных деревьев, у которого aMax наибольшим
 * randomCount - количество генерируемых остовных деревьев
 */
@RequiredArgsConstructor
public class BestRandomSpanningTreeCounter implements SpanningTreeCounter {

    private final int randomCount;

    @Override
    public double[][] count(double[][] matrix) {
        return MatrixCountHelper.getBestSpanningTreeByTries(matrix, randomCount);
    }

}
