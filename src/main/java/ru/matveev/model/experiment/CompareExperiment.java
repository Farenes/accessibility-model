package ru.matveev.model.experiment;

import lombok.RequiredArgsConstructor;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import ru.matveev.model.entity.ExperimentResult;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CompareExperiment extends BaseExperiment {

    private final String name;
    private final String description;
    private final List<Experiment> experiments;

    @Override
    public ExperimentResult make() {
        List<XYSeries> aMaxSeries = new ArrayList<>();
        List<XYSeries> aMinSeries = new ArrayList<>();
        for (int i=0; i<experiments.size(); i++) {
            int num = i;
            ExperimentResult res = experiments.get(i).make();
            res.getAMaxSeries().forEach(series -> series.setKey(series.getKey() + "_" + num));
            aMaxSeries.addAll(res.getAMaxSeries());
            res.getAMinSeries().forEach(series -> series.setKey(series.getKey() + "_" + num));
            aMinSeries.addAll(res.getAMinSeries());
        }

        return new ExperimentResult()
                .setAMaxSeries(aMaxSeries)
                .setAMinSeries(aMinSeries)
                .setMinAxesVal(findMin(aMinSeries))
                .setMaxAxesVal(findMax(aMaxSeries));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
