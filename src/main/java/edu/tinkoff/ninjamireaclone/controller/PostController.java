package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.post.request.CreatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.request.UpdatePostRequestDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;
import edu.tinkoff.ninjamireaclone.exception.AccessDeniedException;
import edu.tinkoff.ninjamireaclone.mapper.PostMapper;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Post;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import edu.tinkoff.ninjamireaclone.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@Tag(name = "post", description = "Работа с постами")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final AccountService accountService;

    @Operation(description = "Обновление поста")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост обновлен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @PreAuthorize("hasAuthority('DEFAULT')")
    @PatchMapping
    public ResponseEntity<PostResponseDto> update(@RequestBody @Valid UpdatePostRequestDto requestDto) {
        var post = postService.updatePost(requestDto.id(), requestDto.text());
        var responseDto = getPostResponseDto(post);
        log.info("Обновлен пост " + responseDto.id());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(description = "Удаление своего поста")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @PreAuthorize("hasAuthority('DEFAULT')")
    @DeleteMapping
    public ResponseEntity<Long> delete(@RequestParam Long id) {
        var post = postService.getPost(id);
        if (accountService.checkFakeId(post.getAuthor().getId())) {
            throw new AccessDeniedException("Удаление чужого поста");
        }
        var postId = postService.deletePost(id);
        log.info("Удален пост " + postId);
        return ResponseEntity.ok(postId);
    }

    @Operation(description = "Удаление поста администратором")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @PreAuthorize("hasAuthority('DELETE_COMMENT')")
    @DeleteMapping("/admin")
    public ResponseEntity<Long> deleteAdmin(@RequestParam Long id) {
        var postId = postService.deletePost(id);
        log.info("Удален пост " + postId);
        return ResponseEntity.ok(postId);
    }


    @Operation(description = "Получение поста")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    @PreAuthorize("hasAuthority('VIEW')")
    @GetMapping
    public ResponseEntity<PostResponseDto> get(@RequestParam Long id) {
        var post = postService.getPost(id);
        var responseDto = getPostResponseDto(post);
        log.info("Получен пост " + responseDto.id());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(description = "Создание поста")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пост создан"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных")
    })
    @PreAuthorize("hasAuthority('CREATE_COMMENT')")
    @PostMapping(value = "/withattach", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<PostResponseDto> createWithAttachments(@ModelAttribute @Valid CreatePostRequestDto requestDto) {
        if (accountService.checkFakeId(requestDto.authorId())) {
            throw new AccessDeniedException("Создание поста от чужого лица");
        }
        var post = postService.createPostWithAttachments(
                postMapper.toPost(requestDto),
                requestDto.authorId(),
                requestDto.parentId(),
                requestDto.files());
        return ResponseEntity.ok(getPostResponseDto(post));
    }

    private PostResponseDto getPostResponseDto(Post post) {
        Account currentUser = accountService.getCurrentUser();
        return postMapper.toPostResponseDto(
                post,
                accountService.getCurrentUserId(),
                currentUser != null && currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR"))
        );
    }
}
