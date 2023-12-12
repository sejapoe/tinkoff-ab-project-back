package edu.tinkoff.ninjamireaclone.service.rule;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.tinkoff.ninjamireaclone.model.AccountEntity;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.TopicEntity;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RuleService {
    /**
     * The EXPECTED_ENTITY_NUMBER variable represents the expected number of entities.
     * It set for 3 because 3 possible entities ({@link SectionEntity}, {@link TopicEntity}, {@link AccountEntity}) are used in the project.)
     *
     * @see RuleService#createTriggers
     */
    private static final int EXPECTED_ENTITY_NUMBER = 3;
    private static final int EXPECTED_ENTRIES_PER_KEY = 8;
    private final List<Rule<?>> rules;

//    private final Table<Class<?>, Long, Entry<?>> updateTriggers = HashBasedTable.create(3, 8);

    private final Multimap<Class<?>, Rule<?>> createTriggers = HashMultimap.create(EXPECTED_ENTITY_NUMBER, EXPECTED_ENTRIES_PER_KEY);


    /**
     * Registers all rules from beans
     */
    @PostConstruct
    @Transactional
    public void init() {
        clear();
        rules.forEach(this::registerRule);
    }

    /**
     * Clears triggers maps
     */
    private void clear() {
        createTriggers.clear();
    }


    private void registerRule(@NotNull Rule<?> rule) {
        if (rule.getConditionTrigger().equals(ConditionTrigger.CREATED)) {
            createTriggers.put(rule.getType(), rule);
        }
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
    public SectionEntity handleSectionCreated(SectionEntity section) {
        var _section = section;
        for (Rule<?> entry : createTriggers.get(SectionEntity.class)) {
            _section = ((Rule<SectionEntity>) entry).evaluate(_section);
        }
        return _section;
    }
}