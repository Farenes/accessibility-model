package ru.matveev.model.entity.generators;

/**
 * Интерфейс для расчета остовного дерева графа
 */
@FunctionalInterface
public interface SpanningTreeCounter {

    double[][] count(double[][] matrix);

}
