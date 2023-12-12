package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.topic.request.CreateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.UpdateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.ShortTopicResponseDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.TopicResponseDto;
import edu.tinkoff.ninjamireaclone.model.PostEntity;
import edu.tinkoff.ninjamireaclone.model.TopicEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface TopicMapper {

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    TopicEntity toTopic(CreateTopicRequestDto requestDto);

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "parent", ignore = true)
    TopicEntity toTopic(UpdateTopicRequestDto requestDto);

    @Mapping(target = "parentId", source = "topic.parent.id")
    @Mapping(target = "posts", source = "posts")
    TopicResponseDto toTopicResponseDto(TopicEntity topic, Page<PostEntity> posts, @Context Long userId, @Context boolean isAdmin);

    ShortTopicResponseDto toShortTopicResponseDto(TopicEntity topic);
}
