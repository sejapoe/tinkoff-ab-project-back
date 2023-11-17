package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.post.request.CreatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.request.UpdatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.PostMapper;
import edu.tinkoff.ninjamireaclone.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@Tag(name = "post", description = "Работа с постами")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

//    @Operation(description = "Создание поста")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Пост создан"),
//            @ApiResponse(responseCode = "400", description = "Неверный формат данных")
//    })
//    @PostMapping
//    public ResponseEntity<PostResponseDto> create(@RequestBody @Valid CreatePostRequestDto requestDto) {
//        var post = postService.createPost(
//                postMapper.toPost(requestDto),
//                requestDto.documentIds(),
//                requestDto.authorId(),
//                requestDto.parentId());
//        var responseDto = postMapper.toPostResponseDto(post);
//        log.info("Создан пост " + responseDto.id());
//        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
//    }

    @Operation(description = "Обновление поста")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @PutMapping
    public ResponseEntity<PostResponseDto> update(@RequestBody @Valid UpdatePostRequestDto requestDto) {
        var post = postService.updatePost(
                postMapper.toPost(requestDto),
                requestDto.authorId(),
                requestDto.parentId());
        var responseDto = postMapper.toPostResponseDto(post);
        log.info("Обновлен пост " + responseDto.id());
        return ResponseEntity.ok(responseDto);
    }


    @Operation(description = "Удаление поста")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @DeleteMapping
    public ResponseEntity<Long> delete(@RequestParam Long id) {
        var postId = postService.deletePost(id);
        log.info("Удален пост " + postId);
        return ResponseEntity.ok(postId);
    }


    @Operation(description = "Получение поста")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @GetMapping
    public ResponseEntity<PostResponseDto> get(@RequestParam Long id) {
        var post = postService.getPost(id);
        var responseDto = postMapper.toPostResponseDto(post);
        log.info("Получен пост " + responseDto.id());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(description = "Создание поста")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пост создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных")
    })
    @PostMapping("/withattach")
    public ResponseEntity<PostResponseDto> createWithAttachments(@RequestPart("data")CreatePostRequestDto requestDto,
                                                                 @Size(max = 5) @RequestPart("file")List<MultipartFile> files) {
        var post = postService
                .createPostWithAttachments(postMapper.toPost(requestDto), requestDto.authorId(), requestDto.authorId(), files);
        return ResponseEntity.ok(postMapper.toPostResponseDto(post));
    }
}
