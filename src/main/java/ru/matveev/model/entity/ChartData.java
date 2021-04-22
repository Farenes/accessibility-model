package ru.matveev.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jfree.data.xy.XYSeries;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ChartData {

    private List<XYSeries> series;
    private double minAxesVal;
    private double maxAxesVal;
    private String fileName = "result.png";
    private String chartTitle = "";
    private String xName = "";
    private String yName = "";
    private boolean showLegend = false;
    private int chartHeight = 600;
    private int chartWidth = 1000;

}
