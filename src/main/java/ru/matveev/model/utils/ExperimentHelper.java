package ru.matveev.model.utils;

import org.jfree.data.xy.XYSeries;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.ChartData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentHelper {

    public static void saveResultToFile(String name, ExperimentResult result) throws IOException {
        List<String> mergeImagesFileNames = new ArrayList<>();

        if (result.getPointsSeries() != null && !result.getPointsSeries().isEmpty()) {
            ChartHelper.saveChartToFile(new ChartData()
                    .setSeries(List.of(result.getPointsSeries()))
                    .setMinAxesVal(0)
                    .setMaxAxesVal(result.getResultMatrix().get(0).length + 5)
                    .setYName("Количество узлов")
                    .setFileName("pre3.png")
                    .setChartTitle(name)
                    .setChartHeight(300)
            );
            mergeImagesFileNames.add("pre3.png");
        }

        if (result.getEdgesSeries() != null && !result.getEdgesSeries().isEmpty()) {
            ChartHelper.saveChartToFile(new ChartData()
                    .setSeries(List.of(result.getEdgesSeries()))
                    .setMinAxesVal(0)
                    .setMaxAxesVal(195)
                    .setYName("Количество связей")
                    .setFileName("pre2.png")
                    .setChartHeight(300)
            );
            mergeImagesFileNames.add("pre2.png");
        }

        List<XYSeries> series = new ArrayList<>();
        series.addAll(result.getAMaxSeries());

        ChartHelper.saveChartToFile(new ChartData()
                .setSeries(series)
                .setMinAxesVal(result.getMinAxesVal() - 0.001)
                .setMaxAxesVal(result.getMaxAxesVal() + 0.001)
                .setXName("m(k)/m(k)max, %")
                .setYName("Δ, %")
                .setFileName("pre1.png")
                .setShowLegend(true)
                .setChartHeight(600)
        );
        mergeImagesFileNames.add("pre1.png");

        ChartHelper.mergeImages(mergeImagesFileNames, name + ".png");
        deleteFile("pre3.png");
        deleteFile("pre2.png");
        deleteFile("pre1.png");
    }

    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

}
