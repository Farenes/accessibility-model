package ru.matveev.model;

import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Switcher implements Runnable {

    private static final Random rand = new Random();

    private Queue<Packet> inputQueue = new ConcurrentLinkedQueue<>();
    private Map<Switcher, Double> accessTab;

    public Switcher(Map<Switcher, Double> accessTab) {
        this.accessTab = accessTab;
    }

    public void addPacket(Packet packet) {
        inputQueue.add(packet);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!inputQueue.isEmpty()) {
                    Packet packet = inputQueue.poll();
                    if (packet.isLastPoint(this)) {
                        packet.end();
                        System.out.println(packet.getLifeTime());
                        continue;
                    }
                    Switcher next = packet.getNextPathAndInc();
                    Thread.sleep(getDelayTime(next));
                    next.addPacket(packet);
                } else {
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                System.out.println("Error while packet handling");
                e.printStackTrace();
            }
        }
    }

    private long getDelayTime(Switcher switcher) {
        double val = accessTab.get(switcher);
        double r = rand.nextDouble();
        return r < val ? rand.nextInt(300) : rand.nextInt(700) + 300;
    }

}
