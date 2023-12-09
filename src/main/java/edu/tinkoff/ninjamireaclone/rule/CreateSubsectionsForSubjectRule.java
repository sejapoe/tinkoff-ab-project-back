package edu.tinkoff.ninjamireaclone.rule;

import edu.tinkoff.ninjamireaclone.model.Rights;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.SectionRights;
import edu.tinkoff.ninjamireaclone.repository.SectionRepository;
import edu.tinkoff.ninjamireaclone.service.RoleService;
import edu.tinkoff.ninjamireaclone.service.rule.ConditionTrigger;
import edu.tinkoff.ninjamireaclone.service.rule.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateSubsectionsForSubjectRule implements Rule<Section> {

    private static final String[] subsectionNames = {"Конспекты семинаров", "Контрольные работы", "Литература"};
    private final SectionRepository sectionRepository;
    private final RoleService roleService;

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

        var createTopicPrivilege = roleService.getPrivilegeByName("CREATE_TOPIC");
        var defaultPrivilege = roleService.getDefaultPrivilege();

        log.info("Creating subsections for `%s`".formatted(section.getName()));

        var subsections = Arrays.stream(subsectionNames).map(name -> {
            var subsection = new Section();
            subsection.setParent(section);
            subsection.setName(name);

            var sectionRights = new SectionRights();
            sectionRights.setPrivilege(createTopicPrivilege);
            sectionRights.setRights(new Rights(false, true));
            sectionRights.setSection(subsection);

            subsection.setSectionRights(new ArrayList<>(List.of(sectionRights)));

            return subsection;
        }).collect(Collectors.toCollection(ArrayList::new));

        section.setSubsections(subsections);

        var sectionRights = new SectionRights();
        sectionRights.setPrivilege(defaultPrivilege);
        sectionRights.setRights(new Rights(false, false));
        sectionRights.setSection(section);
        section.setSectionRights(new ArrayList<>(List.of(sectionRights)));

        return sectionRepository.save(section);
    }
}
