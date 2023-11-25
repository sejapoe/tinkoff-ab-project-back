package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import edu.tinkoff.ninjamireaclone.utils.jwt.JwtTokenUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
public class AuthServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        roleRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createAuthToken() {
        // given
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        var defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
        accountGiven = accountService.createAccount(accountGiven);

        // when
        var token = authService
                .createAuthToken(accountService
                        .loadUserByUsername(accountGiven.getName()), accountGiven.getName(), "qwerty");

        // then
        assertEquals(accountGiven.getName(), jwtTokenUtils.getUsername(token));
        assertEquals(accountGiven.getRoles().stream()
                .map(Role::getName)
                .toList(),
                jwtTokenUtils.getRoles(token));
    }

    @Test
    public void createAuthTokenWithBadCredentials() {
        // given
        Account accountGiven = new Account();
        accountGiven.setName("Alistair");
        accountGiven.setPassword("qwerty");
        var defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        roleRepository.save(defaultRole);
        accountService.createAccount(accountGiven);

        // when
        var exception = assertThrows(BadCredentialsException.class, () -> {
            var token = authService
                    .createAuthToken(accountService
                            .loadUserByUsername("Alistair"), "Alistair", "aw2");
        });


        // then
        assertEquals("Bad credentials", exception.getMessage());
    }
}
