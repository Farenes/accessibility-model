package ru.matveev.model.experiment;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import java.util.List;

public abstract class BaseExperiment implements Experiment {

    protected double findMax(List<XYSeries> series) {
        double max = -10000;
        for (XYSeries s: series) {
            for (Object i: s.getItems()) {
                XYDataItem k = (XYDataItem) i;
                if (k.getYValue() > max) {
                    max = k.getYValue();
                }
            }
        }
        return max;
    }

    protected double findMin(List<XYSeries> series) {
        double min = 10000;
        for (XYSeries s: series) {
            for (Object i: s.getItems()) {
                XYDataItem k = (XYDataItem) i;
                if (k.getYValue() < min) {
                    min = k.getYValue();
                }
            }
        }
        return min;
    }

}
