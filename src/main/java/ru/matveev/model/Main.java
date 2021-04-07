package ru.matveev.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        Switcher switcher2 = new Switcher(new HashMap<>());
        Map<Switcher, Double> switcher1Map = new HashMap<>();
        switcher1Map.put(switcher2, 0.96d);
        Switcher switcher1 = new Switcher(switcher1Map);

        double[][] matrix = new double[][] {{0d, 0.96d}, {0d, 0d}};
        NetworkGraph networkGraph = new NetworkGraph(List.of(switcher1, switcher2), matrix);
        Source source = new Source(switcher1, 100L, networkGraph);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(switcher2);
        executorService.submit(switcher1);
        executorService.submit(source);
    }

}
