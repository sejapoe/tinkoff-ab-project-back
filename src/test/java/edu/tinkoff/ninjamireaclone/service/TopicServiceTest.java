package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.model.Rights;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.TopicEntity;
import edu.tinkoff.ninjamireaclone.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TopicServiceTest extends AbstractBaseTest {
    @MockBean
    private SectionRightsService sectionRightsService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    public void initMocks() {
        when(sectionRightsService.getRights(any())).thenReturn(new Rights(true, true));
    }

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        sectionRepository.deleteAll();
        accountRepository.deleteAll();
        topicRepository.deleteAll();
        postRepository.deleteAll();
        documentRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание топика")
    @Transactional
    public void createTopic() {
        // given
        var sectionGiven = new SectionEntity();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);

        var topicGiven = new TopicEntity();
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
        var sectionGiven = new SectionEntity();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);

        var topicGiven = new TopicEntity();
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
