package ru.matveev.model.immit.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NetworkHelper {

    private List<Switcher> switchers;
    private List<Integer>[][] paths;
    private long derectiveTime;
    private Map<Pair<Switcher, Switcher>, Long> packetGoodTime = new ConcurrentHashMap<>();
    private Map<Pair<Switcher, Switcher>, Long> packetCount = new ConcurrentHashMap<>();

    public NetworkHelper(List<Switcher> switchers, long derectiveTime, double[][] matrix) {
        this.derectiveTime = derectiveTime;
        this.switchers = switchers;
        switchers.forEach(sw -> sw.setNetworkHelper(this));
        paths = MatrixCountHelper.countPaths(matrix);
    }

    public List<Integer>[][] getPaths() {
        return paths;
    }

    public void countResult() {
        packetGoodTime.forEach((k, v) -> log.debug("{} - {} : {}", k.getLeft().getName(), k.getRight().getName(), v/(double)packetCount.get(k)));
    }

    public double[][] getFWMatrix() {
        double[][] fwMatrix = new double[switchers.size()][switchers.size()];
        for (int i=0; i<switchers.size(); i++) {
            fwMatrix[i][i] = 1;
        }
        packetGoodTime.forEach((k, v) ->  fwMatrix[switchers.indexOf(k.getLeft())][switchers.indexOf(k.getRight())] = v/(double)packetCount.get(k));
        return fwMatrix;
    }

    public void countPacket(Packet packet) {
        packetCount.putIfAbsent(Pair.of(packet.getSource(), packet.getDest()), 0L);
        packetGoodTime.putIfAbsent(Pair.of(packet.getSource(), packet.getDest()), 0L);
        packetCount.merge(Pair.of(packet.getSource(), packet.getDest()), 1L, Long::sum);
        if (packet.getLifeTime() < derectiveTime) {
            packetGoodTime.merge(Pair.of(packet.getSource(), packet.getDest()), 1L, Long::sum);
        }
    }

    public List<Switcher> getPath(Switcher source, Switcher dest) {
        List<Switcher> path = paths[switchers.indexOf(source)][switchers.indexOf(dest)].stream().map(switchers::get).collect(Collectors.toList());
        path.add(0, source);
        return path;
    }

    public int getSwitchersSize() {
        return switchers.size();
    }

    public Switcher getSwitcher(int i) {
        return switchers.get(i);
    }

}
