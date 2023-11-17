package edu.tinkoff.ninjamireaclone.service.rule;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.RuleSetRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.rule.action.Action;
import edu.tinkoff.ninjamireaclone.service.rule.action.ActionResolver;
import edu.tinkoff.ninjamireaclone.service.rule.condition.Condition;
import edu.tinkoff.ninjamireaclone.service.rule.condition.ConditionResolver;
import edu.tinkoff.ninjamireaclone.service.rule.condition.ConditionTrigger;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RuleService {
    private final Logger logger = LoggerFactory.getLogger(RuleService.class);
    private final RuleSetRepository ruleSetRepository;
    private final ConditionResolver conditionResolver;
    private final ActionResolver actionResolver;
    private final SectionRepository sectionRepository;

//    private final Table<Class<?>, Long, Entry<?>> updateTriggers = HashBasedTable.create(3, 8);

    /**
     * Map of rules that should be applied when section is created
     * <p>
     * Key is entity type;
     * Value is list of rules
     * <p>
     * Rules are applied in order {@link Rule#getOrder()}
     */
    private final Multimap<Class<?>, Entry<?>> createTriggers = HashMultimap.create(3, 8);

    /**
     * Creates entry from condition and actions
     * <p>
     * Checks if condition and actions have same entity type
     * <p>
     * Casts condition and actions to entity type, specified as generic parameter
     *
     * @param condition the condition to test ({@link Condition})
     * @param actions   the actions to accept ({@link Action})
     * @param <T>       entity type ({@link Section}, {@link Topic}, {@link User}, etc.)
     * @return entry with condition and actions
     * @throws RuleException if condition and actions have different entity types
     */
    private static <T> Entry<T> createEntry(Condition<?> condition, List<? extends Action<?>> actions) {
        if (!actions.stream().allMatch(action -> action.getType().equals(condition.getType())))
            throw new RuleException("Condition and Actions in one ruleset should have same entity type.");

        @SuppressWarnings("unchecked")
        Condition<T> castCondition = (Condition<T>) condition;

        @SuppressWarnings("unchecked")
        List<Action<T>> castActions = actions.stream().map(action -> ((Action<T>) action)).toList();

        return new Entry<>(castCondition, castActions);
    }

    /**
     * Registers all rulesets from database
     * <p>
     * Logs error if ruleset is not valid
     * (condition or action is not valid or has wrong entity type)
     * and skips it
     * (other rulesets will be registered)
     * <p>
     * Should be called after application startup
     *
     * @see #registerRuleSet(RuleSet)
     */
    @PostConstruct
    public void init() {
        ruleSetRepository.findAll().forEach(ruleSet -> {
            try {
                registerRuleSet(ruleSet);
            } catch (RuleException e) {
                logger.atError()
                        .setMessage("Failed register RuleSet[%d] \"%s\""
                                .formatted(ruleSet.getId(), ruleSet.getName()))
                        .setCause(e)
                        .log();
            }
        });
    }

    /**
     * Registers ruleset
     * <p>
     * Creates entry from condition and actions
     * <p>
     * Adds entry to triggers map
     *
     * @param ruleSet the ruleset to register
     * @throws RuleException if ruleset is not valid (condition or action is not valid or has wrong entity type)
     */
    private void registerRuleSet(RuleSet ruleSet) {
        var rawCondition = ruleSet.getCondition();
        var condition = conditionResolver.resolve(rawCondition);
        var actions = ruleSet.getRules().stream().map(rule -> actionResolver.resolve(rule.getAction())).toList();
        var entry = createEntry(condition, actions);

        if (condition.getTrigger().equals(ConditionTrigger.CREATED)) {
            createTriggers.put(entry.condition.getType(), entry);
        }
    }

    /**
     * Creates ruleset
     * <p>
     * Registers ruleset
     * <p>
     * Should be called to create ruleset from API, because it checks if ruleset is valid and registers it
     *
     * @param ruleSet the ruleset to create
     * @return created ruleset
     * @throws RuleException if ruleset is not valid (condition or action is not valid or has wrong entity type)
     */
    @Transactional
    public RuleSet create(RuleSet ruleSet) {
        var _ruleSet = ruleSetRepository.save(ruleSet);
        registerRuleSet(_ruleSet);
        return _ruleSet;
    }

    /**
     * Handles section creation
     * <p>
     * Applies all rules from triggers map to section
     * <p>
     * Should be called after section creation
     *
     * @param section the section to handle
     * @return handled section
     */
    @SuppressWarnings("unchecked")
    public Section handleSectionCreated(Section section) {
        var _section = section;
        for (Entry<?> entry : createTriggers.get(Section.class)) {
            _section = handleSectionEntry(_section, (Entry<Section>) entry);
        }
        return _section;
    }

    /**
     * Handles section rule
     * <p>
     * Applies actions to section if condition is true
     * <p>
     * Internal method for {@link RuleService#handleSectionCreated(Section)} and {@link RuleService#handleSectionUpdated(Section)}
     *
     * @param section the section to handle
     * @return handled section
     */
    private Section handleSectionEntry(Section section, Entry<Section> entry) {
        if (entry.condition().test(section)) { // check if condition is true for this section
            entry.actions.forEach(sectionAction -> sectionAction.accept(section)); // apply action to this section
        }
        return sectionRepository.save(section); // save changes
    }

    /**
     * Represents entry in triggers map
     * <p>
     * Contains condition and list of actions
     * <p>
     * Condition and actions have same entity type
     *
     * @param <T> entity type ({@link Section}, {@link Topic}, {@link User}, etc.)
     * @see RuleService#createEntry(Condition, List)
     */
    private record Entry<T>(Condition<T> condition, List<Action<T>> actions) {
    }
}
