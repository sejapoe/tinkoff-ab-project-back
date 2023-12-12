package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.audit.PostAuditResponseDto;
import edu.tinkoff.ninjamireaclone.model.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PostAuditResponseDto toPostAuditResponseDto(PostEntity post);
}
