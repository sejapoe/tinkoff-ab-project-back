package edu.tinkoff.ninjamireaclone.service.rule.action;

import java.util.function.Consumer;

/**
 * Represents an action that can be performed on an object of type T.
 *
 * @param <T> the type of object on which the action will be performed
 */
public interface Action<T> extends Consumer<T> {
    Class<T> getType();
}
