package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.section.request.CreateSectionRequestDto;
import edu.tinkoff.ninjamireaclone.dto.section.request.UpdateSectionRequestDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.SectionResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.response.ShortSectionResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.SectionMapper;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;
    private final SectionMapper sectionMapper;

    @GetMapping
    public ResponseEntity<SectionResponseDto> getRootSection() {
        Section section = sectionService.getRoot();
        return ResponseEntity.ok(sectionMapper.toDto(section));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<SectionResponseDto> getSection(@PathVariable Long id) {
        Section section = sectionService.get(id);
        return ResponseEntity.ok(sectionMapper.toDto(section));
    }

    @PostMapping
    public ResponseEntity<ShortSectionResponseDto> createSection(@RequestBody @Valid CreateSectionRequestDto createSectionRequestDto) {
        Section section = sectionService.create(createSectionRequestDto.parentId(), createSectionRequestDto.name());
        return ResponseEntity.ok(sectionMapper.toShortDto(section));
    }

    @PutMapping
    public ResponseEntity<ShortSectionResponseDto> updateSection(@RequestBody @Valid UpdateSectionRequestDto createSectionDto) {
        Section section = sectionService.update(createSectionDto.id(), createSectionDto.name());
        return ResponseEntity.ok(sectionMapper.toShortDto(section));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
