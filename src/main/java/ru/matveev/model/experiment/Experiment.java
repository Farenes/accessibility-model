package ru.matveev.model.experiment;

import ru.matveev.model.entity.ExperimentResult;
import ru.matveev.model.entity.InitData;

public interface Experiment {

    ExperimentResult make();
    String getName();
    String getDescription();

}
