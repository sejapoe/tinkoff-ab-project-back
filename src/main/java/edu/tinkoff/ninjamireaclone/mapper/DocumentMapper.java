package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.document.response.DocumentResponseDto;
import edu.tinkoff.ninjamireaclone.model.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface DocumentMapper {
    @Mapping(target = "type", source = "document.documentType")
    DocumentResponseDto toDto(DocumentEntity document);
}