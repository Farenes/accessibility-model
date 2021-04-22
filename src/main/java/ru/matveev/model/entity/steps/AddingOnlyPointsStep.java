package ru.matveev.model.entity.steps;

import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixEditorHelper;
import ru.matveev.model.utils.MatrixUtils;

/**
 * Добавление только узлов
 */
public class AddingOnlyPointsStep implements Step {

    @Override
    public double[][] make(StepData data) {
        return MatrixEditorHelper.addOneRandomPoint(MatrixUtils.copyMatrix(data.getMatrix()), 0d);
    }

}
