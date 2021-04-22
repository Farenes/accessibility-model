package ru.matveev.model.entity;

@FunctionalInterface
public interface EndingCondition {

    boolean isEnd(StepResult stepResult);

}
