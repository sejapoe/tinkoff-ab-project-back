package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.NoSuchPrivilegeException;
import edu.tinkoff.ninjamireaclone.exception.NoSuchRoleException;
import edu.tinkoff.ninjamireaclone.model.PrivilegeEntity;
import edu.tinkoff.ninjamireaclone.model.RoleEntity;
import edu.tinkoff.ninjamireaclone.repository.PrivilegeRepository;
import edu.tinkoff.ninjamireaclone.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public RoleEntity getDefaultRole() {
        return getRoleByName("ROLE_USER");
    }

    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NoSuchRoleException(name));
    }

    public PrivilegeEntity getDefaultPrivilege() {
        return getPrivilegeByName("DEFAULT");
    }

    public PrivilegeEntity getPrivilegeByName(String name) {
        return privilegeRepository.findByName(name).orElseThrow(() -> new NoSuchPrivilegeException(name));
    }
}
