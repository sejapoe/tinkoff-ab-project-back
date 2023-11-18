package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.document.response.DocumentResponseDto;
import edu.tinkoff.ninjamireaclone.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface DocumentMapper {
    @Mapping(target = "type", source = "document.documentType")
    DocumentResponseDto toDto(Document document);
}