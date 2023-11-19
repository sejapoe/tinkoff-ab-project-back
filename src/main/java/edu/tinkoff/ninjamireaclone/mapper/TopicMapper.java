package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.topic.request.CreateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.UpdateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.ShortTopicResponseDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.TopicResponseDto;
import edu.tinkoff.ninjamireaclone.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface TopicMapper {

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    Topic toTopic(CreateTopicRequestDto requestDto);

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Topic toTopic(UpdateTopicRequestDto requestDto);

    @Mapping(target = "parentId", source = "parent.id")
    TopicResponseDto toTopicResponseDto(Topic topic);

    ShortTopicResponseDto toShortTopicResponseDto(Topic topic);
}
