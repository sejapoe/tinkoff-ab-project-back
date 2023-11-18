package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.topic.request.CreateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.UpdateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.TopicResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.TopicMapper;
import edu.tinkoff.ninjamireaclone.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/topic")
@Tag(name = "topic", description = "Работа с топиками")
@RequestMapping("/topic")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;
    private final TopicMapper topicMapper;

    @Operation(description = "Создание топика")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Топик создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных")
    })
    @Schema(implementation = TopicResponseDto.class)
    @PostMapping
    public ResponseEntity<TopicResponseDto> create(@RequestBody @Valid CreateTopicRequestDto requestDto) {
        var topic = topicService.createTopic(topicMapper.toTopic(requestDto), requestDto.parentId());
        var responseDto = topicMapper.toTopicResponseDto(topic);
        log.info("Создан топик " + responseDto.id());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(description = "Удаление топика")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    @DeleteMapping
    public ResponseEntity<Long> delete(@RequestParam Long id) {
        var topicId = topicService.deleteTopic(id);
        log.info("Удален топик " + topicId);
        return ResponseEntity.ok(topicId);
    }

    @Operation(description = "Обновление топика")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    @PutMapping
    public ResponseEntity<TopicResponseDto> update(@RequestBody @Valid UpdateTopicRequestDto requestDto) {
        var topic = topicService.updateTopic(topicMapper.toTopic(requestDto), requestDto.parentId());
        var responseDto = topicMapper.toTopicResponseDto(topic);
        log.info("Обновлен топик " + responseDto.id());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(description = "Получение топика")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик получен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    @GetMapping
    public ResponseEntity<TopicResponseDto> get(@RequestParam Long id) {
        var topic = topicService.getTopic(id);
        return ResponseEntity.ok(topicMapper.toTopicResponseDto(topic));
    }
}
