package edu.tinkoff.ninjamireaclone.service.rule.condition;

import java.util.function.Predicate;

/**
 * The Condition interface represents a condition that can be used to evaluate a predicate on a specific type of object.
 *
 * @param <T> the type of object that the condition can be applied to
 */
public interface Condition<T> extends Predicate<T> {
    Class<T> getType();

    ConditionTrigger getTrigger();
}
