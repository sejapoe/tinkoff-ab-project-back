package edu.tinkoff.ninjamireaclone.service.rule.condition;

import java.util.function.Predicate;

public interface Condition<T> extends Predicate<T> {
    Class<T> getType();

    ConditionTrigger getTrigger();
}
