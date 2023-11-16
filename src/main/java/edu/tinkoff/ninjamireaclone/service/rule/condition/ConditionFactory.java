package edu.tinkoff.ninjamireaclone.service.rule.condition;

import java.util.List;

public interface ConditionFactory<T> {
    String getConditionName();

    Condition<T> create(List<String> args);
}
