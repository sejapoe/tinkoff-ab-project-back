package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.PrivilegeEntity;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.SectionRightsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface SectionRightsRepository extends JpaRepository<SectionRightsEntity, Long>, QuerydslPredicateExecutor<SectionRightsEntity> {
    Optional<SectionRightsEntity> findBySectionAndPrivilege(SectionEntity section, PrivilegeEntity role);

    boolean existsBySection_Id(Long id);
}