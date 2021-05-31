package ru.matveev.model.immit.entity;

import lombok.extern.slf4j.Slf4j;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class Runner {

    public static double experiment(double[][] matrix, int sourceIntensity, int handlingIntensity, int directiveTime, long expTime) throws InterruptedException {
        List<Switcher> switchers = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            switchers.add(new Switcher("" + i, 1000/(double)handlingIntensity));
        }
        NetworkHelper networkHelper = new NetworkHelper(switchers, directiveTime, matrix);
        List<Source> sources = switchers.stream().map(sw -> new Source(sw, 1000/sourceIntensity, networkHelper)).collect(Collectors.toList());
        QueueSizeAnalyzer queueAnalyzer = new QueueSizeAnalyzer(switchers);
        ExecutorService executorService = Executors.newFixedThreadPool(switchers.size() + sources.size() + 1);
        switchers.forEach(executorService::submit);
        sources.forEach(executorService::submit);
        executorService.submit(queueAnalyzer);
        Thread.sleep(expTime);
        executorService.shutdownNow();
        double[][] resultMatrix = networkHelper.getFWMatrix();
        double dMin = MatrixCountHelper.normDistKolmagorovP1(resultMatrix);
        double availability = MatrixCountHelper.countAvailabilityLinearRight(dMin, matrix.length);

        log.debug(MatrixUtils.print(resultMatrix));
        log.debug("Размеры очередей: {}", queueAnalyzer.getSizes());
        log.debug("Средний размер очередей: {}", queueAnalyzer.getSizes().values().stream().mapToDouble(Double::doubleValue).average().orElse(0));
        log.debug("aMax = {}", availability);

        for (int i=0; i<networkHelper.getPaths().length; i++) {
            for (int j=0; j<networkHelper.getPaths().length; j++) {
                if (i != j) {
                    log.debug(i + " - " + networkHelper.getPaths()[i][j].stream().map(Object::toString).collect(Collectors.joining(" - ")));
                }
            }
        }

        return availability;
    }

}
