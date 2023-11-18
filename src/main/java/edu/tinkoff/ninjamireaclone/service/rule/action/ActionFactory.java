package edu.tinkoff.ninjamireaclone.service.rule.action;

import java.util.List;

/**
 * ActionFactory is an interface that represents a factory for creating actions.
 * It provides methods to get the action name and create an action with the given arguments.
 *
 * @param <T> the type of object on which the action will be performed
 */
public interface ActionFactory<T> {
    String getActionName();

    Action<T> create(List<String> args);
}
