package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.NotFoundException;
import edu.tinkoff.ninjamireaclone.model.QSection;
import edu.tinkoff.ninjamireaclone.model.QTopic;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.repository.TopicRepository;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPage;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final RuleService ruleService;
    private final TopicRepository topicRepository;
    private MultiPager<Section, Topic, SectionRepository, TopicRepository> multiPager;

    @PostConstruct
    private void init() {
        multiPager = new MultiPager<>(sectionRepository, topicRepository);
    }

    /**
     * @param id section id
     * @return section with given id
     * @throws NotFoundException if section with given id is not found
     */
    public Section get(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section with id %d is not found".formatted(id)));
    }

    public MultiPage<Section, Topic> getMultiPage(Section section, Pageable pageable) {
        return multiPager.findAll(pageable.getPageNumber(),
                pageable.getPageSize(),
                QSection.section.parent.id.eq(section.getId()),
                QTopic.topic.parent.id.eq(section.getId()));
    }

    /**
     * Creates section and handles it with rules
     *
     * @param section section to create
     * @return created section
     * @see RuleService#handleSectionCreated(Section)
     */
    public Section create(Section section) {
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
     * @see SectionService#create(Section)
     */
    public Section create(Long parentId, String name) {
        var parent = get(parentId);
        // check if parent writable

        Section section = new Section();
        section.setName(name);
        section.setParent(parent);
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
    public Section update(Long id, String name) {
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
    public void delete(Long id) {
        Section section = get(id);
        sectionRepository.delete(section);
    }

    /**
     * Returns root section (section without parent)
     *
     * @return root section
     * @throws NotFoundException if root section is not found
     */
    public Section getRoot() {
        return sectionRepository.findOne(QSection.section.parent.isNull()).orElseThrow(() ->
                new NotFoundException("Root section is not found!"));
    }
}
