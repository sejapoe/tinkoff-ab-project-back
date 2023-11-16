package edu.tinkoff.ninjamireaclone.service.rule.condition;

import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class ConditionResolver {
    private final Pattern commonConditionPattern = Pattern.compile("if (?<name>\\w+)\\((?<args>.*?)\\)");
    private final Map<String, ConditionFactory<?>> conditionFactories = new HashMap<>();

    public ConditionResolver(List<ConditionFactory<?>> conditionFactories) {
        conditionFactories.forEach(conditionFactory -> {
            this.conditionFactories.put(conditionFactory.getConditionName(), conditionFactory);
        });
    }

    public Condition<?> resolve(String condition) {
        var parsedCondition = parseCondition(condition);
        var factory = conditionFactories.get(parsedCondition.name);
        if (Objects.isNull(factory))
            throw new RuleException("Unknown condition: \"%s\"".formatted(parsedCondition.name));
        return factory.create(parsedCondition.args);
    }

    private ParsedCondition parseCondition(String condition) {
        var matcher = commonConditionPattern.matcher(condition);
        if (!matcher.matches()) throw new RuleException("Unable to parse condition \"%s\"".formatted(condition));
        var conditionName = matcher.group("name");
        var rawArgs = matcher.group("args");
        return new ParsedCondition(
                conditionName,
                List.of(rawArgs.split(", ?"))
        );
    }

    private record ParsedCondition(
            String name,
            List<String> args
    ) {
    }
}
