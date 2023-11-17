package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.section.response.SectionResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.ShortSectionResponseDto;
import edu.tinkoff.ninjamireaclone.model.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    @Mapping(target = "subsections", source = "section.subsections")
    @Mapping(target = "parent", source = "section.parent")
    SectionResponseDto toDto(Section section);

    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    ShortSectionResponseDto toShortDto(Section section);
}
