package ru.matveev.model.immit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jfree.data.xy.XYSeries;

@Getter
@Setter
@Accessors(chain = true)
public class Result {

    private double availability;
    private XYSeries series;

}
