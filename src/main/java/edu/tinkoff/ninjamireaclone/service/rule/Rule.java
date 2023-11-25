package edu.tinkoff.ninjamireaclone.service.rule;

public interface Rule<T> {
    Class<T> getType();

    ConditionTrigger getConditionTrigger();

    T evaluate(T object);
}
