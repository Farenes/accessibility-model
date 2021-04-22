package ru.matveev.model.entity.steps;

import org.apache.commons.lang3.tuple.Triple;
import ru.matveev.model.exception.EarlyEndException;
import ru.matveev.model.entity.StepData;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Удаление случайной связи между любыми двумя узлами, чтобы посмотреть как изменяется aMax
 */
public class ConsistentRemovingStep implements Step {

    @Override
    public double[][] make(StepData data) {
        double beforeAMax = MatrixCountHelper.countAMax(data.getMatrix());

        double[][] matrix;
        List<Triple<Integer, Integer, Double>> edges = findEdges(data.getMatrix());

       //edges.sort(Comparator.comparing(Triple::getRight));

        for (Triple<Integer, Integer, Double> edge: edges) {
            matrix = MatrixUtils.copyMatrix(data.getMatrix());
            matrix[edge.getLeft()][edge.getMiddle()] = 0;
            matrix[edge.getMiddle()][edge.getLeft()] = 0;
            //if (MatrixUtils.isConnectivity(matrix) && MatrixCountHelper.countAMax(matrix) > beforeAMax) {
            if (MatrixUtils.isConnected(matrix)) {
                return matrix;
            }
        }

        throw new EarlyEndException();
    }

    private List<Triple<Integer, Integer, Double>> findEdges(double[][] matrix) {
        List<Triple<Integer, Integer, Double>> list = new ArrayList<>();
        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix.length; j++) {
                if (matrix[i][j] > 0) {
                    list.add(Triple.of(i, j, matrix[i][j] + matrix[j][i]));
                }
            }
        }
        return list;
    }

}
