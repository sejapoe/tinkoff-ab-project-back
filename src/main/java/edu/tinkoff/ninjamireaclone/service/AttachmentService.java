package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Attachment;
import edu.tinkoff.ninjamireaclone.model.AttachmentId;
import edu.tinkoff.ninjamireaclone.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    /**
     * Creates new attachment
     * @param attachment attachment to be saved
     * @return saved attachment
     */
    public Attachment createAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    /**
     * Gets the attachment by id
     * @param id id of the attachment
     * @return found attachment
     */
    public Attachment getAttachment(AttachmentId id) {
        return attachmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Вложение", id));
    }

    /**
     * Deletes the attachment by id
     * @param id id of the attachment to be deleted
     * @return deleted attachment
     */
    public Attachment deleteAttachment(AttachmentId id) {
        var found = getAttachment(id);
        attachmentRepository.deleteById(id);
        return found;
    }
}
