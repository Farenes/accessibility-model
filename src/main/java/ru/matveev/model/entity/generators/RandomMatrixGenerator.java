package ru.matveev.model.entity.generators;

import lombok.RequiredArgsConstructor;
import ru.matveev.model.utils.MatrixEditorHelper;

@RequiredArgsConstructor
public class RandomMatrixGenerator implements MatrixGenerator {

    private final int vertexes;
    private final int edges;

    @Override
    public double[][] generate() {
        return MatrixEditorHelper.generateMatrix(vertexes, edges, 0.7, 0.99);
    }
}
