package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.PostEntity;
import edu.tinkoff.ninjamireaclone.repository.PostRevisionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final PostRevisionRepository postRevisionRepository;

    /**
     * Get all revisions of the post by id
     *
     * @param id id of the post
     * @return list of revisions
     */
    @Transactional
    public List<PostEntity> getPostRevisions(Long id) {
        return postRevisionRepository.getRevisionsById(id);
    }
}
