package ru.matveev.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class StepResult {

    private double[][] matrix;
    private int step;
    private double aMin;
    private double aMax;
    private double[][] initMatrix;

}
