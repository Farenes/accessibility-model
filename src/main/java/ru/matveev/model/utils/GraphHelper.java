package ru.matveev.model.utils;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxEdgeLabelLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphHelper {

    public static void visualizeGraph(double[][] matrix) throws IOException {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        List<Object> vertex = new ArrayList<>();
        try
        {
            for (int i=0; i<matrix.length; i++) {
                Object v1 = graph.insertVertex(parent, null, ""+i, 20, 20, 20,
                        20);
                vertex.add(v1);
            }

            for (int i=0; i<matrix.length; i++) {
                for (int j=i+1; j<matrix.length; j++) {
                    if (matrix[i][j] != 0) {
                        graph.insertEdge(parent, null, String.format("%.2f", (matrix[i][j] + matrix[j][i])/2), vertex.get(i), vertex.get(j));
                    }
                }
            }
        }
        finally
        {
            graph.getModel().endUpdate();
        }
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
//        layout.setParallelEdgeSpacing(40);
//        layout.setInterRankCellSpacing(40);
//        layout.setIntraCellSpacing(40);
        Map<String, Object> styles = graph.getStylesheet().getDefaultEdgeStyle();
        styles.remove(mxConstants.STYLE_ENDARROW);
        //styles.merge(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN, (k, v) -> v);
        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        layout.setForceConstant(120);
        layout.setDisableEdgeStyle(false);
        layout.execute(graph.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
        ImageIO.write(image, "PNG", new File("graph.png"));
    }

}
