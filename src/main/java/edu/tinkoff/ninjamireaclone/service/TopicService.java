package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.AccessDeniedException;
import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Post;
import edu.tinkoff.ninjamireaclone.model.QPost;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.repository.PostRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SectionRepository sectionRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final SectionRightsService sectionRightsService;

    private void init(Topic topic, Long parentId) {
        topic.setParent(sectionRepository
                .findById(parentId).orElseThrow(() -> new ResourceNotFoundException("Секция", parentId)));
    }

    /**
     * Creates (saves) new topic
     *
     * @param topic    topic to be saved
     * @param parentId id of the parent section
     * @return saved topic
     */
    @Transactional
    public Topic createTopic(Topic topic, Long parentId) {
        init(topic, parentId);

        if (!sectionRightsService.getRights(topic.getParent()).getCreateTopics()) {
            throw new AccessDeniedException("Вы не можете создавать топики здесь!");
        }

        return topicRepository.saveAndFlush(topic);
    }

    @Transactional
    public Topic createTopicWithPost(Topic topic, Post post, Long parentId, Long authorId, List<MultipartFile> files) {
        var saved = createTopic(topic, parentId);
        postService.createPostWithAttachments(post, authorId, saved.getId(), files);
        return saved;
    }

    /**
     * Deletes the topic by id
     *
     * @param id id of the topic to be deleted
     * @return id of the deleted topic
     */
    @Transactional
    public Long deleteTopic(Long id) {
        var topic = getTopic(id);
        topicRepository.deleteById(id);
        return topic.getId();
    }

    /**
     * Gets the topic by id
     *
     * @param id id of the topic
     * @return found topic
     */
    @Transactional
    public Topic getTopic(Long id) {
        return topicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Тема", id));
    }

    /**
     * Updates the topic by id from 'topic' object
     *
     * @param topic    object with updated fields
     * @param parentId id of the parent section
     * @return updated saved topic
     */
    @Transactional
    public Topic updateTopic(Topic topic, Long parentId) {
        var found = getTopic(topic.getId());
        init(topic, parentId);
        return topicRepository.save(topic);
    }

    public Page<Post> getTopicPosts(Topic topic, Pageable pageable) {
        return postRepository.findAll(QPost.post.parent.id.eq(topic.getId()), pageable);
    }
}
