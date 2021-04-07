package ru.matveev.model;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
import java.util.List;
import java.util.Map;

public class ChartHelper {

    public static void saveChart(Map<Double, Double> aMinResult, Map<Double, Double> aMaxResult, String fileName) throws IOException {
        XYSeries series1 = new XYSeries("aMin");
        aMinResult.forEach(series1::add);
        XYSeries series2 = new XYSeries("aMax");
        aMaxResult.entrySet().stream().filter(val -> val.getValue() > 0).forEach(entry -> series2.add(entry.getKey(), entry.getValue()));

        double minAMin = aMinResult.values().stream().min(Double::compareTo).orElse(0d);
        double minAMax = aMaxResult.values().stream().min(Double::compareTo).orElse(0d);
        double maxAMin = aMinResult.values().stream().max(Double::compareTo).orElse(1d);
        double maxAMax = aMaxResult.values().stream().max(Double::compareTo).orElse(1d);

        saveChart(List.of(series1, series2), Math.min(minAMin, minAMax), Math.max(maxAMin, maxAMax), fileName, "", "Количество связей", "Доступность", true);
    }

    public static void saveChart(List<XYSeries> series, double minY, double maxY, String fileName, String chartName, String xName, String yName, boolean legend) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries serie: series) {
            dataset.addSeries(serie);
        }

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                chartName,
                xName,yName,
                dataset,
                PlotOrientation.VERTICAL,
                legend,true,false);

        XYPlot plot = (XYPlot) lineChart.getPlot();
//        plot.getRenderer().setSeriesStroke(0, new BasicStroke(
//                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
//                1.0f, new float[] {6.0f, 6.0f}, 0.5f
//        ));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.BLACK);

        plot.getRangeAxis().setRange(minY - 0.05, maxY + 0.05);

        OutputStream out = new FileOutputStream(fileName + ".png");

        ChartUtilities.writeChartAsPNG(out,
                lineChart,
                1000,
                600);
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
