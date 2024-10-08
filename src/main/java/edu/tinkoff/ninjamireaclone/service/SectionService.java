package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.config.DataLoader;
import edu.tinkoff.ninjamireaclone.exception.AccessDeniedException;
import edu.tinkoff.ninjamireaclone.exception.ConflictException;
import edu.tinkoff.ninjamireaclone.exception.NotFoundException;
import edu.tinkoff.ninjamireaclone.model.QSectionEntity;
import edu.tinkoff.ninjamireaclone.model.QTopicEntity;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.TopicEntity;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPage;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPager;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final RuleService ruleService;
    private final TopicRepository topicRepository;
    private final SectionRightsService sectionRightsService;
    private MultiPager<SectionEntity, TopicEntity, SectionRepository, TopicRepository> multiPager;

    @PostConstruct
    private void init() {
        multiPager = new MultiPager<>(sectionRepository, topicRepository);
    }

    /**
     * @param id section id
     * @return section with given id
     * @throws NotFoundException if section with given id is not found
     */
    @Transactional
    public SectionEntity get(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section with id %d is not found".formatted(id)));
    }

    public MultiPage<SectionEntity, TopicEntity> getMultiPage(SectionEntity section, Pageable pageable) {
        return multiPager.findAll(pageable.getPageNumber(),
                pageable.getPageSize(),
                QSectionEntity.sectionEntity.parent.id.eq(section.getId()),
                QTopicEntity.topicEntity.parent.id.eq(section.getId()));
    }

    /**
     * Creates section and handles it with rules
     *
     * @param section section to create
     * @return created section
     * @see RuleService#handleSectionCreated(SectionEntity)
     */
    @Transactional
    public SectionEntity create(SectionEntity section) {
        if (Objects.nonNull(section.getParent())
                && !sectionRightsService.getRights(section.getParent()).getCreateSubsections()) {
            throw new AccessDeniedException("Вы не можете создавать подразделы здесь!");
        }

        var saved = sectionRepository.save(section);
        return ruleService.handleSectionCreated(saved);
    }

    /**
     * Creates subsection and handles it with rules
     *
     * @param parentId parent section id
     * @param name     section name
     * @return created section
     * @throws NotFoundException if parent section is not found
     * @see SectionService#create(SectionEntity)
     */
    @Transactional
    public SectionEntity create(Long parentId, String name) {
        var parent = get(parentId);
        // check if parent writable

        SectionEntity section = new SectionEntity();
        section.setName(name);
        section.setParent(parent);

        if (parent.getSubsections().stream().anyMatch(subsection -> subsection.getName().equals(name))) {
            throw new ConflictException("Имя подраздела должно быть уникальным");
        }

        return create(section);
    }

    /**
     * Updates section and handles it with rules
     *
     * @param id   section id
     * @param name new section name
     * @return updated section
     * @throws NotFoundException if section with given id is not found
     */
    @Transactional
    public SectionEntity update(Long id, String name) {
        var section = get(id);
        section.setName(name);
        return sectionRepository.save(section);
        // ruleService.handleSectionUpdate
    }

    /**
     * Deletes section
     *
     * @param id section id
     * @throws NotFoundException if section with given id is not found
     */
    @Transactional
    public void delete(Long id) {
        SectionEntity section = get(id);
        sectionRepository.delete(section);
    }

    /**
     * Returns root section (section without parent)
     *
     * @return root section
     * @throws NotFoundException if root section is not found
     */
    @Transactional
    public SectionEntity getRoot() {
        return sectionRepository.findById(DataLoader.ROOT_ID).orElseThrow(() ->
                new NotFoundException("Root section is not found!"));
    }
}
