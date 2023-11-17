package edu.tinkoff.ninjamireaclone.repository;

import edu.tinkoff.ninjamireaclone.model.Attachment;
import edu.tinkoff.ninjamireaclone.model.AttachmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, AttachmentId> {
}
