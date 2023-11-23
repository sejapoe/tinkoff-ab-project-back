package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.common.PageRequestDto;
import edu.tinkoff.ninjamireaclone.dto.section.request.CreateSectionRequestDto;
import edu.tinkoff.ninjamireaclone.dto.section.request.UpdateSectionRequestDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.SectionResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.ShortSectionResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.PageMapper;
import edu.tinkoff.ninjamireaclone.mapper.SectionMapper;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.model.Topic;
import edu.tinkoff.ninjamireaclone.service.SectionService;
import edu.tinkoff.ninjamireaclone.utils.page.MultiPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
@Tag(name = "section", description = "Работа с разделами")
public class SectionController {
    private final SectionService sectionService;
    private final SectionMapper sectionMapper;
    private final PageMapper pageMapper;

    @Operation(description = "Получение корневого раздела")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Корневой раздел",
                    content = @Content(schema = @Schema(implementation = SectionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Корневой раздел отсутствует")
    })
    @GetMapping
    public ResponseEntity<SectionResponseDto> getRootSection(@ParameterObject PageRequestDto pageRequestDto) {
        Section section = sectionService.getRoot();
        return ResponseEntity.ok(sectionToPageDto(section, pageMapper.fromRequestDto(pageRequestDto)));
    }

    @Operation(description = "Получение раздела по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Раздел",
                    content = @Content(schema = @Schema(implementation = SectionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Раздел не найден")
    })
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<SectionResponseDto> getSection(@PathVariable Long id, @ParameterObject PageRequestDto pageRequestDto) {
        Section section = sectionService.get(id);
        return ResponseEntity.ok(sectionToPageDto(section, pageMapper.fromRequestDto(pageRequestDto)));
    }

    private SectionResponseDto sectionToPageDto(Section section, Pageable pageable) {
        MultiPage<Section, Topic> multiPage = sectionService.getMultiPage(section, pageable);
        return sectionMapper.toDto(section, multiPage);
    }

    @Operation(description = "Создание раздела")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Раздел создан",
                    content = @Content(schema = @Schema(implementation = SectionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неправильный формат данных"),
            @ApiResponse(responseCode = "404", description = "Родительский раздел не найден")
    })
    @PostMapping
    public ResponseEntity<ShortSectionResponseDto> createSection(@RequestBody @Valid CreateSectionRequestDto createSectionRequestDto) {
        Section section = sectionService.create(createSectionRequestDto.parentId(), createSectionRequestDto.name());
        return ResponseEntity.ok(sectionMapper.toShortDto(section));
    }

    @Operation(description = "Обновление раздела")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Раздел обновлен",
                    content = @Content(schema = @Schema(implementation = SectionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неправильный формат данных"),
            @ApiResponse(responseCode = "404", description = "Родительский раздел не найден")
    })
    @PutMapping
    public ResponseEntity<ShortSectionResponseDto> updateSection(@RequestBody @Valid UpdateSectionRequestDto createSectionDto) {
        Section section = sectionService.update(createSectionDto.id(), createSectionDto.name());
        return ResponseEntity.ok(sectionMapper.toShortDto(section));
    }

    @Operation(description = "Обновление раздела")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Раздел удален"),
            @ApiResponse(responseCode = "404", description = "Раздел не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
