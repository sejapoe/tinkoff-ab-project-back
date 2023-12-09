package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.NoSuchPrivilegeException;
import edu.tinkoff.ninjamireaclone.exception.NoSuchRoleException;
import edu.tinkoff.ninjamireaclone.model.Privilege;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.repository.PrivilegeRepository;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public Role getDefaultRole() {
        return getRoleByName("ROLE_USER");
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NoSuchRoleException(name));
    }

    public Privilege getDefaultPrivilege() {
        return getPrivilegeByName("DEFAULT");
    }

    public Privilege getPrivilegeByName(String name) {
        return privilegeRepository.findByName(name).orElseThrow(() -> new NoSuchPrivilegeException(name));
    }
}
