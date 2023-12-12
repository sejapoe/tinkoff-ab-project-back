package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    Optional<PrivilegeEntity> findByName(String name);
}