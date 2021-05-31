package ru.matveev.model.immit.entity;

import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Switcher implements Runnable {

    private static final Random rand = new Random();

    private Queue<Packet> inputQueue = new ConcurrentLinkedQueue<>();
    private double averageDelay;
    private NetworkHelper networkHelper;
    private String name;

    public Switcher(String name, double averageDelay) {
        this.name = name;
        this.averageDelay = averageDelay;
    }

    public String getName() {
        return name;
    }

    public void setNetworkHelper(NetworkHelper networkHelper) {
        this.networkHelper = networkHelper;
    }

    public int getQueueSize() {
        return inputQueue.size();
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

                        networkHelper.countPacket(packet);
                        continue;
                    }
                    Switcher next = packet.getNextPathAndInc();
                    Thread.sleep(getDelayTime(next));
                    next.addPacket(packet);
                } else {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.out.println("Error while packet handling");
                e.printStackTrace();
            }
        }
    }

    private long getDelayTime(Switcher switcher) {
        double delta = rand.nextDouble() * 0.05 * averageDelay;
        return (long) (rand.nextBoolean() ? averageDelay + delta : averageDelay - delta);
    }

}
