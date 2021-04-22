package ru.matveev.model.entity.steps;

import ru.matveev.model.entity.StepData;

/**
 * Интерфейс для шага алгоритма (расчета следующей матрицы)
 */
@FunctionalInterface
public interface Step {

    double[][] make(StepData data);

}
