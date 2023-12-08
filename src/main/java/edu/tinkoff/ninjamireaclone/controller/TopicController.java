package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.annotation.IsAdmin;
import edu.tinkoff.ninjamireaclone.dto.common.PageRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.CreateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.request.UpdateTopicRequestDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.ShortTopicResponseDto;
import edu.tinkoff.ninjamireaclone.dto.topic.response.TopicResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.PageMapper;
import edu.tinkoff.ninjamireaclone.mapper.PostMapper;
import edu.tinkoff.ninjamireaclone.mapper.TopicMapper;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import edu.tinkoff.ninjamireaclone.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final PostMapper postMapper;
    private final PageMapper pageMapper;
    private final AccountService accountService;

    @Operation(description = "Создание топика")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Топик создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных")
    })
    @Schema(implementation = TopicResponseDto.class)
    @PostMapping(consumes =
            {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ShortTopicResponseDto> create(@ModelAttribute @Valid CreateTopicRequestDto requestDto) {
        var topic = topicService.createTopicWithPost(topicMapper.toTopic(requestDto),
                postMapper.toPost(requestDto),
                requestDto.parentId(),
                requestDto.authorId(),
                requestDto.files()
        );
        var responseDto = topicMapper.toShortTopicResponseDto(topic);
        log.info("Создан топик " + responseDto.id());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(description = "Удаление топика")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    @IsAdmin
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
    @IsAdmin
    @PutMapping
    public ResponseEntity<ShortTopicResponseDto> update(@RequestBody @Valid UpdateTopicRequestDto requestDto) {
        var topic = topicService.updateTopic(topicMapper.toTopic(requestDto), requestDto.parentId());
        var responseDto = topicMapper.toShortTopicResponseDto(topic);
        log.info("Обновлен топик " + responseDto.id());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(description = "Получение топика")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Топик получен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Топик не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDto> get(@PathVariable Long id, @ParameterObject PageRequestDto pageRequestDto) {
        var topic = topicService.getTopic(id);
        var posts = topicService.getTopicPosts(topic, pageMapper.fromRequestDto(pageRequestDto));
        Account currentUser = accountService.getCurrentUser();
        return ResponseEntity.ok(topicMapper.toTopicResponseDto(topic,
                posts,
                accountService.getCurrentUserId(),
                currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))
        ));
    }
}
