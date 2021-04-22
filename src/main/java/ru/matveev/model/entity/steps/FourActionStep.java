package ru.matveev.model.entity.steps;

import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixEditorHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.Random;

/**
 * Совершение случайного действия (добавление узла, добавление связи, удаление узла, удаление связи) по вероятностям p1, p2, p3:
 *
 */
public class FourActionStep implements Step {

    private static double p1 = 0.15;
    private static double p2 = 0.35;
    private static double p3 = 0.60;
    private static Random rand = new Random();

    @Override
    public double[][] make(StepData data) {
        double p = rand.nextDouble();
        if (p < p1 && data.getMatrix().length > 5) {
            double[][] matrix = MatrixEditorHelper.removeOneRandomPoint(data.getMatrix());
            if (MatrixUtils.isConnected(matrix)) {
                return matrix;
            }
        } else if (p >= p1 && p < p2) {
            double[][] matrix = MatrixEditorHelper.removeOneRandomEdge(data.getMatrix());
            if (MatrixUtils.isConnected(matrix)) {
                return matrix;
            }
        } else if (p >= p2 && p < p3 && data.getMatrix().length < 40) {
            return MatrixEditorHelper.addOneRandomPoint(data.getMatrix(), 0d);
        } else if (p >= p3 && MatrixCountHelper.countEdges(data.getMatrix()) < (data.getMatrix().length-1)*data.getMatrix().length/2) {
            return MatrixEditorHelper.addOneRandomEdge(data.getMatrix());
        }
        return data.getMatrix();
    }
}
