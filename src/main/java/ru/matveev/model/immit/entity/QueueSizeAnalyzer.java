package ru.matveev.model.immit.entity;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueueSizeAnalyzer implements Runnable {

    private List<Switcher> switchers;
    private Map<Switcher, Integer> sizes = new HashMap<>();
    private AtomicInteger measures = new AtomicInteger(0);

    public QueueSizeAnalyzer(List<Switcher> switchers) {
        this.switchers = switchers;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            measures.incrementAndGet();
            switchers.forEach(switcher -> {
                sizes.merge(switcher, switcher.getQueueSize(), Integer::sum);
            });
            Thread.sleep(1000L);
        }
    }

    public Map<String, Double> getSizes() {
        return sizes.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), entry -> entry.getValue()/(double)measures.get()));
    }
}
