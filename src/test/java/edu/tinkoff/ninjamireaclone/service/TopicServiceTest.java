package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class TopicServiceTest {

    @Autowired
    private TopicService topicService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private TopicRepository topicRepository;

    @Test
    @DisplayName("Создание топика")
    @Transactional
    public void createTopic() {
        // given
        var sectionGiven = new Section();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);

        var topicGiven = new Topic();
        topicGiven.setName("Main");

        // when
        var topicCreated = topicService.createTopic(topicGiven, sectionGiven.getId());

        // then
        var topics = topicRepository.findAll();
        assertEquals(1, topics.size());
        var topic = topics.get(0);
        assertEquals(topicGiven.getName(), topic.getName());
        assertEquals(sectionGiven, topic.getParent());
    }

    @Test
    @DisplayName("Обновление топика")
    @Transactional
    public void updateTopic() {
        // given
        var sectionGiven = new Section();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);

        var topicGiven = new Topic();
        topicGiven.setName("Main");

        var topicCreated = topicRepository.save(topicGiven);

        // when
        topicCreated.setName("New name");
        topicService.updateTopic(topicCreated, sectionGiven.getId());

        // then
        var topics = topicRepository.findAll();
        assertEquals(1, topics.size());
        var topic = topics.get(0);
        assertEquals("New name", topic.getName());
        assertEquals(sectionGiven, topic.getParent());
    }
}
