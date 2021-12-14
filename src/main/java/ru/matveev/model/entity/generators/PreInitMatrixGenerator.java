package ru.matveev.model.entity.generators;

import ru.matveev.model.utils.MatrixEditorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Генератор с преинициализированным набором матриц,
 * чтобы была возможность в разные эксперимент передать один набор матриц
 */
public class PreInitMatrixGenerator implements MatrixGenerator {

    private final List<double[][]> matrixes;
    private int caret = 0;

    public PreInitMatrixGenerator(int count, int vertexes, int edges) {
        matrixes = new ArrayList<>();
        for (int i=0; i<count; i++) {
            matrixes.add(MatrixEditorHelper.generateMatrix(vertexes, edges, 0.5, 0.9));
        }
    }

    public void reset() {
        caret = 0;
    }

    @Override
    public double[][] generate() {
        if (caret >= matrixes.size()) {
            caret = 0;
        }
        return matrixes.get(caret++);
    }
}
