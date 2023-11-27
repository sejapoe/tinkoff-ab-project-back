package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.SectionRights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface SectionRightsRepository extends JpaRepository<SectionRights, Long>, QuerydslPredicateExecutor<SectionRights> {
    Optional<SectionRights> findBySectionAndRole(Section section, Role role);
}