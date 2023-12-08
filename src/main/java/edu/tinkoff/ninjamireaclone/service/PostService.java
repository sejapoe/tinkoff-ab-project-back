package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Document;
import edu.tinkoff.ninjamireaclone.model.Post;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.PostRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final TopicRepository topicRepository;
    private final StorageService storageService;
    private final TransactionExecutorService transactionExecutorService;

    private void attachDocuments(Post post, Set<Document> documents) {
        for (var d : documents) {
            post.addDocument(d);
        }
    }

    private void init(Post post, Long authorId, Long parentId) {
        if (nonNull(authorId)) {
            post.setAuthor(accountRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь", authorId)));
        }
        if (nonNull(parentId)) {
            post.setParent(topicRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Топик", parentId)));
        }
    }

    @Transactional
    public Post updatePost(Post post, Long authorId, Long parentId) {
        var found = getPost(post.getId());
        System.out.println("found: " + found.getCreatedAt());
        post.setCreatedAt(found.getCreatedAt());
        post.setDocuments(found.getDocuments());
        init(post, authorId, parentId);
        return postRepository.save(post);
    }

    @Transactional
    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Пост", id));
    }

    @Transactional
    public Long deletePost(Long id) {
        var post = getPost(id);
        postRepository.deleteById(id);
        return post.getId();
    }

    @Transactional
    public Post createPostWithAttachments(Post post, Long authorId, Long parentId, List<MultipartFile> files) {
        init(post, authorId, parentId);
        if (nonNull(files)) {
            Set<Document> documents = new HashSet<>();
            for (var f : files) {
                documents.add(storageService.store(f));
            }
            attachDocuments(post, documents);
        }
        return postRepository.saveAndFlush(post);
    }

    public long cleanUpComments() {
        var deleted = -1L;
        var totalDeleted = 0L;

        while (deleted != 0) {
            deleted = transactionExecutorService.execute(() -> postRepository.deleteComments(1000));
            totalDeleted += deleted;
        }
        return totalDeleted;
    }

}