package edu.tinkoff.ninjamireaclone.rule;

import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.rule.ConditionTrigger;
import edu.tinkoff.ninjamireaclone.service.rule.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateSubsectionsForSubjectRule implements Rule<Section> {

    private static final String[] subsectionNames = {"Конспекты семинаров", "Контрольные работы", "Литература"};
    private final SectionRepository sectionRepository;

    @Override
    public Class<Section> getType() {
        return Section.class;
    }

    @Override
    public ConditionTrigger getConditionTrigger() {
        return ConditionTrigger.CREATED;
    }

    @Override
    public Section evaluate(Section section) {
        if (!section.getParent().getName().endsWith("курс")
                || !Objects.isNull(section.getParent().getParent().getParent())) return section;

        log.info("Creating subsections for `%s`".formatted(section.getName()));

        var subsections = Arrays.stream(subsectionNames).map(name -> {
            var subsection = new Section();
            subsection.setParent(section);
            subsection.setName(name);
            return subsection;
        }).toList();

        section.setSubsections(subsections);

        return sectionRepository.save(section);
    }
}
