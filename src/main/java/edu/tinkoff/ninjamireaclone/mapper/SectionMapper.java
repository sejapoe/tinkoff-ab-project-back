package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.section.request.SectionMultiPageResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.SectionResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.ShortSectionResponseDto;
import edu.tinkoff.ninjamireaclone.model.Rights;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.model.TopicEntity;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TopicMapper.class})
public interface SectionMapper {
    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    @Mapping(target = "parent", source = "section.parent")
    @Mapping(target = "page", source = "multiPage")
    @Mapping(target = "rights.canCreateTopics", source = "rights.createTopics")
    @Mapping(target = "rights.canCreateSubsections", source = "rights.createSubsections")
    SectionResponseDto toDto(SectionEntity section, MultiPage<SectionEntity, TopicEntity> multiPage, Rights rights);

    @Mapping(target = "subsections", source = "multiPage.content1")
    @Mapping(target = "topics", source = "multiPage.content2")
    SectionMultiPageResponseDto toMultiPageDto(MultiPage<SectionEntity, TopicEntity> multiPage);

    @Mapping(target = "id", source = "section.id")
    @Mapping(target = "name", source = "section.name")
    ShortSectionResponseDto toShortDto(SectionEntity section);
}
