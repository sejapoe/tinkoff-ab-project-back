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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

//@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AuditServiceTest extends AbstractBaseTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRevisionRepository postRevisionRepository;

    @Autowired
    private TransactionExecutorService transactionExecutorService;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        sectionRepository.deleteAll();
        accountRepository.deleteAll();
        topicRepository.deleteAll();
        postRepository.deleteAll();
        postRevisionRepository.deleteAll();
    }

    @Test
    @DisplayName("Аудит создания, обновления и удаления поста")
    public void auditPostCreationUpdateDeletion() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "TEST",
                null,
                null
        ));

        var ids = transactionExecutorService.execute(() -> {
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
            accountGiven = accountRepository.saveAndFlush(accountGiven);

            return Map.of("acc_id", accountGiven.getId(), "topic_id", topicGiven.getId());
        });

        var postId = transactionExecutorService.execute(() -> {
            var postGiven = new Post();
            postGiven.setText("Text 1");
            return postService.createPostWithAttachments(postGiven, ids.get("acc_id"), ids.get("topic_id"), null).getId();
        });

        transactionExecutorService.execute(() -> {
            var postCreated = postService.getPost(postId);
            postCreated.setText("Text 2");
            return postService.updatePost(postCreated, ids.get("acc_id"), ids.get("topic_id"));
        });

        transactionExecutorService.execute(() -> {
            var postCreated = postService.getPost(postId);
            postCreated.setText("Text 3");
            return postService.updatePost(postCreated, ids.get("acc_id"), ids.get("topic_id"));
        });

        transactionExecutorService.execute(() -> postService.deletePost(postId));

        // then
        var posts = postRepository.findAll();
        assertEquals(0, posts.size());
        var revisions = auditService.getPostRevisions(postId);
        assertEquals(4, revisions.size());
        assertEquals("Text 1", revisions.get(0).getText());
        assertEquals("Text 2", revisions.get(1).getText());
        assertEquals("Text 3", revisions.get(2).getText());
        assertNull(revisions.get(3));
    }
}
