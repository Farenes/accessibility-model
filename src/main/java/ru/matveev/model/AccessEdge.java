package ru.matveev.model;

import lombok.RequiredArgsConstructor;
import org.jgrapht.graph.DefaultWeightedEdge;

@RequiredArgsConstructor
public class AccessEdge extends DefaultWeightedEdge {

    private final double weight;

    @Override
    public double getWeight() {
        return weight;
    }
}
