package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.Rights;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.SectionRights;
import edu.tinkoff.ninjamireaclone.repository.SectionRightsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionRightsService {
    private final SectionRightsRepository sectionRightsRepository;
    private final RoleService roleService;

    private Optional<SectionRights> getRightsForRole(Section section, Role role) {
        Optional<SectionRights> sectionRights = sectionRightsRepository.findBySectionAndRole(section, role);
        while (sectionRights.isEmpty() && Objects.nonNull(section.getParent())) {
            section = section.getParent();
            sectionRights = sectionRightsRepository.findBySectionAndRole(section, role);
        }
        return sectionRights;
    }


    public Rights getRights(Section section) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(grantedAuthority -> {
                            if (grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS")) {
                                return Optional.<SectionRights>empty();
                            }
                            var role = roleService.getRoleByName(grantedAuthority.getAuthority());
                            return getRightsForRole(section, role);
                        }
                )
                .flatMap(Optional::stream)
                .map(SectionRights::getRights)
                .reduce((rights, rights2) ->
                        new Rights(rights.getCreateSubsections() || rights2.getCreateSubsections(),
                                rights.getCreateTopics() || rights2.getCreateTopics())
                )
                .orElseGet(() -> new Rights(false, false));
    }
}
