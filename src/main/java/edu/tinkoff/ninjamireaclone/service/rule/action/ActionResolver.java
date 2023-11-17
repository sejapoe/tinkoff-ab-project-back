package edu.tinkoff.ninjamireaclone.service.rule.action;

import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This class is responsible for resolving actions based on their names and arguments.
 * It uses action factories to create actions based on the provided name and arguments.
 * The resolved action is returned for further processing.
 */
@Component
public class ActionResolver {
    /**
     * The commonActionPattern variable is a regular expression pattern used for matching and extracting information from action strings.
     * <p>
     * The pattern consists of the following parts:
     * - "(?<name>\\w+)" : This part matches and captures the action name. It uses the "\w+" pattern to match one or more word characters.
     * - "\\(" : This part matches the opening parenthesis character "(".
     * - "(?<args>.*?)" : This part matches and captures the action arguments. It uses the ".*?" pattern to match any characters (non-greedy).
     * - "\\)" : This part matches the closing parenthesis character ")".
     * <p>
     * The commonActionPattern pattern is compiled using the java.util.regex.Pattern class and is stored in a private final variable.
     * Once compiled, the pattern can be used with a Matcher object to perform matching and extraction operations on action strings.
     * <p>
     * The commonActionPattern variable should be initialized before using it to match action strings.
     */
    private final Pattern commonActionPattern = Pattern.compile("(?<name>\\w+)\\((?<args>.*?)\\)");
    private final Map<String, ActionFactory<?>> actionFactories = new HashMap<>();

    public ActionResolver(List<ActionFactory<?>> actionFactories) {
        actionFactories.forEach(actionFactory -> {
            this.actionFactories.put(actionFactory.getActionName(), actionFactory);
        });
    }

    /**
     * Resolves an action based on its name and arguments.
     *
     * @param action the action string to resolve
     * @return the resolved Action object
     * @throws RuleException if the action string is invalid or the action is unknown
     */
    public Action<?> resolve(String action) {
        var parsedAction = parseCondition(action);
        var factory = actionFactories.get(parsedAction.name);
        if (Objects.isNull(factory)) throw new RuleException("Unknown action: \"%s\"".formatted(parsedAction.name));
        return factory.create(parsedAction.args);
    }

    /**
     * Parses a condition string and returns a ParsedAction object.
     * The condition string should be in the format "name(args)".
     * The name represents the action name, and args represent the action arguments.
     *
     * @param condition the condition string to parse
     * @return a ParsedAction object containing the action name and arguments
     * @throws RuleException if the condition string cannot be parsed
     * @see #commonActionPattern
     */
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

    /**
     * ParsedAction represents a parsed action from a condition string.
     */
    private record ParsedAction(
            String name,
            List<String> args
    ) {
    }
}
