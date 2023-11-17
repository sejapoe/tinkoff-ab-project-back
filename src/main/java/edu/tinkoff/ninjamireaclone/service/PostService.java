package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Document;
import edu.tinkoff.ninjamireaclone.model.Post;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.PostRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
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

    private void attachDocuments(Post post, Set<Document> documents) {
        for (var d : documents) {
            post.addDocument(d);
        }
    }

    private void init(Post post, Long authorId, Long parentId) {
        if (nonNull(authorId)) {post.setAuthor(accountRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", authorId)));}
        if (nonNull(parentId)) {post.setParent(topicRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Топик", parentId)));}
    }

    /**
     * Updates the post by id from 'post' object
     * @param post object with updated fields
     * @param authorId id of the author account
     * @param parentId id of the parent topic
     * @return updated saved post
     */
    public Post updatePost(Post post, Long authorId, Long parentId) {
        var found = getPost(post.getId());
        post.setCreatedAt(found.getCreatedAt());
        post.setDocuments(found.getDocuments());
        init(post, authorId, parentId);
        return postRepository.save(post);
    }

    /**
     * Gets the post by id
     * @param id id of the post
     * @return found post
     */
    public Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Пост", id));
    }

    /**
     * Deletes the post by id
     * @param id id of the post to be deleted
     * @return id of the deleted post
     */
    public Long deletePost(Long id) {
        var post = getPost(id);
        postRepository.deleteById(id);
        return post.getId();
    }

    /**
     * Creates (saves) new post
     * @param post post to be saved
     * @param authorId id of the author account
     * @param parentId id of the parent topic
     * @param files files to be attached
     * @return saved post
     */
    public Post createPostWithAttachments(Post post, Long authorId, Long parentId, List<MultipartFile> files) {
        init(post, authorId, parentId);
        if (nonNull(files)) {
            Set<Document> documents = new HashSet<>();
            for (var f : files) {
                documents.add(storageService.store(f));
            }
            attachDocuments(post, documents);
        }
        return postRepository.save(post);
    }
}
