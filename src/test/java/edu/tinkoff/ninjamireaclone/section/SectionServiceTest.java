package edu.tinkoff.ninjamireaclone.section;

import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.model.QSection;
import edu.tinkoff.ninjamireaclone.model.Rights;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.SectionRightsService;
import edu.tinkoff.ninjamireaclone.service.SectionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SectionServiceTest extends AbstractBaseTest {
    @MockBean
    SectionRightsService sectionRightsService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private SectionService sectionService;

    @BeforeEach
    public void initMocks() {
        when(sectionRightsService.getRights(any())).thenReturn(new Rights(true, true));
    }

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        sectionRepository.deleteAll(sectionRepository.findAll(QSection.section.name.startsWith("[TEST]")));
    }

    @Test
    @Transactional
    @DisplayName("Получить корневой раздел")
    public void getRoot() {
        // when
        Section result = sectionService.getRoot();

        // then
        assertThat(result.getName()).isEqualTo("root");
        assertThat(result.getSubsections()).size().isEqualTo(6);
    }

    @Test
    @Transactional
    @DisplayName("Создать курс")
    public void createCourse() {
        // given
        Section root = sectionRepository.findAll().stream()
                .filter(section -> section.getName().equals("root")).findAny().get();

        // when
        Section result = sectionService.create(root.getId(), "[TEST] 1 курс");

        // then
        assertThat(result.getName()).isEqualTo("[TEST] 1 курс");
        assertThat(result.getSubsections()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("Создать предмет")
    public void createSubject() {
        // given
        Section course1 = sectionRepository.findAll().stream()
                .filter(section -> section.getName().equals("1 курс")).findAny().get();

        // when
        Section result = sectionService.create(course1.getId(), "[TEST] Линейная алгебра");

        // then
        assertThat(result.getName()).isEqualTo("[TEST] Линейная алгебра");
        assertThat(result.getSubsections()).size().isEqualTo(3);
        assertThat(result.getSubsections().stream().map(Section::getName))
                .contains("Контрольные работы", "Конспекты семинаров", "Литература");
    }

    @Test
    @Transactional
    @DisplayName("Удалить курс")
    public void deleteCourse() {
        // given
        Section course1 = sectionService.create(sectionService.getRoot().getId(), "[TEST] 1 курс");

        // when
        sectionService.delete(course1.getId());

        // then
        assertThat(sectionRepository.existsById(course1.getId())).isFalse();
        assertThat(sectionRepository.findOne(QSection.section.name.eq("[TEST] 1 курс"))).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Обновить курс")
    public void updateCourse() {
        // given
        Section course1 = sectionService.create(sectionService.getRoot().getId(), "[TEST] 1 курс");

        // when
        Section result = sectionService.update(course1.getId(), "[TEST] 2 курс");

        // then
        assertThat(result.getName()).isEqualTo("[TEST] 2 курс");
        assertThat(sectionRepository.existsById(course1.getId())).isTrue();
        assertThat(sectionRepository.findOne(QSection.section.name.eq("[TEST] 1 курс"))).isEmpty();
        assertThat(sectionRepository.findOne(QSection.section.name.eq("[TEST] 2 курс"))).isPresent();
    }

    @Test
    @Transactional
    @DisplayName("Получить раздел по ID")
    public void getSectionById() {
        // given
        Section course1 = sectionService.create(sectionService.getRoot().getId(), "[TEST] 1 курс");

        // when
        Section result = sectionService.get(course1.getId());

        // then
        assertThat(result.getName()).isEqualTo("[TEST] 1 курс");
    }
}