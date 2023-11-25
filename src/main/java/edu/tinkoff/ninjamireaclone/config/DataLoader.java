package edu.tinkoff.ninjamireaclone.config;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import edu.tinkoff.ninjamireaclone.model.QRole;
import edu.tinkoff.ninjamireaclone.model.QSection;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import edu.tinkoff.ninjamireaclone.repository.RuleSetRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    private static final String[] subsectionNames = {"Конспекты семинаров", "Контрольные работы", "Литература"};
    private final SectionRepository sectionRepository;
    private final RuleSetRepository ruleSetRepository;
    private final RuleService ruleService;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createCourses(getRoot());
        createRoles();
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

    private void createRoles() {
        if (!roleRepository.exists(QRole.role.name.eq("ROLE_USER"))) {
            var defaultRole = new Role();
            defaultRole.setName("ROLE_USER");
            roleRepository.save(defaultRole);
        }
        if (!roleRepository.exists(QRole.role.name.eq("ROLE_ADMIN"))) {
            var adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
    }
}
