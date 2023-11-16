package edu.tinkoff.ninjamireaclone.rule;

import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.RuleSetRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.SectionService;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RuleServiceTest {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private RuleSetRepository ruleSetRepository;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private RuleService ruleService;


    @Test
    public void createSubsectionWhenSubsectionCreatedTest() {
        // given
        Section course1 = sectionRepository.findAll().stream().filter(section -> section.getName().equals("1 курс")).findAny().get();
        Section algebra = new Section();
        algebra.setName("Линейная алгебра");
        algebra.setParent(course1);

        // when
        Section result = sectionService.create(algebra);

        // then
        assertThat(result.getName()).isEqualTo("Линейная алгебра");
        assertThat(result.getSubsections()).size().isEqualTo(3);
        assertThat(result.getSubsections().stream().map(Section::getName)).contains("Контрольные работы", "Конспекты семинаров", "Литература");
    }
}
