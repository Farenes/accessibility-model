package ru.matveev.model;

import lombok.extern.slf4j.Slf4j;
import org.jfree.data.xy.XYSeries;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExperimentModel {

    public void experiment001() throws IOException {
        String name = "Эксперимент 001. Добавление только связей";
        log.debug(name);
        log.debug("Изначально: Случайный граф, 20 узлов, 40 связей");
        log.debug("Действие: Добавление ");
        double[][] matrix = MatrixHelper.generateMatrix(20, 40, 0.78d, 0.98d);


        XYSeries aMinSeries = new XYSeries("AMax");
        XYSeries aMaxSeries = new XYSeries("AMin");
        XYSeries edgesSeries = new XYSeries("Edges");
        XYSeries pointsSeries = new XYSeries("Points");

        int step = 1;
        double min = 10000;
        double max = -10000;
        try {
            while (MatrixHelper.existsZeros(matrix)) {
                double[][] matrixH = MatrixHelper.countMatrixHMin(matrix);
                double[][] filledMatrix = MatrixHelper.countMatrixFWMin(matrix, matrixH);
                matrix = MatrixHelper.addIntellectEdge(filledMatrix, matrix);
                double aMin = MatrixHelper.countAMin(matrix);
                double aMax = MatrixHelper.countAMax(matrix);
                max = Math.max(aMin, max);
                min = Math.min(aMax, min);
                aMinSeries.add(step, aMin);
                aMaxSeries.add(step, aMax);
                edgesSeries.add(step, MatrixHelper.countEdges(matrix));
                pointsSeries.add(step, matrix.length);
                step++;
            }
        } catch (IllegalArgumentException e) {
            log.debug("End");
        }

        ChartHelper.saveChart(List.of(aMinSeries, aMaxSeries), min, max, "pre1", "", "Шаг", "Доступность", true);
        ChartHelper.saveChart(List.of(edgesSeries), 0, MatrixHelper.countEdges(matrix)+5, "pre2", "", "Шаг", "Количество связей", false);
        ChartHelper.saveChart(List.of(pointsSeries), 0, matrix.length + 5, "pre3", "", "Шаг", "Количество узлов", false);
        ChartHelper.mergeImages(List.of("pre3.png", "pre2.png", "pre1.png"), name + ".png");
    }

    public void experiment002() throws IOException {
        String name = "Эксперимент 002. Добавление только связей";
        log.debug(name);
        log.debug("Изначально: Случайный граф, 20 узлов, 40 связей");
        log.debug("Действие: Добавление ");
        double matrix[][] = MatrixHelper.generateMatrix(17, 20, 0.78d, 0.98d);
        log.debug(MatrixHelper.matrixStr(matrix));

        Map<Double, Double> aMaxResult = new HashMap<>();
        Map<Double, Double> aMinResult = new HashMap<>();

        try {
            while (MatrixHelper.existsZeros(matrix)) {
//                double[][] matrixH = MatrixHelper.countMatrixHMin(matrix);
//                double[][] filledMatrix = MatrixHelper.countMatrixFWMin(matrix, matrixH);
//                matrix = MatrixHelper.addIntellectEdge(filledMatrix, matrix);
                matrix = MatrixHelper.addOneRandomEdge(matrix);
                aMaxResult.put((double) MatrixHelper.countEdges(matrix), MatrixHelper.countAMax(matrix));
                aMinResult.put((double) MatrixHelper.countEdges(matrix), MatrixHelper.countAMin(matrix));
            }
        } catch (IllegalArgumentException e) {
            log.debug("End");
        }

        ChartHelper.saveChart(aMaxResult, aMinResult, name);
    }

}
