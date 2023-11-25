package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
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
        var defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);

        accountGiven = accountService.createAccount(accountGiven);

        // when
        var userDetails = accountService.loadUserByUsername(accountGiven.getName());

        // then
        assertEquals(accountGiven.getName(), userDetails.getUsername());
        assertEquals(accountGiven
                .getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()), userDetails.getAuthorities());
    }

    @Test
    @Transactional
    public void grantRoles() {
        // given
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
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
}
