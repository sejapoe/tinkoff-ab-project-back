package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Post;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.repository.AccountRepository;
import edu.tinkoff.ninjamireaclone.repository.PostRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        sectionRepository.deleteAll();
        accountRepository.deleteAll();
        topicRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание поста с вложениями")
    @Transactional
    public void createPostWithAttachments() {
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

        var sectionGiven = new Section();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);
        var topicGiven = new Topic();
        topicGiven.setName("Main");
        topicGiven.setParent(sectionGiven);
        topicGiven = topicRepository.save(topicGiven);
        var accountGiven = new Account();
        accountGiven.setName("Astarion");
        accountGiven = accountRepository.save(accountGiven);

        var postGiven = new Post();
        postGiven.setText("Sample");

        // when
        postService.createPostWithAttachments(postGiven, accountGiven.getId(), topicGiven.getId(), files);

        // then
        var posts = postRepository.findAll();
        assertEquals(1, posts.size());
        var post = posts.get(0);
        assertEquals(2, post.getDocuments().size());
        assertEquals(accountGiven.getName(), post.getAuthor().getName());
        assertEquals("Sample", post.getText());
    }

    @Test
    @Transactional
    @DisplayName("Обновление поста")
    public void updatePost() {
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

        var sectionGiven = new Section();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);
        var topicGiven = new Topic();
        topicGiven.setName("Main");
        topicGiven.setParent(sectionGiven);
        topicGiven = topicRepository.save(topicGiven);
        var accountGiven = new Account();
        accountGiven.setName("Astarion");
        accountGiven = accountRepository.save(accountGiven);

        var postGiven = new Post();
        postGiven.setText("Sample");

        var postCreated = postService.createPostWithAttachments(postGiven, accountGiven.getId(), topicGiven.getId(), files);

        // when
        postCreated.setText("New text");
        var postUpdated = postService.updatePost(postCreated, accountGiven.getId(), sectionGiven.getId());
        System.out.println("docs: " + postUpdated.getDocuments());
        System.out.println("created_at: " + postUpdated.getCreatedAt());
        // then
        var posts = postRepository.findAll();
        assertEquals(1, posts.size());
        var post = posts.get(0);
        assertEquals(accountGiven.getName(), post.getAuthor().getName());
        assertEquals("New text", post.getText());
    }

    @Test
    @Transactional
    @DisplayName("Получение поста")
    public void getPost() {
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

        var sectionGiven = new Section();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);
        var topicGiven = new Topic();
        topicGiven.setName("Main");
        topicGiven.setParent(sectionGiven);
        topicGiven = topicRepository.save(topicGiven);
        var accountGiven = new Account();
        accountGiven.setName("Astarion");
        accountGiven = accountRepository.save(accountGiven);

        var postGiven = new Post();
        postGiven.setText("Sample");

        var postCreated = postService.createPostWithAttachments(postGiven, accountGiven.getId(), topicGiven.getId(), files);

        // when
        var post = postService.getPost(postCreated.getId());

        // then
        assertEquals(2, post.getDocuments().size());
        assertEquals(accountGiven.getName(), post.getAuthor().getName());
        assertEquals("Sample", post.getText());
    }

    @Test
    @DisplayName("Удаление поста")
    public void deletePost() {
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

        var sectionGiven = new Section();
        sectionGiven.setName("Root");
        sectionGiven = sectionRepository.save(sectionGiven);
        var topicGiven = new Topic();
        topicGiven.setName("Main");
        topicGiven.setParent(sectionGiven);
        topicGiven = topicRepository.save(topicGiven);
        var accountGiven = new Account();
        accountGiven.setName("Astarion");
        accountGiven = accountRepository.save(accountGiven);

        var postGiven = new Post();
        postGiven.setText("Sample");

        var postCreated = postService.createPostWithAttachments(postGiven, accountGiven.getId(), topicGiven.getId(), files);

        // when
        var deleted = postService.deletePost(postCreated.getId());

        // then
        assertEquals(postCreated.getId(), deleted);
        assertEquals(0, postRepository.findAll().size());
    }
}
