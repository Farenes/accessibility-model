package ru.matveev.model.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.matveev.model.entity.ChartData;
import ru.matveev.model.entity.generators.MatrixGenerator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartHelper {

    public static void getGist(String name, List<double[][]> matrixes, int scope) throws IOException {
        int count = matrixes.size();
        Map<Double, Double> dataMap = new HashMap<>();
        List<Double> result = new ArrayList<>();
        for (double[][] matrix: matrixes) {
            result.add(MatrixCountHelper.countAMax(matrix));
        }

        double min = result.stream().mapToDouble(Double::doubleValue).min().orElse(0d);
        double max = result.stream().mapToDouble(Double::doubleValue).max().orElse(1d);

        double maxY = -1000;

        double step = (max - min) / scope;
        for (int i=1; i<scope; i++) {
            int k = i;
            double val = min + (step*k);
            long num = result.stream().mapToDouble(Double::doubleValue).filter(d -> d > min + (step*(k-1)) && d < min + (step*k)).count();
            double p = num / (double) count;
            dataMap.put(val,  p);
            if (p > maxY) {
                maxY = p;
            }
        }

        XYSeries serie = new XYSeries("gist");
        dataMap.forEach(serie::add);
        saveChartToFile(new ChartData().setFileName(name).setSeries(List.of(serie)).setMaxAxesVal(0d).setMaxAxesVal(maxY+0.02));
    }

    public static void saveChartToFile(ChartData chartData) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries serie: chartData.getSeries()) {
            dataset.addSeries(serie);
        }

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                chartData.getChartTitle(),
                chartData.getXName(),
                chartData.getYName(),
                dataset,
                PlotOrientation.VERTICAL,
                chartData.isShowLegend(),true,false);

        XYPlot plot = (XYPlot) lineChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.getRenderer(0).setSeriesPaint(3, Color.BLACK);
        plot.getRenderer(0).setSeriesPaint(4, Color.ORANGE);

        plot.getRangeAxis().setRange(chartData.getMinAxesVal(), chartData.getMaxAxesVal());

        OutputStream out = new FileOutputStream(chartData.getFileName());

        ChartUtilities.writeChartAsPNG(out,
                lineChart,
                chartData.getChartWidth(),
                chartData.getChartHeight());
    }

    public static void mergeImages(List<String> imageFiles, String finalImage) throws IOException {
        int heightTotal = 0;
        int maxWidth = 100;

        List<BufferedImage> images = new ArrayList<>();
        for (String file : imageFiles) {
            BufferedImage image = ImageIO.read(new File(file));
            images.add(image);
        }
        for (BufferedImage bufferedImage : images) {
            heightTotal += bufferedImage.getHeight();
            if (bufferedImage.getWidth() > maxWidth) {
                maxWidth = bufferedImage.getWidth();
            }
        }

        BufferedImage concatImage = new BufferedImage(maxWidth, heightTotal, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = concatImage.createGraphics();
        int heightCurr = 0;
        for (BufferedImage bufferedImage : images) {
            g2d.drawImage(bufferedImage, 0, heightCurr, null);
            heightCurr += bufferedImage.getHeight();
        }

        File compressedImageFile = new File(finalImage);
        OutputStream outputStream = new FileOutputStream(compressedImageFile);

        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("png").next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);

        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(0.9f);

        imageWriter.write(null, new IIOImage(concatImage, null, null), imageWriteParam);

        outputStream.close();
        imageOutputStream.close();
        imageWriter.dispose();
    }

}
