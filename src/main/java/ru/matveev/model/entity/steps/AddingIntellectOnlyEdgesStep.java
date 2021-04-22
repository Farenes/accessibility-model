package ru.matveev.model.entity.steps;

import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixEditorHelper;
import ru.matveev.model.utils.MatrixUtils;

/**
 * Добавление связей между узлами с наихудшим путем
 */
public class AddingIntellectOnlyEdgesStep implements Step {

    @Override
    public double[][] make(StepData data) {
        double[][] matrix = MatrixUtils.copyMatrix(data.getMatrix());
        double[][] matrixH = MatrixCountHelper.countMatrixHMax(matrix);
        double[][] filledMatrix = MatrixCountHelper.countMatrixFWMax(matrix, matrixH);
        return MatrixEditorHelper.addIntellectEdge(filledMatrix, matrix);
    }
}
