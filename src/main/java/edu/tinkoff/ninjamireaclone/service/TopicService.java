package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SectionRepository sectionRepository;

    private void init(Topic topic, Long parentId) {
        topic.setParent(sectionRepository
                .findById(parentId).orElseThrow(() -> new ResourceNotFoundException("Секция", parentId)));
    }

    /**
     * Creates (saves) new topic
     * @param topic topic to be saved
     * @param parentId id of the parent section
     * @return saved topic
     */
    public Topic createTopic(Topic topic, Long parentId) {
        init(topic, parentId);
        return topicRepository.save(topic);
    }

    /**
     * Deletes the topic by id
     * @param id id of the topic to be deleted
     * @return id of the deleted topic
     */
    public Long deleteTopic(Long id) {
        var topic = getTopic(id);
        topicRepository.deleteById(id);
        return topic.getId();
    }

    /**
     * Gets the topic by id
     * @param id id of the topic
     * @return found topic
     */
    public Topic getTopic(Long id) {
        return topicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Автомобиль", id));
    }

    /**
     * Updates the topic by id from 'topic' object
     * @param topic object with updated fields
     * @param parentId id of the parent section
     * @return updated saved topic
     */
    public Topic updateTopic(Topic topic, Long parentId) {
        var found = getTopic(topic.getId());
        init(topic, parentId);
        return topicRepository.save(topic);
    }
}
