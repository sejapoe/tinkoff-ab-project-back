package edu.tinkoff.ninjamireaclone.service.rule.condition;

import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The ConditionResolver class is responsible for resolving condition strings and creating corresponding Condition objects.
 */
@Component
public class ConditionResolver {
    /**
     * Represents a regular expression pattern for a common condition format.
     * The pattern is used to match and parse a condition string with the following format:
     * "if {conditionName}({args})"
     * <p>
     * The pattern uses named capturing groups for easier extraction of the condition name and arguments.
     */
    private final Pattern commonConditionPattern = Pattern.compile("if (?<name>\\w+)\\((?<args>.*?)\\)");
    /**
     * This variable represents a map of condition factories.
     * Each condition factory is associated with a condition name and is responsible for creating instances of Condition objects.
     * The key of the map is the condition name, while the value is the corresponding ConditionFactory object.
     * It's filled in the constructor of the class from Spring beans.
     *
     * @see ConditionFactory
     * @see Condition
     */
    private final Map<String, ConditionFactory<?>> conditionFactories = new HashMap<>();

    public ConditionResolver(List<ConditionFactory<?>> conditionFactories) {
        conditionFactories.forEach(conditionFactory -> {
            this.conditionFactories.put(conditionFactory.getConditionName(), conditionFactory);
        });
    }

    /**
     * Resolves a condition string and returns a corresponding Condition object.
     *
     * @param condition the condition string to resolve
     * @return the resolved Condition object
     * @throws RuleException if the condition string is invalid or the condition is unknown
     */
    public Condition<?> resolve(String condition) {
        var parsedCondition = parseCondition(condition);
        var factory = conditionFactories.get(parsedCondition.name);
        if (Objects.isNull(factory))
            throw new RuleException("Unknown condition: \"%s\"".formatted(parsedCondition.name));
        return factory.create(parsedCondition.args);
    }

    /**
     * Parses a condition string and returns a ParsedCondition object.
     * The condition string should be in the format "if name(args)".
     *
     * @param condition the condition string to parse
     * @return the parsed condition
     * @throws RuleException if the condition string cannot be parsed
     * @see #commonConditionPattern
     */
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


    /**
     * Represents a parsed condition.
     * <p>
     * A {@code ParsedCondition} consists of a name and a list of arguments.
     */
    private record ParsedCondition(
            String name,
            List<String> args
    ) {
    }
}
