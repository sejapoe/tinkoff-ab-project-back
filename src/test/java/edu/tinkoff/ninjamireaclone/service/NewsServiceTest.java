package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.config.DataLoader;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class NewsServiceTest extends AbstractBaseTest {
    @Autowired
    private NewsService newsService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    @Transactional
    void clear() {
        sectionRepository.deleteAll(sectionRepository.findAll(QSectionEntity.sectionEntity.parent.id.eq(DataLoader.NEWS_ROOT_ID)));
        accountRepository.deleteAll(accountRepository.findAll(QAccountEntity.accountEntity.name.eq("TEST_USER")));

        AccountEntity account = AccountEntity.builder()
                .name("TEST_USER")
                .password("testpass")
                .displayName("Test user")
                .description("Test user")
                .gender(Gender.NOT_SPECIFIED)
                .enabled(true)
                .build();

        accountRepository.save(account);
        log.info("CLEARED");
    }

    @Test
    @Transactional
    @DisplayName("Создание новости с вложением")
    void createNewsWithAttachment() {
        // given
        MultipartFile fileA = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Lorem ipsum dolores".getBytes(Charset.defaultCharset())
        );
        MultipartFile fileB = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "Lorem ipsum dolores".getBytes(Charset.defaultCharset())
        );
        List<MultipartFile> files = List.of(fileA, fileB);

        // when
        var news = newsService.createNews("[TEST] Test news", "Lorem ipsum dolores", files);

        // then
//        var newsRoot = newsService.getNewsRoot();
//        assertThat(newsRoot.getSubsections()).size().isEqualTo(1);
//        var news = newsRoot.getSubsections().get(0);
        assertThat(news.getName()).isEqualTo("[TEST] Test news");
        assertThat(news.getTopics()).size().isEqualTo(1);
        var topic = news.getTopics().get(0);
        assertThat(topic.getName()).isEqualTo("[TEST] Test news");
        assertThat(topic.getPosts()).size().isEqualTo(1);
        var post = topic.getPosts().get(0);
        assertThat(post.getText()).isEqualTo("Lorem ipsum dolores");
        assertThat(post.getDocuments()).size().isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("Получение корня новостей")
    void getNewsRoot() {
        // when
        SectionEntity result = newsService.getNewsRoot();

        // then
        assertThat(result.getId()).isEqualTo(DataLoader.NEWS_ROOT_ID);
        assertThat(result.getName()).isEqualTo("news_root");
        assertThat(result.getSubsections()).size().isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("Получение всех новостей")
    void getAllNews() {
        // given
        List<MultipartFile> files = List.of();
        newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", files);
        newsService.createNews("[TEST] Test news 2", "Lorem ipsum dolores", files);
        newsService.createNews("[TEST] Test news 3", "Lorem ipsum dolores", files);

        // when
        var result = newsService.getAllNews(Pageable.unpaged());

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().stream().map(SectionEntity::getName))
                .contains("[TEST] Test news 1", "[TEST] Test news 2", "[TEST] Test news 3");
    }

    @Test
    @Transactional
    @DisplayName("Получение новости по ID")
    void getNews() {
        // given
        var saved = newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", List.of());

        // when
        var result = newsService.getNews(saved.getId());

        // then
        assertThat(result.getName()).isEqualTo("[TEST] Test news 1");
        assertThat(result.getTopics()).size().isEqualTo(1);
        TopicEntity topicEntity = result.getTopics().get(0);
        assertThat(topicEntity.getName()).isEqualTo("[TEST] Test news 1");
        assertThat(topicEntity.getPosts()).size().isEqualTo(1);
        assertThat(topicEntity.getPosts().get(0).getText()).isEqualTo("Lorem ipsum dolores");
    }

    @Test
    @Transactional
    @DisplayName("Получение главного топика новости")
    void getNewsRootTopic() {
        // given
        var saved = newsService.createNews("[TEST] Test news 3", "Lorem ipsum dolores", List.of());

        // when
        var result = newsService.getNewsRootTopic(saved);

        // then
        assertThat(result.getName()).isEqualTo("[TEST] Test news 3");
        assertThat(result.getPosts()).size().isEqualTo(1);
        assertThat(result.getPosts().get(0).getText()).isEqualTo("Lorem ipsum dolores");
    }

    @Test
    @Transactional
    @DisplayName("Получение главного поста новости")
    void getNewsRootTopicPost() {
        // given
        var saved = newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", List.of());

        // when
        var result = newsService.getNewsRootTopicPost(saved);

        // then
        assertThat(result.getText()).isEqualTo("Lorem ipsum dolores");
    }

    @Test
    @Transactional
    @DisplayName("Создание комментария в новости")
    void createComment() {
        // given
        var saved = newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", List.of());

        // when
        var result = newsService.createComment(saved.getId(), "Lorem ipsum dolores 2", false);

        // then
        assertThat(result.getPosts()).size().isEqualTo(1);
        var comment = result.getPosts().get(0);
        assertThat(comment.getText()).isEqualTo("Lorem ipsum dolores 2");
        assertThat(comment.getDocuments()).isNullOrEmpty();
        assertThat(comment.isAnonymous()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("Создание комментариев в ветке")
    void createThreadComment() {
        // given
        var saved = newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", List.of());
        var savedTopic = newsService.createComment(saved.getId(), "Lorem ipsum dolores 2", false);

        // when
        var result = newsService.createThreadComment(savedTopic.getId(), "Lorem ipsum dolores 3", true);

        // then
        assertThat(result.getText()).isEqualTo("Lorem ipsum dolores 3");
        assertThat(result.isAnonymous()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("Получение комментариев к новости")
    void getComments() {
        // given
        var saved = newsService.createNews("[TEST] Test news 2", "Lorem ipsum dolores", List.of());
        newsService.createComment(saved.getId(), "Lorem ipsum dolores 2.1", false);
        newsService.createComment(saved.getId(), "Lorem ipsum dolores 2.2", false);
        newsService.createComment(saved.getId(), "Lorem ipsum dolores 2.3", false);

        // when
        var result = newsService.getComments(saved.getId(), Pageable.unpaged());

        // then5
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().stream().map(TopicEntity::getPosts))
                .extracting(postEntities -> postEntities.get(0))
                .extracting("text")
                .contains("Lorem ipsum dolores 2.1", "Lorem ipsum dolores 2.2", "Lorem ipsum dolores 2.3");
    }

    @Test
    @DisplayName("Получение главного комментария ветки")
    void getRootComment() {
        // given
        var saved = newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", List.of());
        var savedTopic = newsService.createComment(saved.getId(), "Lorem ipsum dolores 2", false);
        newsService.createThreadComment(savedTopic.getId(), "Lorem ipsum dolores 3", true);

        // when
        var result = newsService.getRootComment(savedTopic);

        // then
        assertThat(result.getText()).isEqualTo("Lorem ipsum dolores 2");
    }

    @Test
    @DisplayName("Получение комментариев в ветке")
    void getThread() {
        // given
        var saved = newsService.createNews("[TEST] Test news 1", "Lorem ipsum dolores", List.of());
        var savedTopic = newsService.createComment(saved.getId(), "Lorem ipsum dolores 2", false);
        newsService.createThreadComment(savedTopic.getId(), "Lorem ipsum dolores 3.1", true);
        newsService.createThreadComment(savedTopic.getId(), "Lorem ipsum dolores 3.2", true);
        newsService.createThreadComment(savedTopic.getId(), "Lorem ipsum dolores 3.3", true);

        // when
        var result = newsService.getThread(savedTopic, Pageable.unpaged());

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result)
                .extracting("text")
                .contains("Lorem ipsum dolores 3.1", "Lorem ipsum dolores 3.2", "Lorem ipsum dolores 3.3");
    }
}