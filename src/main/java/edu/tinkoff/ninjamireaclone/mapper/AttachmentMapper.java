package edu.tinkoff.ninjamireaclone.mapper;

import edu.tinkoff.ninjamireaclone.dto.attachment.request.AttachmentRequestDto;
import edu.tinkoff.ninjamireaclone.dto.attachment.response.AttachmentResponseDto;
import edu.tinkoff.ninjamireaclone.model.Attachment;
import edu.tinkoff.ninjamireaclone.model.AttachmentId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(target = "post.id", source = "postId")
    @Mapping(target = "document.id", source = "documentId")
    Attachment toAttachment(AttachmentRequestDto requestDto);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "documentId", source = "document.id")
    AttachmentResponseDto toAttachmentResponseDto(Attachment attachment);

    AttachmentId toAttachmentId(AttachmentRequestDto requestDto);
}
