package ru.matveev.model.immit.entity;

import org.jfree.data.xy.XYSeries;
import ru.matveev.model.utils.MatrixCountHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Runner {

    public static double experiment(double[][] matrix, int intensity, int averageDelay, int directiveTime, long expTime) throws InterruptedException {
        List<Switcher> switchers = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            switchers.add(new Switcher("" + i, averageDelay));
        }
        NetworkHelper networkHelper = new NetworkHelper(switchers, directiveTime, matrix);
        List<Source> sources = switchers.stream().map(sw -> new Source(sw, 1000/intensity, networkHelper)).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(switchers.size() + sources.size());
        switchers.forEach(executorService::submit);
        sources.forEach(executorService::submit);
        Thread.sleep(expTime);
        executorService.shutdownNow();
        networkHelper.countResult();
        double dMin = MatrixCountHelper.normDistKolmagorovP1(networkHelper.getFWMatrix());
        double availability = MatrixCountHelper.countAvailabilityLinearRight(dMin, matrix.length);
        System.out.println(availability);
        return availability;
    }

}
