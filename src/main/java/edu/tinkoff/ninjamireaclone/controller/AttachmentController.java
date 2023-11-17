package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.attachment.request.AttachmentRequestDto;
import edu.tinkoff.ninjamireaclone.dto.attachment.response.AttachmentResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.AttachmentMapper;
import edu.tinkoff.ninjamireaclone.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "attachment", description = "Работа со вложениями")
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;

    @Operation(description = "Создание вложения")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Вложение создано"),
            @ApiResponse(responseCode = "400", description = "Неверный формат")
    })
    @PostMapping
    public ResponseEntity<AttachmentResponseDto> create(@RequestBody  @Valid AttachmentRequestDto requestDto) {
        var attachment = attachmentService.createAttachment(attachmentMapper.toAttachment(requestDto));
        var responseDto = attachmentMapper.toAttachmentResponseDto(attachment);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(description = "Удаление вложения")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Вложение удалено"),
            @ApiResponse(responseCode = "400", description = "Неверный формат")
    })
    @DeleteMapping
    public ResponseEntity<AttachmentResponseDto> delete(@RequestBody @Valid AttachmentRequestDto requestDto) {
        var responseDto = attachmentMapper
                .toAttachmentResponseDto(attachmentService
                        .deleteAttachment(attachmentMapper.toAttachmentId(requestDto)));
        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AttachmentResponseDto> get(@RequestBody @Valid AttachmentRequestDto requestDto) {
        var attachment = attachmentService.getAttachment(attachmentMapper.toAttachmentId(requestDto));
        var responseDto = attachmentMapper.toAttachmentResponseDto(attachment);
        return ResponseEntity.ok(responseDto);
    }
}
