package edu.tinkoff.ninjamireaclone.dto.section.response;

public record SectionRightsDto(
        boolean canCreateSubsections,
        boolean canCreateTopics
) {
}
