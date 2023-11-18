package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.topic.request.CreateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.UpdateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.TopicResponseDto;
import edu.tinkoff.ninjamireaclone.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    Topic toTopic(CreateTopicRequestDto requestDto);

    Topic toTopic(UpdateTopicRequestDto requestDto);

    @Mapping(target = "parentId", source = "parent.id")
    TopicResponseDto toTopicResponseDto(Topic topic);
}
