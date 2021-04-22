package ru.matveev.model.entity.generators;

@FunctionalInterface
public interface SpanningTreeCounter {

    double[][] count(double[][] matrix);

}
