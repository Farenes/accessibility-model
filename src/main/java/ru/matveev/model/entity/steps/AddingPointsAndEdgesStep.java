package ru.matveev.model.entity.steps;

import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixEditorHelper;
import ru.matveev.model.utils.MatrixUtils;

/**
 * Добавление одной связи и потом забивание до полносвязного графа,
 * потом опять
 */
public class AddingPointsAndEdgesStep implements Step {

    @Override
    public double[][] make(StepData data) {
        double[][] matrix = MatrixUtils.copyMatrix(data.getMatrix());
        if (MatrixUtils.isFullyConnected(matrix)) {
            return MatrixEditorHelper.addOneRandomPoint(matrix, 0d);
        } else {
            double[][] matrixH = MatrixCountHelper.countMatrixHMax(matrix);
            double[][] filledMatrix = MatrixCountHelper.countMatrixFWMax(matrix, matrixH);
            return MatrixEditorHelper.addIntellectEdge(filledMatrix, matrix);
        }
    }
}
