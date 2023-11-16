package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
