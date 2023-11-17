package edu.tinkoff.ninjamireaclone.service.rule.action;

import edu.tinkoff.ninjamireaclone.exception.rule.RuleException;
import edu.tinkoff.ninjamireaclone.model.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the {@link Action} interface that creates a new subsection in a {@link Section}.
 * <p>
 * Example usage: "create_subsection(Example subsection name)"
 */
@RequiredArgsConstructor
public class CreateSubsection implements Action<Section> {
    private final String name;

    @Override
    public Class<Section> getType() {
        return Section.class;
    }

    @Override
    public void accept(Section section) {
        var subsection = new Section();
        subsection.setParent(section);
        subsection.setName(name);
        if (Objects.isNull(section.getSubsections())) section.setSubsections(new ArrayList<>());
        section.getSubsections().add(subsection);
    }

    @Component
    public static class Factory implements ActionFactory<Section> {

        @Override
        public String getActionName() {
            return "create_subsection";
        }

        @Override
        public Action<Section> create(List<String> args) {
            if (args.size() != 1)
                throw new RuleException(("Invalid number of arguments for `create_subsection` action." +
                        " Should be 1 got %d").formatted(args.size()));

            return new CreateSubsection(args.get(0));
        }
    }
}
