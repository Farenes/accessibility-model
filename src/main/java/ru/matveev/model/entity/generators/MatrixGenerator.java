package ru.matveev.model.entity.generators;

/**
 * Интерфейс для генерации матрицы
 */
@FunctionalInterface
public interface MatrixGenerator {

    double[][] generate();

}
