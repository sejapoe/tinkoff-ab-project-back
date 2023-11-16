package edu.tinkoff.ninjamireaclone.service.rule;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import edu.tinkoff.ninjamireaclone.model.RuleSet;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.RuleRepository;
import edu.tinkoff.ninjamireaclone.repository.RuleSetRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.rule.action.Action;
import edu.tinkoff.ninjamireaclone.service.rule.action.ActionResolver;
import edu.tinkoff.ninjamireaclone.service.rule.condition.Condition;
import edu.tinkoff.ninjamireaclone.service.rule.condition.ConditionResolver;
import edu.tinkoff.ninjamireaclone.service.rule.condition.ConditionTrigger;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RuleService {
    private final RuleSetRepository ruleSetRepository;
    private final RuleRepository ruleRepository;
    private final ConditionResolver conditionResolver;
    private final ActionResolver actionResolver;

//    /**
//     * Таблица, в которой хранятся ConditionExecutor'ы для изменений сущностей. <br/>
//     * Ряды - TriggerType - тип сущности, изменение которой вызывает срабатывание. <br/>
//     * Колонки - Long - ID сущности, изменение которой вызывает срабатывание. <br/>
//     * Ячейки - ConditionExecutor - непосредственно проверяет выполнение условия
//     */
//    private final Table<Class<?>, Long, Entry<?>> updateTriggers = HashBasedTable.create(3, 8);

    private final Multimap<Class<?>, Entry<?>> createTriggers = HashMultimap.create(3, 8);
    private final SectionRepository sectionRepository;

    private static <T> Entry<T> createEntry(Condition<?> condition, List<? extends Action<?>> actions) {
        if (!actions.stream().allMatch(action -> action.getType().equals(condition.getType())))
            throw new RuleException("Condition and Actions in one ruleset should have same entity type.");

        @SuppressWarnings("unchecked")
        Condition<T> castCondition = (Condition<T>) condition;

        @SuppressWarnings("unchecked")
        List<Action<T>> castActions = actions.stream().map(action -> ((Action<T>) action)).toList();

        return new Entry<>(castCondition, castActions);
    }

    @PostConstruct
    public void init() {
        ruleSetRepository.findAll().forEach(this::registerRuleSet);
    }

    private void registerRuleSet(RuleSet ruleSet) {
        var rawCondition = ruleSet.getCondition();
        var condition = conditionResolver.resolve(rawCondition);
        var actions = ruleSet.getRules().stream().map(rule -> actionResolver.resolve(rule.getAction())).toList();
        var entry = createEntry(condition, actions);

        if (condition.getTrigger().equals(ConditionTrigger.CREATED)) {
            createTriggers.put(entry.condition.getType(), entry);
        }
    }

    public RuleSet create(RuleSet ruleSet) {
        var _ruleSet = ruleSetRepository.save(ruleSet);
        registerRuleSet(_ruleSet);
        return _ruleSet;
    }

    @SuppressWarnings("unchecked")
    public Section handleSectionCreated(Section section) {
        var _section = section;
        for (Entry<?> entry : createTriggers.get(Section.class)) {
            _section = handleSectionEntry(_section, (Entry<Section>) entry);
        }
        return _section;
    }

    private Section handleSectionEntry(Section section, Entry<Section> entry) {
        if (entry.condition().test(section)) { // check if condition is true for this section
            entry.actions.forEach(sectionAction -> sectionAction.accept(section)); // apply action to this section
        }
        return sectionRepository.save(section); // save changes
    }

    private record Entry<T>(Condition<T> condition, List<Action<T>> actions) {
    }
}
