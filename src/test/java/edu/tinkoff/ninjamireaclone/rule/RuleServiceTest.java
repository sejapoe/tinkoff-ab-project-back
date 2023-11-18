package edu.tinkoff.ninjamireaclone.rule;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import edu.tinkoff.ninjamireaclone.model.QRuleSet;
import edu.tinkoff.ninjamireaclone.model.Rule;
import edu.tinkoff.ninjamireaclone.model.RuleSet;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.RuleSetRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.SectionService;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
public class RuleServiceTest {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private RuleSetRepository ruleSetRepository;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private SectionService sectionService;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        ruleSetRepository.deleteAll(ruleSetRepository.findAll(QRuleSet.ruleSet.name.startsWith("[TEST]")));
        ruleService.init();
    }

    @Test
    @DisplayName("Создание набора правил с неправильным действием")
    public void createRuleSetWithWrongAction() {
        // given
        Section root = sectionService.getRoot();
        RuleSet ruleSet = new RuleSet();
        ruleSet.setName("[TEST] Тестовый рулсет");
        ruleSet.setCondition("if subsection_created(%d)".formatted(root.getId()));
        Rule rule = new Rule();
        rule.setOrder(0L);
        rule.setAction("unknown()");
        rule.setRuleSet(ruleSet);
        ruleSet.setRules(List.of(rule));

        // when
        Exception e = assertThrows(Exception.class, () -> ruleService.create(ruleSet));

        // then
        assertThat(ruleSetRepository.exists(QRuleSet.ruleSet.name.eq("[TEST] Тестовый рулсет"))).isFalse();
        assertThat(e).isInstanceOf(RuleException.class);
        assertThat(e.getMessage()).isEqualTo("Unknown action: \"unknown\"");
    }

    @Test
    @DisplayName("Создание набора правил с неправильным условием")
    public void createRuleSetWithWrongCondition() {
        // given
        RuleSet ruleSet = new RuleSet();
        ruleSet.setName("[TEST] Тестовый рулсет");
        ruleSet.setCondition("if unknown()");
        Rule rule = new Rule();
        rule.setOrder(0L);
        rule.setAction("create_subsection(Тест)");
        rule.setRuleSet(ruleSet);
        ruleSet.setRules(List.of(rule));

        // when
        Exception e = assertThrows(Exception.class, () -> ruleService.create(ruleSet));

        // then
        assertThat(ruleSetRepository.exists(QRuleSet.ruleSet.name.eq("[TEST] Тестовый рулсет"))).isFalse();
        assertThat(e).isInstanceOf(RuleException.class);
        assertThat(e.getMessage()).isEqualTo("Unknown condition: \"unknown\"");
    }

    @Test
    @DisplayName("Создание валидного набора правил")
    public void createValidRuleset() {
        // given
        Section root = sectionService.getRoot();
        RuleSet ruleSet = new RuleSet();
        ruleSet.setName("[TEST] Тестовый рулсет");
        ruleSet.setCondition("if subsection_created(%d)".formatted(root.getId()));
        Rule rule = new Rule();
        rule.setOrder(0L);
        rule.setAction("create_subsection(Тест)");
        rule.setRuleSet(ruleSet);
        ruleSet.setRules(List.of(rule));

        // when
        ruleService.create(ruleSet);

        // then
        assertThat(ruleSetRepository.exists(QRuleSet.ruleSet.name.eq("[TEST] Тестовый рулсет"))).isTrue();
    }

    @Test
    @DisplayName("Невалидный набор правил при инициализации")
    public void invalidRuleSetOnInit() {
        // given
        RuleSet ruleSet = new RuleSet();
        ruleSet.setName("[TEST] Тестовый рулсет");
        ruleSet.setCondition("if unknown()");
        Rule rule = new Rule();
        rule.setOrder(0L);
        rule.setAction("create_subsection(Тест)");
        rule.setRuleSet(ruleSet);
        ruleSet.setRules(List.of(rule));
        RuleSet saved = ruleSetRepository.save(ruleSet);

        // capture logs
        var logWatcher = new ListAppender<ILoggingEvent>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(RuleService.class)).addAppender(logWatcher);

        // when
        ruleService.init();

        // then
        assertThat(logWatcher.list).anySatisfy(iLoggingEvent -> {
            assertThat(iLoggingEvent.getLevel()).isEqualTo(Level.ERROR);
            assertThat(iLoggingEvent.getMessage()).isEqualTo("Failed register RuleSet[%d] \"%s\"".formatted(saved.getId(), saved.getName()));
        });
    }
}