package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.AccountAlreadyExistsException;
import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get account by name
     * @param name account's name
     * @return account
     */
    public Account getByName(String name) {
        return accountRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Аккаунт", name));
    }

    /**
     * Get account by id
     * @param id id to be used
     * @return account
     */
    public Account getById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Аккаунт", id));
    }

    /**
     * Get {@link UserDetails} by account's name
     * @param username account's name
     * @return userDetails
     * @throws UsernameNotFoundException if the user cannot be found
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getByName(username);
    }

    /**
     * Create new account
     * @param account account to be created
     * @return created account
     * @throws AccountAlreadyExistsException if account with such username already exists
     */
    public Account createAccount(Account account) throws AccountAlreadyExistsException {
        try {
            getByName(account.getName());
            throw new AccountAlreadyExistsException(account.getName());
        } catch (ResourceNotFoundException ex) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setRoles(List.of(roleService.getDefaultRole()));
            return accountRepository.save(account);
        }
    }

    /**
     * Grant specified roles to the account
     * @param id id of the account
     * @param roles roles to be assigned
     * @return updated account
     */
    public Account grantRoles(Long id, List<Role> roles) {
        var account = getById(id);
        roles.addAll(account.getRoles());
        account.setRoles(roles);
        return accountRepository.save(account);
    }

    /**
     * Remove specified roles from the account
     * @param id id of the account
     * @param roles roles to be removed
     * @return updated account
     */
    public Account removeRoles(Long id, List<Role> roles) {
        var account = getById(id);
        var resultRoles = new ArrayList<>(account.getRoles());
        resultRoles.removeAll(roles);
        account.setRoles(resultRoles);
        return accountRepository.save(account);
    }
}
