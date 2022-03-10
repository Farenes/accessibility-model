package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;
import org.junit.Assert;
import org.junit.Test;
import ru.matveev.model.entity.ChartData;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.MetaExperimentResult;
import ru.matveev.model.entity.generators.*;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.MetaSpanningTreeAlphaExperiment;
import ru.matveev.model.utils.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MatrixCountHelperTest {

    @Test
    public void testNorm() {
        MatrixGenerator generator = new RandomMatrixGenerator(10, 25);
        for (int i=0; i<10; i++) {
            log.debug("{}", MatrixCountHelper.countAMax(generator.generate()));
        }

        double[][] matrix = new double[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
        };
        log.debug("{}", MatrixCountHelper.normDistKolmagorovP2(matrix));

        matrix = new double[][] {
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
        };
        log.debug("{}", MatrixCountHelper.normDistKolmagorovP2(matrix));

        matrix = new double[][] {
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
        };
        log.debug("{}", MatrixCountHelper.normDistKolmagorovP2(matrix));

    }

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

}
