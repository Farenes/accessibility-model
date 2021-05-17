package ru.matveev.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jfree.data.xy.XYSeries;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ExperimentResult {

    private List<double[][]> resultMatrix;
    private List<XYSeries> aMinSeries;
    private List<XYSeries> aMaxSeries;
    private XYSeries edgesSeries;
    private XYSeries pointsSeries;
    private double minAxesVal;
    private double maxAxesVal;

}
