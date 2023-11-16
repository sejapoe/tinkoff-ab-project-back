package edu.tinkoff.ninjamireaclone.service.rule.action;

import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class ActionResolver {
    private final Pattern commonActionPattern = Pattern.compile("(?<name>\\w+)\\((?<args>.*?)\\)");
    private final Map<String, ActionFactory<?>> actionFactories = new HashMap<>();

    public ActionResolver(List<ActionFactory<?>> actionFactories) {
        actionFactories.forEach(actionFactory -> {
            this.actionFactories.put(actionFactory.getActionName(), actionFactory);
        });
    }

    public Action<?> resolve(String action) {
        var parsedAction = parseCondition(action);
        var factory = actionFactories.get(parsedAction.name);
        if (Objects.isNull(factory)) throw new RuleException("Unknown action: \"%s\"".formatted(parsedAction.name));
        return factory.create(parsedAction.args);
    }

    private ParsedAction parseCondition(String condition) {
        var matcher = commonActionPattern.matcher(condition);
        if (!matcher.matches()) throw new RuleException("Unable to parse condition \"%s\"".formatted(condition));
        var conditionName = matcher.group("name");
        var rawArgs = matcher.group("args");
        return new ParsedAction(
                conditionName,
                List.of(rawArgs.split(", ?"))
        );
    }

    private record ParsedAction(
            String name,
            List<String> args
    ) {
    }
}
