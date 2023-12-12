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

    private SectionEntity root;
    private SectionEntity newsRoot;

    private PrivilegeEntity defaultPrivilege;
    private PrivilegeEntity createSubjectPrivilege;
    private PrivilegeEntity createNewsPrivilege;

    private RoleEntity userRole;
    private RoleEntity moderatorRole;
    private RoleEntity adminRole;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        initRoles();
        initRoot();
        initCourses();
        initNewsRoot();
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

        createNewsPrivilege = createPrivilegeIfDoesntExists("CREATE_NEWS");

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

    private PrivilegeEntity createPrivilegeIfDoesntExists(String name) {
        return privilegeRepository.findByName(name).orElseGet(() -> {
            var privilege = new PrivilegeEntity();
            privilege.setName(name);
            return privilegeRepository.save(privilege);
        });
    }

    private RoleEntity createRoleIfDoesntExists(String name, PrivilegeEntity... privileges) {
        List<PrivilegeEntity> privilegeList = new ArrayList<>(List.of(privileges));

        var createdRole = roleRepository.findByName(name).orElseGet(() -> {
            var role = new RoleEntity();
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

        SectionRightsEntity rights = new SectionRightsEntity();
        rights.setRights(new Rights(false, false));
        rights.setSection(root);
        rights.setPrivilege(defaultPrivilege);
        sectionRightsRepository.save(rights);
    }

    private void initCourses() {
        List<SectionEntity> subsections = root.getSubsections();
        if (Objects.nonNull(subsections) && !subsections.isEmpty()) return;
        for (int i = 1; i <= NUM_COURSES; i++) {
            var course = createCourseIfDoesntExists(i);
            initCourseRights(course);
        }
    }

    private SectionEntity createCourseIfDoesntExists(int number) {
        SectionEntity course = new SectionEntity();
        course.setParent(root);
        course.setName("%d курс".formatted(number));
        return sectionRepository.save(course);
    }

    private void initCourseRights(SectionEntity course) {
        if (sectionRightsRepository.existsBySection_Id(course.getId())) return;

        SectionRightsEntity rights = new SectionRightsEntity();
        rights.setRights(new Rights(true, false));
        rights.setSection(course);

        rights.setPrivilege(createSubjectPrivilege);
        sectionRightsRepository.save(rights);
    }

    private void initNewsRoot() {
        newsRoot = sectionRepository.findById(NEWS_ROOT_ID).orElseGet(() -> {
            entityManager.createNativeQuery("INSERT INTO section (id, name) VALUES (?, ?)")
                    .setParameter(1, NEWS_ROOT_ID)
                    .setParameter(2, "news_root")
                    .executeUpdate();

            return sectionRepository.getReferenceById(NEWS_ROOT_ID);
        });

        initNewsRootRights();
    }

    private void initNewsRootRights() {
        if (sectionRightsRepository.existsBySection_Id(NEWS_ROOT_ID)) {
            return;
        }

        var rights = SectionRightsEntity.builder()
                .rights(new Rights(true, false))
                .section(newsRoot)
                .privilege(createNewsPrivilege)
                .build();
        sectionRightsRepository.save(rights);
    }

    private void initAdmin() {
        boolean adminExists = accountRepository.exists(QAccountEntity.accountEntity.roles.any().id.eq(adminRole.getId()));
        if (adminExists) {
            return;
        }

        var account = new AccountEntity();
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
