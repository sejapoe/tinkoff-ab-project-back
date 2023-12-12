package edu.tinkoff.ninjamireaclone.service;

import com.google.common.collect.Lists;
import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.exception.ConflictException;
import edu.tinkoff.ninjamireaclone.model.AccountEntity;
import edu.tinkoff.ninjamireaclone.model.Gender;
import edu.tinkoff.ninjamireaclone.model.PrivilegeEntity;
import edu.tinkoff.ninjamireaclone.model.RoleEntity;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRightsRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest extends AbstractBaseTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SectionRightsRepository sectionRightsRepository;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        sectionRightsRepository.deleteAll();
        roleRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание нового аккаунта")
    @Transactional
    public void createAccount() {
        // given
        AccountEntity accountGiven = new AccountEntity();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);

        // when
        var account = accountService.createAccount(accountGiven);

        // then
        var accounts = accountRepository.findAll();
        assertEquals(1, accounts.size());
        assertEquals(accountGiven.getName(), account.getName());
        assertEquals(1, account.getRoles().size());
        assertEquals(roleRepository.findByName("ROLE_USER").get(), account.getRoles().get(0));
    }

    @Test
    @DisplayName("Получение нового аккаунта")
    @Transactional
    public void getAccount() {
        // given
        AccountEntity accountGiven = new AccountEntity();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
        accountGiven = accountService.createAccount(accountGiven);

        // when
        var accountByName = accountService.getByName(accountGiven.getName());
        var accountById = accountService.getById(accountGiven.getId());

        // then
        assertEquals(accountGiven.getName(), accountByName.getName());
        assertEquals(1, accountByName.getRoles().size());
        assertEquals(roleRepository.findByName("ROLE_USER").get(), accountByName.getRoles().get(0));
        assertEquals(accountById, accountByName);
    }

    @Test
    @DisplayName("Получение UserDetails аккаунта")
    @Transactional
    public void getAccountUserDetails() {
        // given
        AccountEntity accountGiven = new AccountEntity();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultPrivilege = new PrivilegeEntity();
        defaultPrivilege.setName("DEFAULT");
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        defaultRole.setPrivileges(new ArrayList<>(List.of(defaultPrivilege)));
        roleRepository.save(defaultRole);

        accountGiven = accountService.createAccount(accountGiven);

        // when
        var userDetails = accountService.loadUserByUsername(accountGiven.getName());

        // then
        assertEquals(accountGiven.getName(), userDetails.getUsername());
        assertThat(userDetails.getAuthorities()).map(GrantedAuthority::getAuthority).containsAll(List.of("ROLE_USER", "DEFAULT"));
    }

    @Test
    @Transactional
    public void grantRoles() {
        // given
        AccountEntity accountGiven = new AccountEntity();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        defaultRole = roleRepository.save(defaultRole);
        var adminRole = new RoleEntity();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);
        accountGiven = accountService.createAccount(accountGiven);

        // when
        var roles = new ArrayList<>(List.of(adminRole));
        var account = accountService.grantRoles(accountGiven.getId(), roles);

        // then
        var expected = Set.of(defaultRole, adminRole);
        assertEquals(expected, new HashSet<>(account.getRoles()));
    }

    @Test
    @Transactional
    public void removeRoles() {
        // given
        AccountEntity accountGiven = new AccountEntity();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        defaultRole = roleRepository.save(defaultRole);
        var adminRole = new RoleEntity();
        adminRole.setName("ROLE_ADMIN");
        adminRole = roleRepository.save(adminRole);
        accountGiven = accountService.createAccount(accountGiven);
        var roles = new ArrayList<>(List.of(adminRole));
        var account = accountService.grantRoles(accountGiven.getId(), roles);

        // when
        account = accountService.removeRoles(account.getId(), List.of(adminRole));

        // then
        var expected = Set.of(defaultRole);
        assertEquals(expected, new HashSet<>(account.getRoles()));
    }

    @Test
    public void deleteAccount() {
        // given
        AccountEntity accountGiven = new AccountEntity();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setEnabled(true);
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
        accountGiven = accountService.createAccount(accountGiven);

        // when
        accountService.deleteAccount(accountGiven.getId());

        // then
        var accounts = accountRepository.findAll();
        assertEquals(1, accounts.size());
        assertFalse(accounts.get(0).isEnabled());
    }

    @Test
    @Transactional
    public void getAllAccounts() {
        // given
        AccountEntity accountGivenA = new AccountEntity();
        accountGivenA.setName("Alistair");
        accountGivenA.setPassword("qwerty");
        accountGivenA.setDisplayName("Alistair");
        accountGivenA.setDescription("");
        accountGivenA.setEnabled(true);
        AccountEntity accountGivenB = new AccountEntity();
        accountGivenB.setName("Ketheric");
        accountGivenB.setPassword("123");
        accountGivenB.setDisplayName("Ketheric");
        accountGivenB.setEnabled(true);
        accountGivenB.setDescription("");
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
        accountService.createAccount(accountGivenA);
        accountService.createAccount(accountGivenB);

        // when
        var page = accountService.getAll(Pageable.ofSize(5));

        // then
        assertEquals(2, page.getTotalElements());
    }

    @Test
    @Transactional
    public void checkFakeId() {
        // given
        AccountEntity accountA = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .build();
        AccountEntity accountB = AccountEntity.builder()
                .name("NOT_TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .build();

        var savedAccountA = accountRepository.save(accountA);

        // when
        var result = accountService.checkFakeId(savedAccountA.getId());

        // then
        assertFalse(result);
    }

    @Test
    @Transactional
    public void checkFakeIdWrong() {
        // given
        AccountEntity accountA = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .build();
        AccountEntity accountB = AccountEntity.builder()
                .name("NOT_TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .build();

        var savedAccountA = accountRepository.save(accountA);
        var savedAccountB = accountRepository.save(accountB);

        // when
        var result = accountService.checkFakeId(savedAccountB.getId());

        // then
        assertTrue(result);
    }

    @Test
    @Transactional
    public void promoteUser() {
        // given
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);

        var moderatorRole = new RoleEntity();
        moderatorRole.setName("ROLE_MODERATOR");
        roleRepository.save(moderatorRole);

        var account = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .roles(Lists.newArrayList(defaultRole))
                .build();
        accountRepository.save(account);

        // when
        var result = accountService.promote(account.getId());

        // then
        assertThat(result.getRoles()).contains(moderatorRole);
    }

    @Test
    @Transactional
    public void promoteUserAlreadyModerator() {
        // given
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);

        var moderatorRole = new RoleEntity();
        moderatorRole.setName("ROLE_MODERATOR");
        roleRepository.save(moderatorRole);

        var account = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .roles(Lists.newArrayList(defaultRole, moderatorRole))
                .build();
        accountRepository.save(account);

        // when
        var result = assertThrows(Exception.class, () -> accountService.promote(account.getId()));

        // then
        assertThat(result).isInstanceOf(ConflictException.class);
    }

    @Test
    @Transactional
    public void demoteUser() {
        // given
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);

        var moderatorRole = new RoleEntity();
        moderatorRole.setName("ROLE_MODERATOR");
        roleRepository.save(moderatorRole);

        var account = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .roles(Lists.newArrayList(defaultRole, moderatorRole))
                .build();
        accountRepository.save(account);

        // when
        var result = accountService.demote(account.getId());

        // then
        assertThat(result.getRoles()).doesNotContain(moderatorRole);
    }

    @Test
    @Transactional
    public void demoteUserNotAModerator() {
        // given
        var defaultRole = new RoleEntity();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);

        var moderatorRole = new RoleEntity();
        moderatorRole.setName("ROLE_MODERATOR");
        roleRepository.save(moderatorRole);

        var account = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .roles(Lists.newArrayList(defaultRole))
                .build();
        accountRepository.save(account);

        // when
        var result = assertThrows(Exception.class, () -> accountService.demote(account.getId()));

        // then
        assertThat(result).isInstanceOf(ConflictException.class);
    }
}
