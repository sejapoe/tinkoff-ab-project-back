package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.Post;
import edu.tinkoff.ninjamireaclone.repository.PostRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final PostRevisionRepository postRevisionRepository;

    public List<Post> getRevisions(Long id) {
        return postRevisionRepository.getRevisionsById(id);
    }
}
