package ru.matveev.model;

import picocli.CommandLine;

import java.io.FileInputStream;
import java.util.Properties;

public class ExperimentMain {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        new CommandLine(options).parseArgs(args);
        Properties props = new Properties();
        props.load(new FileInputStream(options.getGraphConfigFileName()));
        Experiments.makeExperimentWithResultsToFile(options.getVertexes(), options.getEdges(), options.getProcOut());
    }

}
