package ru.matveev.model.immit.entity;

import java.util.Random;

public class Source implements Runnable {

    private static Random RAND = new Random();

    private Switcher sink;
    private NetworkHelper helper;
    private long intense;

    public Source(Switcher sink, long intense, NetworkHelper networkHelper) {
        this.sink = sink;
        this.intense = intense;
        this.helper = networkHelper;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(intense);
                int dest = RAND.nextInt(helper.getSwitchersSize());
                while (sink.equals(helper.getSwitcher(dest))) {
                    dest = RAND.nextInt(helper.getSwitchersSize());
                }
                sink.addPacket(new Packet(helper.getPath(sink, helper.getSwitcher(dest))));
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.out.println("Error while sending packet");
                e.printStackTrace();
            }
        }
    }

}
