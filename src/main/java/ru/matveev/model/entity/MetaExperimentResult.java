package ru.matveev.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class MetaExperimentResult {
    private double[][] resultMatrix;
    private double deltaAMax;
    private double lgDeltaAMax;
    private int increaseStep;
    private int finalStep;
    private int vertexes;
    private int edges;
    private Map<Integer, Double> aMinMap;
    private Map<Integer, Double> aMaxMap;

}
