package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import ru.matveev.model.utils.GraphHelper;
import ru.matveev.model.utils.InterestingMatrix;
import ru.matveev.model.utils.MatrixEditorHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.io.IOException;

@Slf4j
public class MatrixCountHelperTest {

    @Test
    public void testGenerateMatrix_rightEdges() {
        double[][] matrix = MatrixEditorHelper.generateMatrix(10, 20, 0.7d, 0.9d);
        log.debug(MatrixUtils.print(matrix));
        Assert.assertEquals(10, matrix.length);
        int edges = 0;
        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix.length; j++) {
                if (i == j && matrix[i][j] < 1) {
                    Assert.fail();
                } else if (i != j && matrix[i][j] > 0 && (matrix[i][j] < 0.7d || matrix[i][j] > 0.9d)) {
                    Assert.fail();
                } else if (i != j && matrix[i][j] > 0) {
                    edges++;
                }
            }
        }
        Assert.assertEquals(20, edges / 2);
    }

    @Test
    public void testVisualize() throws IOException {
        GraphHelper.visualizeGraph(InterestingMatrix.matrix3);
    }

}
