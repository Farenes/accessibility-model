package ru.matveev.model.utils;

import com.itextpdf.text.PageSize;
import org.apache.commons.io.IOUtils;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.layout.plugin.random.RandomLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GephiHelper {

    public static void visualizeGraph(double[][] matrix) throws IOException {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

        List<Node> nodes = new ArrayList<>();

        for (int i=0; i<matrix.length; i++) {
            Node n = graphModel.factory().newNode("" + (i+1));
            n.setLabel("" + (i+1));
            n.setSize(1f);
            nodes.add(n);
        }

        List<Edge> edges = new ArrayList<>();

        for (int i=0; i<matrix.length; i++) {
            for (int j=0; j<matrix.length; j++) {
                if (i != j && matrix[i][j] > 0) {
                    Edge e = graphModel.factory().newEdge(nodes.get(i), nodes.get(j), 0, 1.0, false);
                    e.setLabel(String.format("%.2f", matrix[i][j]).replace(",", "."));
                    edges.add(e);
                }
            }
        }

        UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();

        nodes.forEach(undirectedGraph::addNode);
        edges.forEach(undirectedGraph::addEdge);

        AutoLayout autoLayout = new AutoLayout(10, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);

        YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(2f));

        //RandomLayout firstLayout = new RandomLayout(null, 100);

        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);

        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 500d, 0f);//500 for the complete period
        autoLayout.addLayout(firstLayout, 0.5f);
        autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty,
                repulsionProperty});
        autoLayout.execute();

        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
//        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
//        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, new Font("TimesRoman", Font.BOLD, 14));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_LABEL_FONT, new Font("TimesRoman", Font.PLAIN, 1));
//        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 2.0);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 0.1);
//        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.PINK);

        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
//        try {
//            ec.exportFile(new File("simple.png"));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return;
//        }

//        PNGExporter pngExporter = (PNGExporter) ec.getExporter("png");
//        pngExporter.setWorkspace(workspace);
//        pngExporter.setHeight(1000);
//        pngExporter.setWidth(1000);

        PDFExporter pdfExporter = (PDFExporter) ec.getExporter("pdf");
        pdfExporter.setPageSize(PageSize.A4);
        pdfExporter.setWorkspace(workspace);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ec.exportStream(baos, pdfExporter);
        byte[] pdf = baos.toByteArray();

        IOUtils.copy(new ByteArrayInputStream(pdf), new FileOutputStream(new File("my.pdf")));
    }

}
