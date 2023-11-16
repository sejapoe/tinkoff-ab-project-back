package edu.tinkoff.ninjamireaclone.service.rule.condition;

import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import edu.tinkoff.ninjamireaclone.model.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
public class SubsectionCreated implements Condition<Section> {
    private final List<Long> parentIds;

    @Override
    public Class<Section> getType() {
        return Section.class;
    }

    @Override
    public ConditionTrigger getTrigger() {
        return ConditionTrigger.CREATED;
    }

    @Override
    public boolean test(Section section) {
        return parentIds.contains(section.getParent().getId());
    }

    @Component
    public static class Factory implements ConditionFactory<Section> {
        @Override
        public String getConditionName() {
            return "subsection_created";
        }

        @Override
        public Condition<Section> create(List<String> args) {
            if (args.isEmpty())
                throw new RuleException(("Invalid number of arguments for `subsection_created` condition. " +
                        "At least one required got 0."));

            try {
                return new SubsectionCreated(args.stream().map(Long::parseLong).toList());
            } catch (NumberFormatException e) {
                throw new RuleException("Unable to parse `parent_id` argument of `subsection_created` condition", e);
            }
        }
    }
}
