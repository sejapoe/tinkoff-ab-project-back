package edu.tinkoff.ninjamireaclone.config;

import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {
    public static final long NEWS_ROOT_ID = -2L;
    public static final long ROOT_ID = -1L;
    public static final int NUM_COURSES = 6;

    private final EntityManager entityManager;
    private final SectionRepository sectionRepository;
    private final RoleRepository roleRepository;
    private final SectionRightsRepository sectionRightsRepository;
    private final PrivilegeRepository privilegeRepository;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private Section root;
    private Privilege defaultPrivilege;
    private Privilege createSubjectPrivilege;

    private Role userRole;
    private Role moderatorRole;
    private Role adminRole;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initRoles();
        initRoot();
        initCourses();
        initAdmin();
    }

    private void initRoles() {
        defaultPrivilege = createPrivilegeIfDoesntExists("DEFAULT");
        var viewPrivilege = createPrivilegeIfDoesntExists("VIEW");

        // comment privileges
        var deleteCommentPrivilege = createPrivilegeIfDoesntExists("DELETE_COMMENT");
        var auditCommentPrivilege = createPrivilegeIfDoesntExists("AUDIT_COMMENT");
        var createCommentPrivilege = createPrivilegeIfDoesntExists("CREATE_COMMENT");

        // topic privileges
        var deleteTopicPrivilege = createPrivilegeIfDoesntExists("DELETE_TOPIC");
        var createTopicPrivilege = createPrivilegeIfDoesntExists("CREATE_TOPIC");

        // domain based privileges
        createSubjectPrivilege = createPrivilegeIfDoesntExists("CREATE_SUBJECT");
        var deleteSubjectPrivilege = createPrivilegeIfDoesntExists("DELETE_SUBJECT");

        var createNewsPrivilege = createPrivilegeIfDoesntExists("CREATE_NEWS");

        // user based privileges
        var banUserPrivilege = createPrivilegeIfDoesntExists("BAN_USER");
        var auditUserPrivilege = createPrivilegeIfDoesntExists("AUDIT_USER");
        var manageRolesPrivilege = createPrivilegeIfDoesntExists("MANAGE_ROLES");

        // system settings
        var manageJobsPrivilege = createPrivilegeIfDoesntExists("MANAGE_JOBS");

        userRole = createRoleIfDoesntExists("ROLE_USER",
                defaultPrivilege,
                viewPrivilege,
                createCommentPrivilege,
                createTopicPrivilege
        );

        moderatorRole = createRoleIfDoesntExists("ROLE_MODERATOR",
                createNewsPrivilege,
                auditCommentPrivilege,
                deleteCommentPrivilege,
                deleteTopicPrivilege
        );

        adminRole = createRoleIfDoesntExists("ROLE_ADMIN",
                createSubjectPrivilege,
                deleteSubjectPrivilege,
                banUserPrivilege,
                auditUserPrivilege,
                manageRolesPrivilege,
                manageJobsPrivilege
        );
    }

    private Privilege createPrivilegeIfDoesntExists(String name) {
        return privilegeRepository.findByName(name).orElseGet(() -> {
            var privilege = new Privilege();
            privilege.setName(name);
            return privilegeRepository.save(privilege);
        });
    }

    private Role createRoleIfDoesntExists(String name, Privilege... privileges) {
        List<Privilege> privilegeList = new ArrayList<>(List.of(privileges));

        var createdRole = roleRepository.findByName(name).orElseGet(() -> {
            var role = new Role();
            role.setName(name);
            role.setPrivileges(privilegeList);
            return roleRepository.save(role);
        });

        boolean arePrivilegesMatched = new HashSet<>(createdRole.getPrivileges()).containsAll(privilegeList);
        if (!arePrivilegesMatched) {
            createdRole.setPrivileges(privilegeList);
            return roleRepository.save(createdRole);
        }
        return createdRole;
    }

    private void initRoot() {
        root = sectionRepository.findById(ROOT_ID).orElseGet(() -> {
            entityManager.createNativeQuery("INSERT INTO section (id, name) VALUES (?, ?)")
                    .setParameter(1, ROOT_ID)
                    .setParameter(2, "root")
                    .executeUpdate();

            return sectionRepository.getReferenceById(ROOT_ID);
        });

        initRootRights();
    }

    private void initRootRights() {
        if (sectionRightsRepository.existsBySection_Id(root.getId())) {
            return;
        }

        SectionRights rights = new SectionRights();
        rights.setRights(new Rights(false, false));
        rights.setSection(root);
        rights.setPrivilege(defaultPrivilege);
        sectionRightsRepository.save(rights);
    }

    private void initCourses() {
        List<Section> subsections = root.getSubsections();
        if (Objects.nonNull(subsections) && !subsections.isEmpty()) return;
        for (int i = 1; i <= NUM_COURSES; i++) {
            var course = createCourseIfDoesntExists(i);
            initCourseRights(course);
        }
    }

    private Section createCourseIfDoesntExists(int number) {
        Section course = new Section();
        course.setParent(root);
        course.setName("%d курс".formatted(number));
        return sectionRepository.save(course);
    }

    private void initCourseRights(Section course) {
        if (sectionRightsRepository.existsBySection_Id(course.getId())) return;

        SectionRights rights = new SectionRights();
        rights.setRights(new Rights(true, false));
        rights.setSection(course);

        rights.setPrivilege(createSubjectPrivilege);
        sectionRightsRepository.save(rights);
    }

    private void initAdmin() {
        boolean adminExists = accountRepository.exists(QAccount.account.roles.any().id.eq(adminRole.getId()));
        if (adminExists) {
            return;
        }

        var account = new Account();
        account.setName("admin");
        account.setDisplayName("Администратор");
        account.setDescription("Царь во дворца, царь во дворца.\nХоди то, делай сюда");
        account.setPassword(passwordEncoder.encode("admin"));
        account.setGender(Gender.APACHE_HELICOPTER);
        account.setRoles(List.of(userRole, moderatorRole, adminRole));
        account.setEnabled(true);
        accountRepository.save(account);
    }
}
