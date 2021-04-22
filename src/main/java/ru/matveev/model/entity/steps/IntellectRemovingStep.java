package ru.matveev.model.entity.steps;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

/**
 * Заполняем матрицу до всех путей
 * Считаем медианы столбцов
 * Выбираем столбец с минимальной медианой
 * Выдергиваем связь с наименьшим входом
 * Если развалился, то не берем такой вариант
 * И переходим к следующей связи или следующему столбцу
 */
@RequiredArgsConstructor
public class IntellectRemovingStep implements Step {

    private final int maxVertexes;
    private final int maxEdges;

    @Override
    public double[][] make(StepData data) {
        double beforeAMax = MatrixCountHelper.countAMax(data.getMatrix());
        double minMedianBorder = 0d;
        double minInputBorder = 0d;
        double[][] matrix;
        oloop: while (true) {
            matrix = MatrixUtils.copyMatrix(data.getMatrix());
            double[][] matrixH = MatrixCountHelper.countMatrixHMax(matrix);
            double[][] filledMatrix = MatrixCountHelper.countMatrixFWMax(matrix, matrixH);

            Pair<Integer, Double> minMedianResult = MatrixCountHelper.findColumnWithMinMedian(filledMatrix, minMedianBorder);
            int minMedianCol = minMedianResult.getLeft();
            if (minMedianCol == -1) {
                throw new EarlyEndException();
            }
            while (true) {
                matrix = MatrixUtils.copyMatrix(data.getMatrix());
                Pair<Integer, Double> minInputResult = MatrixCountHelper.findMin(MatrixUtils.getColumn(matrix, minMedianCol), minInputBorder);
                int minInput = minInputResult.getLeft();
                if (minInput == -1) {
                    minMedianBorder = minMedianResult.getRight();
                    break;
                }
                matrix[minInput][minMedianCol] = 0d;
                matrix[minMedianCol][minInput] = 0d;
                if (MatrixUtils.isConnected(matrix)) {
                //if (MatrixUtils.isConnectivity(matrix) && beforeAMax < MatrixCountHelper.countAMax(matrix)) {
                    break oloop;
                } else {
                    minInputBorder = minInputResult.getRight();
                }
            }
        }

        return matrix;
    }

}
