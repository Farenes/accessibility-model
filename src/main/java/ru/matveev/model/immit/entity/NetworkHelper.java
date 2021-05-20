package ru.matveev.model.immit.entity;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
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
        paths = countPaths(matrix);
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

    public static List<Integer>[][] countPaths(double[][] nearMatrix) {
        List<Integer>[][] paths = new List[nearMatrix.length][nearMatrix.length];

        double[][] matrixFW = MatrixUtils.copyMatrix(nearMatrix);
        for (int i=0; i<matrixFW.length-1; i++) {
            for (int j=i+1; j<matrixFW.length; j++) {
                if (nearMatrix[i][j] > 0) {
                    paths[i][j] = Stream.of(j).collect(Collectors.toList());
                    paths[j][i] = Stream.of(i).collect(Collectors.toList());
                }
            }
        }

        for (int k=0; k<matrixFW.length; k++) {
            for (int i=0; i<matrixFW.length; i++) {
                for (int j=0; j<matrixFW.length; j++) {
                    if (i != k && j != k
                            && matrixFW[i][k] > 0d
                            && matrixFW[k][j] > 0d
                            && matrixFW[i][j] < matrixFW[k][j] * matrixFW[i][k]) {
                        matrixFW[i][j] = matrixFW[k][j] * matrixFW[i][k];
                        paths[i][j] = new ArrayList<>();
                        paths[i][j].addAll(paths[i][k]);
                        paths[i][j].addAll(paths[k][j]);
                    }
                }
            }
        }

        return paths;
    }

}
