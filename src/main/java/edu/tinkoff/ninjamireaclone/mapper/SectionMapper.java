package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.section.request.SectionMultiPageResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.SectionResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.ShortSectionResponseDto;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TopicMapper.class})
public interface SectionMapper {
    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    @Mapping(target = "parent", source = "section.parent")
    @Mapping(target = "page", source = "multiPage")
    SectionResponseDto toDto(Section section, MultiPage<Section, Topic> multiPage);

    @Mapping(target = "subsections", source = "multiPage.content1")
    @Mapping(target = "topics", source = "multiPage.content2")
    SectionMultiPageResponseDto toMultiPageDto(MultiPage<Section, Topic> multiPage);

    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    ShortSectionResponseDto toShortDto(Section section);
}
