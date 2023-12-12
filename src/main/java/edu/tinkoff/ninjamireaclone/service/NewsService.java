package edu.tinkoff.ninjamireaclone.service;

import com.google.common.collect.Lists;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import edu.tinkoff.ninjamireaclone.config.DataLoader;
import edu.tinkoff.ninjamireaclone.exception.ConflictException;
import edu.tinkoff.ninjamireaclone.exception.NotFoundException;
import edu.tinkoff.ninjamireaclone.model.*;
import edu.tinkoff.ninjamireaclone.repository.PostRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.SectionRightsRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final EntityManager entityManager;
    private final SectionRepository sectionRepository;
    private final TopicRepository topicRepository;
    private final StorageService storageService;
    private final AccountService accountService;
    private final PostRepository postRepository;
    private final RoleService roleService;
    private final SectionRightsRepository sectionRightsRepository;

    @Transactional
    public SectionEntity createNews(String title, String text, List<MultipartFile> files) {
        var rootPost = createRootPost(text, files);
        var rootTopic = createRootTopic(title, rootPost);

        rootPost.setParent(rootTopic);

        var section = SectionEntity.builder()
                .name(title)
                .parent(getNewsRoot())
                .topics(Lists.newArrayList(rootTopic))
                .build();

        rootTopic.setParent(section);

        var savedSection = sectionRepository.save(section);

        initNewsRights(savedSection);

        return savedSection;
    }

    private TopicEntity createRootTopic(String title, PostEntity rootPost) {
        return TopicEntity.builder()
                .name(title)
                .posts(Lists.newArrayList(rootPost))
                .build();
    }

    private PostEntity createRootPost(String text, List<MultipartFile> files) {
        var currentUser = accountService.getCurrentUser();

        List<MultipartFile> nonNullFiles = Objects.requireNonNullElse(files, List.of());
        var documents = nonNullFiles.stream()
                .map(storageService::store)
                .collect(Collectors.toCollection(HashSet::new));

        return PostEntity.builder()
                .author(currentUser)
                .text(text)
                .isAnonymous(false)
                .isOpening(true)
                .documents(documents)
                .build();
    }

    private void initNewsRights(SectionEntity savedSection) {
        var createCommentPrivilege = roleService.getPrivilegeByName("CREATE_COMMENT");

        var sectionRights = SectionRightsEntity.builder()
                .rights(new Rights(false, true))
                .privilege(createCommentPrivilege)
                .section(savedSection)
                .build();

        sectionRightsRepository.save(sectionRights);
    }

    @Transactional
    public SectionEntity getNewsRoot() {
        return sectionRepository.findById(DataLoader.NEWS_ROOT_ID).orElseThrow(() -> new NotFoundException("News root is not found"));
    }

    @Transactional
    public Page<SectionEntity> getAllNews(Pageable pageable) {
        return sectionRepository.findAll(QSection.section.parent.id.eq(DataLoader.NEWS_ROOT_ID), pageable);
    }


    @Transactional
    public SectionEntity getNews(Long id) {
        var section = sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News with id %s is not found".formatted(id)));

        if (Objects.isNull(section.getParent()) || section.getParent().getId() != DataLoader.NEWS_ROOT_ID) {
            throw new NotFoundException("Section you are trying to get is not news");
        }

        return section;
    }

    @Transactional
    public TopicEntity getNewsRootTopic(SectionEntity news) {
        var queryFactory = new JPAQueryFactory(entityManager);
        var rootTopic = queryFactory
                .selectFrom(QTopic.topic)
                .where(QTopic.topic.parent.id.eq(news.getId()))
                .orderBy(QTopic.topic.id.asc())
                .fetchFirst();

        if (Objects.isNull(rootTopic)
                || !rootTopic.getName().equals(news.getName())
                || rootTopic.getPosts().isEmpty()
        ) {
            throw new ConflictException("News is corrupted");
        }

        return rootTopic;
    }

    @Transactional
    public PostEntity getNewsRootTopicPost(SectionEntity news) {
        return getNewsRootTopic(news).getPosts().get(0);
    }

    @Transactional
    public Page<TopicEntity> getComments(Long newsId, Pageable pageable) {
        var news = getNews(newsId);
        var rootTopic = getNewsRootTopic(news);

        var predicate = ExpressionUtils.and(
                QTopic.topic.id.ne(rootTopic.getId()),
                QTopic.topic.parent.id.eq(news.getId())
        );

        return topicRepository.findAll(predicate, pageable);
    }

    @Transactional
    public PostEntity getRootComment(TopicEntity comment) {
        var queryFactory = new JPAQueryFactory(entityManager);
        var rootPost = queryFactory
                .selectFrom(QPost.post)
                .where(QPost.post.parent.id.eq(comment.getId()))
                .orderBy(QPost.post.id.asc())
                .fetchFirst();

        if (Objects.isNull(rootPost)) {
            throw new ConflictException("Comment is corrupted");
        }

        return rootPost;
    }

    @Transactional
    public TopicEntity getThread(Long id) {
        var topic = topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id %s is not found".formatted(id)));

        try {
            var news = getNews(topic.getParent().getId());
            var rootTopic = getNewsRootTopic(news);

            if (topic.getId().equals(rootTopic.getId())) {
                throw new ConflictException("It is a root topic");
            }

            if (topic.getPosts().isEmpty()) {
                throw new ConflictException("No posts");
            }
        } catch (NotFoundException | ConflictException e) {
            throw new ConflictException("It is not a thread", e);
        }

        return topic;
    }

    @Transactional
    public Page<PostEntity> getThread(TopicEntity comment, Pageable pageable) {
        var rootPost = getRootComment(comment);

        var predicate = ExpressionUtils.and(
                QPost.post.id.ne(rootPost.getId()),
                QPost.post.parent.id.eq(comment.getId())
        );

        return postRepository.findAll(predicate, pageable);
    }

    @Transactional
    public Page<PostEntity> getThread(Long threadId, Pageable pageable) {
        return getThread(getThread(threadId), pageable);
    }

    @Transactional
    public TopicEntity createComment(Long newsId, String text, boolean isAnonymous) {
        var account = accountService.getCurrentUser();
        var news = getNews(newsId);

        var post = PostEntity.builder()
                .author(account)
                .text(text)
                .isAnonymous(isAnonymous)
                .build();

        var topic = TopicEntity.builder()
                .name("comment")
                .parent(news)
                .posts(Lists.newArrayList(post))
                .build();

        post.setParent(topic);

        return topicRepository.save(topic);
    }

    @Transactional
    public PostEntity createThreadComment(Long threadId, String text, boolean isAnonymous) {
        var account = accountService.getCurrentUser();
        var thread = getThread(threadId);

        var post = PostEntity.builder()
                .parent(thread)
                .author(account)
                .text(text)
                .isAnonymous(isAnonymous)
                .build();

        return postRepository.save(post);
    }
}
