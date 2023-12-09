package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Gender;
import edu.tinkoff.ninjamireaclone.model.Privilege;
import edu.tinkoff.ninjamireaclone.model.Role;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new Role();
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
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new Role();
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
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultPrivilege = new Privilege();
        defaultPrivilege.setName("DEFAULT");
        var defaultRole = new Role();
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
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        defaultRole = roleRepository.save(defaultRole);
        var adminRole = new Role();
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
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setGender(Gender.NOT_SPECIFIED);
        accountGiven.setEnabled(true);
        var defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        defaultRole = roleRepository.save(defaultRole);
        var adminRole = new Role();
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
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        accountGiven.setDisplayName("Alistair");
        accountGiven.setDescription("");
        accountGiven.setEnabled(true);
        var defaultRole = new Role();
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
        Account accountGivenA = new Account();
        accountGivenA.setName("Alistair");
        accountGivenA.setPassword("qwerty");
        accountGivenA.setDisplayName("Alistair");
        accountGivenA.setDescription("");
        accountGivenA.setEnabled(true);
        Account accountGivenB = new Account();
        accountGivenB.setName("Ketheric");
        accountGivenB.setPassword("123");
        accountGivenB.setDisplayName("Ketheric");
        accountGivenB.setEnabled(true);
        accountGivenB.setDescription("");
        var defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
        accountService.createAccount(accountGivenA);
        accountService.createAccount(accountGivenB);

        // when
        var page = accountService.getAll(Pageable.ofSize(5));

        // then
        assertEquals(2, page.getTotalElements());
    }
}
