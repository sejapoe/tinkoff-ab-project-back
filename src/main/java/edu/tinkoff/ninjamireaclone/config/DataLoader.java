package edu.tinkoff.ninjamireaclone.config;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRightsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    public static final int NUM_COURSES = 6;
    private final SectionRepository sectionRepository;
    private final RoleRepository roleRepository;
    private final SectionRightsRepository sectionRightsRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createRoles();
        createCourses(getRoot());
        createRights();
    }

    private void createCourses(Section root) {
        List<Section> subsections = root.getSubsections();
        if (Objects.nonNull(subsections) && !subsections.isEmpty()) return;
        for (int i = 1; i <= NUM_COURSES; i++) {
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
                .orElseGet(this::createRoot);
    }

    @NotNull
    private Section createRoot() {
        Section root = new Section();
        root.setParent(null);
        root.setName("root");
        return sectionRepository.save(root);
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

    private void createRights() {
        Section root = getRoot();
        setRootRights(root);
        for (Section course : sectionRepository.findAll(QSection.section.parent.id.eq(root.getId()))) {
            setCourseRights(course);
        }
    }

    private void setCourseRights(Section course) {
        if (sectionRightsRepository.exists(QSectionRights.sectionRights.section.id.eq(course.getId()))) return;

        Role userRole = roleRepository.findByName("ROLE_USER").get();

        SectionRights rights = new SectionRights();
        rights.setRights(new Rights(true, false));
        rights.setSection(course);

        rights.setRole(userRole);
        sectionRightsRepository.save(rights);

        // todo: make only admin create this
    }

    private void setRootRights(Section root) {
        if (sectionRightsRepository.exists(QSectionRights.sectionRights.section.id.eq(root.getId()))) return;

        Role userRole = roleRepository.findByName("ROLE_USER").get();

        SectionRights rights = new SectionRights();
        rights.setRights(new Rights(false, false));
        rights.setSection(root);
        rights.setRole(userRole);
        sectionRightsRepository.save(rights);
    }
}
