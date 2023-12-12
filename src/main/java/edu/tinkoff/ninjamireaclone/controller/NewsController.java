package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.common.PageRequestDto;
import edu.tinkoff.ninjamireaclone.dto.common.PageResponseDto;
import edu.tinkoff.ninjamireaclone.dto.news.request.CreateCommentInThreadRequestDto;
import edu.tinkoff.ninjamireaclone.dto.news.request.CreateNewsCommentRequestDto;
import edu.tinkoff.ninjamireaclone.dto.news.request.CreateNewsRequestDto;
import edu.tinkoff.ninjamireaclone.dto.news.response.NewsCommentResponseDto;
import edu.tinkoff.ninjamireaclone.dto.news.response.NewsResponseDto;
import edu.tinkoff.ninjamireaclone.dto.news.response.ShortNewsResponseDto;
import edu.tinkoff.ninjamireaclone.dto.post.response.PostResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.NewsMapper;
import edu.tinkoff.ninjamireaclone.mapper.PageMapper;
import edu.tinkoff.ninjamireaclone.mapper.PostMapper;
import edu.tinkoff.ninjamireaclone.model.PostEntity;
import edu.tinkoff.ninjamireaclone.model.SectionEntity;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import edu.tinkoff.ninjamireaclone.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "news", description = "Работа с новостями")
@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final PageMapper pageMapper;
    private final AccountService accountService;
    private final PostMapper postMapper;

    @Operation(description = "Создание новости")
    @PreAuthorize("hasAuthority('CREATE_NEWS')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<NewsResponseDto> createNews(@ModelAttribute @Valid CreateNewsRequestDto dto) {
        SectionEntity news = newsService.createNews(
                dto.name(),
                dto.text(),
                dto.files()
        );

        PostEntity newsPost = newsService.getNewsRootTopicPost(news);

        return ResponseEntity.ok(newsMapper.toNewsResponseDto(news, newsPost));
    }

    @PreAuthorize("hasAuthority('VIEW')")
    @GetMapping
    public ResponseEntity<PageResponseDto<ShortNewsResponseDto>> getAllNews(@ParameterObject @Valid PageRequestDto dto) {
        var news = newsService.getAllNews(pageMapper.fromRequestDto(dto));
        return ResponseEntity.ok(pageMapper.toResponseDto(news, newsMapper::toShortNewsResponseDto));
    }

    @PreAuthorize("hasAuthority('VIEW')")
    @GetMapping("/{newsId}")
    public ResponseEntity<NewsResponseDto> getNews(@PathVariable @Valid Long newsId) {
        var news = newsService.getNews(newsId);
        var newsPost = newsService.getNewsRootTopicPost(news);
        return ResponseEntity.ok(newsMapper.toNewsResponseDto(news, newsPost));
    }

    @PreAuthorize("hasAuthority('VIEW')")
    @GetMapping("/{newsId}/comments")
    public ResponseEntity<PageResponseDto<NewsCommentResponseDto>> getComments(
            @PathVariable @Valid Long newsId,
            @ParameterObject @Valid PageRequestDto dto
    ) {
        var account = accountService.getCurrentUser();
        var comments = newsService.getComments(newsId, pageMapper.fromRequestDto(dto));

        var responseDto = pageMapper.toResponseDto(comments, comment -> {
            var post = newsService.getRootComment(comment);
            return newsMapper.toNewsCommentResponseDto(
                    comment,
                    post,
                    accountService.getCurrentUserId(),
                    account != null && account.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR"))
            );
        });

        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAuthority('VIEW')")
    @GetMapping("/comments/{threadId}")
    public ResponseEntity<PageResponseDto<PostResponseDto>> getThreadComments(
            @PathVariable @Valid Long threadId,
            @ParameterObject @Valid PageRequestDto dto
    ) {
        var account = accountService.getCurrentUser();
        var threadComments = newsService.getThread(threadId, pageMapper.fromRequestDto(dto));
        var responseDto = pageMapper.toResponseDto(threadComments, post ->
                postMapper.toPostResponseDto(
                        post,
                        accountService.getCurrentUserId(),
                        account != null && account.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR"))
                )
        );

        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAuthority('CREATE_COMMENT')")
    @PostMapping("/comments")
    public ResponseEntity<NewsCommentResponseDto> createComment(
            @RequestBody @Valid CreateNewsCommentRequestDto dto
    ) {
        var comment = newsService.createComment(
                dto.newsId(),
                dto.text(),
                dto.isAnonymous()
        );

        var account = accountService.getCurrentUser();

        var post = newsService.getRootComment(comment);
        var responseDto = newsMapper.toNewsCommentResponseDto(
                comment,
                post,
                accountService.getCurrentUserId(),
                account != null && account.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR"))
        );

        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasAuthority('CREATE_COMMENT')")
    @PostMapping("/comments/thread")
    public ResponseEntity<PostResponseDto> createThreadComment(
            @RequestBody @Valid CreateCommentInThreadRequestDto dto
    ) {
        var comment = newsService.createThreadComment(
                dto.threadId(),
                dto.text(),
                dto.isAnonymous()
        );

        var account = accountService.getCurrentUser();

        var responseDto = postMapper.toPostResponseDto(
                comment,
                accountService.getCurrentUserId(),
                account != null && account.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_MODERATOR"))
        );

        return ResponseEntity.ok(responseDto);
    }
}
