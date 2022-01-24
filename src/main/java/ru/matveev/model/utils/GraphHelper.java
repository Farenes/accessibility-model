package ru.matveev.model.utils;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphHelper {

    public static void visualizeGraph(double[][] matrix, String name) throws IOException {
        DirectedSparseGraph<String, GraphEdge> g = new DirectedSparseGraph<>();

        for (int i=0; i<matrix.length; i++) {
            g.addVertex(""+i);
        }

        for (int i=0; i<matrix.length; i++) {
            for (int j=i+1; j<matrix.length; j++) {
                if (matrix[i][j] != 0) {
                    g.addEdge(new GraphEdge(String.format("%.2f", matrix[i][j])), ""+i, ""+j);
                    g.addEdge(new GraphEdge(String.format("%.2f", matrix[j][i])), ""+j, ""+i);
                }
            }
        }

        VisualizationViewer<String, GraphEdge> vv =
                new VisualizationViewer<>(
                        new CircleLayout<>(g), new Dimension(1200,800));

        VisualizationImageServer<String, GraphEdge> vis =
                new VisualizationImageServer<>(vv.getGraphLayout(),
                        vv.getGraphLayout().getSize());

        vis.setBackground(Color.WHITE);
        vis.getRenderContext().setEdgeLabelTransformer(GraphEdge::getVal);
        vis.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<>());
        vis.getRenderContext().setVertexLabelTransformer(s -> s);
        vis.getRenderContext().setVertexFillPaintTransformer(s -> Color.WHITE);
        vis.getRenderContext().setVertexShapeTransformer(s -> new Ellipse2D.Double(-15, -15, 30, 30));

        vis.getRenderer().getVertexLabelRenderer()
                .setPosition(Renderer.VertexLabel.Position.CNTR);

        BufferedImage image = (BufferedImage) vis.getImage(
                new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
                        vv.getGraphLayout().getSize().getHeight() / 2),
                new Dimension(vv.getGraphLayout().getSize()));

        try {
            ImageIO.write(image, "PNG", new File(name + ".png"));
        } catch (IOException e) {
            // Exception handling
        }
    }

}
