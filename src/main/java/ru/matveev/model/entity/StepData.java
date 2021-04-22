package ru.matveev.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class StepData {

    double[][] matrix;

}
