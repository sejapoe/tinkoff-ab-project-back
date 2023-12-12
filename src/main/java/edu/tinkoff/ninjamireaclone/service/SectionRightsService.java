package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.PrivilegeEntity;
import edu.tinkoff.ninjamireaclone.model.Rights;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.SectionRightsEntity;
import edu.tinkoff.ninjamireaclone.repository.SectionRightsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionRightsService {
    private final SectionRightsRepository sectionRightsRepository;
    private final RoleService roleService;

    private Optional<SectionRightsEntity> getRightsForRole(SectionEntity section, PrivilegeEntity privilege) {
        return sectionRightsRepository.findBySectionAndPrivilege(section, privilege);
    }


    public Rights getRights(SectionEntity section) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(grantedAuthority -> {
                            if (grantedAuthority.getAuthority().startsWith("ROLE_")) {
                                //  pass roles, because we now use privileges in section rights
                                return Optional.<SectionRightsEntity>empty();
                            }
                            var role = roleService.getPrivilegeByName(grantedAuthority.getAuthority());
                            return getRightsForRole(section, role);
                        }
                )
                .flatMap(Optional::stream)
                .map(SectionRightsEntity::getRights)
                .reduce((rights, rights2) ->
                        new Rights(rights.getCreateSubsections() || rights2.getCreateSubsections(),
                                rights.getCreateTopics() || rights2.getCreateTopics())
                )
                .orElseGet(() -> new Rights(false, false));
    }
}
