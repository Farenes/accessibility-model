package ru.matveev.model.entity.generators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.matveev.model.utils.MatrixEditorHelper;

@RequiredArgsConstructor
@Slf4j
public class ConnectedMatrixGenerator implements MatrixGenerator {

    private final int vertexes;
    private final double connected;

    @Override
    public double[][] generate() {
        int maxEdges = vertexes * (vertexes - 1) / 2;
        int minEdges = vertexes;
        int edges = (int) Math.round(connected * (maxEdges - minEdges) + minEdges);
        return MatrixEditorHelper.generateMatrix(vertexes, edges, 0.85, 0.99);
    }
}