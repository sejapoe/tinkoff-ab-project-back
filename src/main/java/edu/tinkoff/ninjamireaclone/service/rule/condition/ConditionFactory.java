package edu.tinkoff.ninjamireaclone.service.rule.condition;

import java.util.List;

/**
 * The ConditionFactory interface represents a factory for creating Condition objects.
 * It allows for the creation of custom conditions based on specific requirements.
 *
 * @param <T> the type of object that the condition operates on
 */
public interface ConditionFactory<T> {
    String getConditionName();

    Condition<T> create(List<String> args);
}
