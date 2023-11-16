package edu.tinkoff.ninjamireaclone.service.rule.action;

import java.util.function.Consumer;

public interface Action<T> extends Consumer<T> {
    Class<T> getType();
}
