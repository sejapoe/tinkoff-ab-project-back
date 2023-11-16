package edu.tinkoff.ninjamireaclone.config;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.RuleSetRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private static final String[] subsectionNames = {"Конспекты семинаров", "Контрольные работы", "Литература"};
    private final SectionRepository sectionRepository;
    private final RuleSetRepository ruleSetRepository;
    private final RuleService ruleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createCourses(getRoot());
        createRuleset(getRoot());
    }

    private void createCourses(Section root) {
        List<Section> subsections = root.getSubsections();
        if (Objects.nonNull(subsections) && !subsections.isEmpty()) return;
        for (int i = 1; i <= 6; i++) {
            Section course = new Section();
            course.setParent(root);
            course.setName("%d курс".formatted(i));
            sectionRepository.save(course);
        }
    }

    private void createRuleset(Section root) {
        if (ruleSetRepository.exists(QRuleSet.ruleSet.name.eq("Создание секций"))) return;

        RuleSet ruleSet = new RuleSet();
        String ids = root.getSubsections().stream()
                .map(section -> section.getId().toString())
                .collect(Collectors.joining(", "));

        ruleSet.setName("Создание секций");
        ruleSet.setCondition("if subsection_created(%s)".formatted(ids));

        AtomicLong order = new AtomicLong(0L);
        var rules = Arrays.stream(subsectionNames).map((name) -> {
            var rule = new Rule();
            rule.setOrder(order.getAndIncrement());
            rule.setRuleSet(ruleSet);
            rule.setAction("create_subsection(%s)".formatted(name));
            return rule;
        }).toList();
        ruleSet.setRules(rules);

        ruleService.create(ruleSet);
    }

    private Section getRoot() {
        Predicate predicate = ExpressionUtils.allOf(
                QSection.section.parent.isNull(),
                QSection.section.name.eq("root")
        );
        if (Objects.isNull(predicate)) predicate = Expressions.FALSE;

        return sectionRepository.findOne(predicate)
                .orElseGet(() -> {
                    Section root = new Section();
                    root.setParent(null);
                    root.setName("root");
                    return sectionRepository.save(root);
                });
    }
}
