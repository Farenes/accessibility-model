package ru.matveev.model;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.junit.Test;
import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.generators.MatrixGenerator;
import ru.matveev.model.entity.generators.MaxSpanningTreeCounter;
import ru.matveev.model.entity.generators.PreInitMatrixGenerator;
import ru.matveev.model.entity.generators.RandomMatrixGenerator;
import ru.matveev.model.entity.steps.AddingBestAmaxEdgeStep;
import ru.matveev.model.experiment.CompareExperiment;
import ru.matveev.model.experiment.Experiment;
import ru.matveev.model.experiment.MetaSpanningTreeAlphaExperiment;
import ru.matveev.model.utils.ExperimentHelper;
import ru.matveev.model.utils.GraphHelper;
import ru.matveev.model.utils.InterestingMatrix;
import ru.matveev.model.utils.MatrixCountHelper;
import ru.matveev.model.utils.MatrixForShowing;

import java.util.ArrayList;
import java.util.List;

public class RunningExperiments {

    //14.02.2022
    @Test
    public void lastValidExperimentForResult() throws Exception {
        Experiments.makeExperimentWithResultsToFile(9, 13, 0.17d);
    }

}
