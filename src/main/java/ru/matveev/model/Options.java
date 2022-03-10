package ru.matveev.model;

import lombok.Getter;
import picocli.CommandLine;

import java.io.File;

public class Options {

    @CommandLine.Option(names = { "-v", "--vertexes" }, description = "vertexes", defaultValue = "10", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String vertexes;
    @CommandLine.Option(names = { "-e", "--edges" }, description = "vertexes", defaultValue = "16", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String edges;
    @CommandLine.Option(names = { "-proc", "--proc-out" }, description = "vertexes", defaultValue = "0.18", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String procForOut;
    @Getter
    @CommandLine.Option(names = { "-gc", "--graph-config" }, description = "graph config", defaultValue = "graph.config", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String graphConfigFileName;

    public int getEdges() {
        try {
            return Integer.parseInt(edges);
        } catch (Exception e) {
            return 10;
        }
    }

    public int getVertexes() {
        try {
            return Integer.parseInt(vertexes);
        } catch (Exception e) {
            return 10;
        }
    }

    public double getProcOut() {
        try {
            return Double.parseDouble(procForOut);
        } catch (Exception e) {
            return 0.18d;
        }
    }
}
