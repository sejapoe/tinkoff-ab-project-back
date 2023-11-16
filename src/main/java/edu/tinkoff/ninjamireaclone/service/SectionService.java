package edu.tinkoff.ninjamireaclone.service;

import edu.tinkoff.ninjamireaclone.exception.NotFoundException;
import edu.tinkoff.ninjamireaclone.model.QSection;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.rule.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final RuleService ruleService;

    public Section get(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Section with id %d is not found".formatted(id)));
    }

    public Section create(Section section) {
        var saved = sectionRepository.save(section);
        return ruleService.handleSectionCreated(saved);
    }

    public Section create(Long parentId, String name) {
        var parent = get(parentId);
        // check if parent writable

        Section section = new Section();
        section.setName(name);
        section.setParent(parent);
        return create(section);
    }

    public Section update(Long id, String name) {
        var section = get(id);
        section.setName(name);
        return sectionRepository.save(section);
        // ruleService.handleSectionUpdate
    }

    public void delete(Long id) {
        Section section = get(id);
        sectionRepository.delete(section);
    }

    public Section getRoot() {
        return sectionRepository.findOne(QSection.section.parent.isNull()).orElseThrow(() ->
                new NotFoundException("Root section is not found!"));
    }
}
