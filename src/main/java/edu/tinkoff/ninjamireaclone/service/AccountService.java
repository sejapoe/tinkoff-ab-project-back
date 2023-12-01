package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.AccessDeniedException;
import edu.tinkoff.ninjamireaclone.exception.AccountAlreadyExistsException;
import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.service.storage.FileSystemStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final FileSystemStorageService storageService;

    /**
     * Get account by name
     *
     * @param name account's name
     * @return account
     */
    public Account getByName(String name) {
        return accountRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Аккаунт", name));
    }

    /**
     * Get account by id
     *
     * @param id id to be used
     * @return account
     */
    public Account getById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Аккаунт", id));
    }

    /**
     * Get {@link UserDetails} by account's name
     *
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
     *
     * @param account account to be created
     * @return created account
     * @throws AccountAlreadyExistsException if account with such username already exists
     */
    public Account createAccount(Account account) throws AccountAlreadyExistsException {
        if (accountRepository.findByName(account.getName()).isPresent()) {
            throw new AccountAlreadyExistsException(account.getName());
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRoles(List.of(roleService.getDefaultRole()));
        account.setEnabled(true);
        return accountRepository.save(account);
    }

    /**
     * Grant specified roles to the account
     *
     * @param id    id of the account
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
     *
     * @param id    id of the account
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

    /**
     * Check if the provided id is not related to the current authenticated user
     *
     * @param id provided id
     * @return true, if the id is fake
     */
    public boolean checkFakeId(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var author = getById(id);
        if (!author.getName().equals(authentication.getName())) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).noneMatch(s -> s.equals("ROLE_ADMIN"));
        }
        return false;
    }

    /**
     * @return current user id or -1 if unauthenticated
     */
    public long getCurrentUserId() {
        try {
            return getCurrentUser().getId();
        } catch (AccessDeniedException e) {
            return -1L;
        }
    }

    public Account getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return getByName(authentication.getName());
        } catch (ResourceNotFoundException e) {
            throw new AccessDeniedException("Вы не авторизованы");
        }
    }

    public Account update(Account updater, MultipartFile avatar) {
        var account = getById(updater.getId());
        account.setDisplayName(updater.getDisplayName());
        account.setDescription(updater.getDescription());
        account.setGender(updater.getGender());
        if (Objects.nonNull(avatar)) {
            account.setAvatar(storageService.store(avatar));
        }
        return accountRepository.save(account);
    }

    /**
     * Get all accounts
     * @param pageable pagination properties
     * @return page of accounts
     */
    public Page<Account> getAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    /**
     * Delete account by id
     * @param id id of the account to be deleted
     * @return the account's id
     */
    public Long deleteAccount(Long id) {
        var account = getById(id);
        account.setEnabled(false);
        accountRepository.save(account);
        return account.getId();
    }
}
