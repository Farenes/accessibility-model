package ru.matveev.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class MetaExperimentResult {
    private double[][] startMatrix;
    private double startAMin;
    private double resultAMin;
    private double startAMax;
    private double resultAMax;
    private double[][] resultMatrix;
    private double spanningAMax;
    private double deltaAMax;
    private double deltaAMin;
    private int increaseStep;
    private int finalStep;
    private int vertexes;
    private int edges;
    private Map<Integer, Double> aMinMap;
    private Map<Integer, Double> aMaxMap;

}
