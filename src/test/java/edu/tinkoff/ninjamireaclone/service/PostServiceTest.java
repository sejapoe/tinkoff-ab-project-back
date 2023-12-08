package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.AbstractBaseTest;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostServiceTest extends AbstractBaseTest {

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

    @Autowired
    private DocumentRepository documentRepository;

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
        accountGiven.setPassword("12345");
        accountGiven.setDisplayName("Astarion");
        accountGiven.setDescription("Cool vampire");
        accountGiven.setGender(Gender.APACHE_HELICOPTER);
        accountGiven.setEnabled(true);
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
        accountGiven.setPassword("12345");
        accountGiven.setDisplayName("Astarion");
        accountGiven.setDescription("Cool vampire");
        accountGiven.setGender(Gender.APACHE_HELICOPTER);
        accountGiven.setEnabled(true);
        accountGiven = accountRepository.save(accountGiven);

        var postGiven = new Post();
        postGiven.setText("Sample");

        var postCreated = postService.createPostWithAttachments(postGiven, accountGiven.getId(), topicGiven.getId(), files);

        // when
        postCreated.setText("New text");
        var postUpdated = postService.updatePost(postCreated, accountGiven.getId(), topicGiven.getId());
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
        accountGiven.setPassword("12345");
        accountGiven.setDisplayName("Astarion");
        accountGiven.setDescription("Cool vampire");
        accountGiven.setGender(Gender.APACHE_HELICOPTER);
        accountGiven.setEnabled(true);
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
    @Transactional
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
        accountGiven.setPassword("12345");
        accountGiven.setDisplayName("Astarion");
        accountGiven.setDescription("Cool vampire");
        accountGiven.setGender(Gender.APACHE_HELICOPTER);
        accountGiven.setEnabled(true);
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
