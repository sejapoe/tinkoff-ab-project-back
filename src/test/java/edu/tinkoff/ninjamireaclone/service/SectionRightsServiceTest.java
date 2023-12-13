package edu.tinkoff.ninjamireaclone.service;

import com.google.common.collect.Lists;
import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SectionRightsServiceTest extends AbstractBaseTest {
    @Autowired
    private SectionRightsService sectionRightsService;

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private SectionRightsRepository sectionRightsRepository;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        sectionRepository.deleteAll();
        roleRepository.deleteAll();
        privilegeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "TEST_USER", authorities = {"ROLE_TEST", "TEST_PRIVILEGE"})
    void getRights() {
        // given
        var testPrivilege = PrivilegeEntity.builder()
                .name("TEST_PRIVILEGE")
                .build();


        var testRole = RoleEntity.builder()
                .name("ROLE_TEST")
                .privileges(Lists.newArrayList(testPrivilege))
                .build();

        testPrivilege.setRoles(Lists.newArrayList(testRole));
        roleRepository.save(testRole);

        var section = SectionEntity.builder().name("TEST SECTION").build();
        section = sectionRepository.save(section);

        var sectionRights = SectionRightsEntity.builder()
                .section(section)
                .privilege(testPrivilege)
                .rights(new Rights(true, true))
                .build();

        sectionRightsRepository.save(sectionRights);

        // when
        var result = sectionRightsService.getRights(section);

        // then
        assertTrue(result.getCreateSubsections());
        assertTrue(result.getCreateTopics());
    }
}