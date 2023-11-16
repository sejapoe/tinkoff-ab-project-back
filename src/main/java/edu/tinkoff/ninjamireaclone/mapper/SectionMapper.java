package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.section.SectionDto;
import edu.tinkoff.ninjamireaclone.dto.section.ShortSectionDto;
import edu.tinkoff.ninjamireaclone.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    @Mapping(target = "subsections", source = "section.subsections")
    @Mapping(target = "parent", source = "section.parent")
    SectionDto toDto(Section section);

    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    ShortSectionDto toShortDto(Section section);
}
