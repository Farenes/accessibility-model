package ru.matveev.model.utils;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class GraphHelper {

    private final Properties properties;

    public GraphHelper(Properties properties) {
        this.properties = properties;
    }

    private Color convertColor(String colorStr) {
        List<Integer> list = Arrays.stream(colorStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        return new Color(list.get(0), list.get(1), list.get(2));
    }

    public void visualizeGraph(double[][] matrix, String name) throws IOException {
        int width = Integer.parseInt(properties.getProperty("width", "800"));
        int height = Integer.parseInt(properties.getProperty("height", "800"));
        Color backgroundColor = convertColor(properties.getProperty("background.color", "255,255,255"));

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

        CircleLayout<String, GraphEdge> layout = new CircleLayout<>(g);
        layout.setRadius(800 * 0.4d);

        VisualizationViewer<String, GraphEdge> vv =
                new VisualizationViewer<>(layout
                        , new Dimension(800,800));

        VisualizationImageServer<String, GraphEdge> vis =
                new VisualizationImageServer<>(vv.getGraphLayout(),
                        vv.getGraphLayout().getSize());

        vis.setBackground(Color.WHITE);
        vis.getRenderContext().setEdgeLabelTransformer(GraphEdge::getVal);
        vis.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<>());
        vis.getRenderContext().setEdgeFontTransformer(e -> new Font("Times New Roman", Font.PLAIN, 24));
        vis.getRenderContext().setVertexLabelTransformer(s -> s);
        vis.getRenderContext().setVertexFillPaintTransformer(s -> Color.WHITE);
        vis.getRenderContext().setVertexFontTransformer(s -> new Font("Times New Roman", Font.PLAIN, 24));
        vis.getRenderContext().setVertexShapeTransformer(s -> new Ellipse2D.Double(-15, -15, 60, 60));

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
