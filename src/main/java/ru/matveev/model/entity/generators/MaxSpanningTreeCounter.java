package ru.matveev.model.entity.generators;

import ru.matveev.model.utils.MatrixCountHelper;

/**
 * Поиск остовного дерева по максимальным средним связям ((Aij+Aji)/2) между смежными узлами
 */
public class MaxSpanningTreeCounter implements SpanningTreeCounter {

    @Override
    public double[][] count(double[][] matrix) {
        return MatrixCountHelper.getMaxSpanningTree(matrix);
    }
}
