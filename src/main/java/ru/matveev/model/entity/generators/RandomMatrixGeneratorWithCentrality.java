package ru.matveev.model.entity.generators;

import lombok.RequiredArgsConstructor;
import ru.matveev.model.utils.BetweennessCentralityMatrixEditorHelper;

@RequiredArgsConstructor
public class RandomMatrixGeneratorWithCentrality implements MatrixGenerator {

    private final int vertexes;
    private final int edges;

    @Override
    public double[][] generate() {
        return BetweennessCentralityMatrixEditorHelper.generateMatrix(vertexes, edges, 0.85, 0.99);
    }
}
