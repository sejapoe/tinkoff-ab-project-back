package edu.tinkoff.ninjamireaclone.service.rule.action;

import java.util.List;

public interface ActionFactory<T> {
    String getActionName();

    Action<T> create(List<String> args);
}
