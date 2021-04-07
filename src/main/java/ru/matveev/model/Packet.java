package ru.matveev.model;

import java.util.Date;
import java.util.List;
import java.util.Random;


public class Packet {

    private final static Random rand = new Random();

    private int id;
    private long timeCreation;
    private long timeDeath;
    private int tick;
    private List<Switcher> path;

    public Packet(List<Switcher> path) {
        this.path = path;

        id = rand.nextInt();
        timeCreation = new Date().getTime();
        tick = 0;
    }

    public boolean isLastPoint(Switcher switcher) {
        return tick == path.size()-1 && switcher.equals(path.get(path.size()-1));
    }

    public void end() {
        timeDeath = new Date().getTime();
    }

    public long getLifeTime() {
        return timeDeath - timeCreation;
    }

    public Switcher getNextPathAndInc() {
        tick++;
        return path.get(tick);
    }

}
