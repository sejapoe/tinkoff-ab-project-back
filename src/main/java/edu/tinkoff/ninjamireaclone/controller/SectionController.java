package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.GenericResponseDto;
import edu.tinkoff.ninjamireaclone.dto.section.CreateSectionDto;
import edu.tinkoff.ninjamireaclone.dto.section.SectionDto;
import edu.tinkoff.ninjamireaclone.dto.section.ShortSectionDto;
import edu.tinkoff.ninjamireaclone.dto.section.UpdateSectionDto;
import edu.tinkoff.ninjamireaclone.mapper.SectionMapper;
import edu.tinkoff.ninjamireaclone.model.Section;
import edu.tinkoff.ninjamireaclone.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;
    private final SectionMapper sectionMapper;

    @GetMapping
    public GenericResponseDto<SectionDto> getRootSection() {
        Section section = sectionService.getRoot();
        return new GenericResponseDto<>(HttpStatus.OK.value(), sectionMapper.toDto(section));
    }

    @GetMapping("/{id:\\d+}")
    public GenericResponseDto<SectionDto> getSection(@PathVariable Long id) {
        Section section = sectionService.get(id);
        return new GenericResponseDto<>(HttpStatus.OK.value(), sectionMapper.toDto(section));
    }

    @PostMapping
    public GenericResponseDto<ShortSectionDto> createSection(@RequestBody CreateSectionDto createSectionDto) {
        Section section = sectionService.create(createSectionDto.parentId(), createSectionDto.name());
        return new GenericResponseDto<>(HttpStatus.CREATED.value(), sectionMapper.toShortDto(section));
    }

    @PutMapping
    public GenericResponseDto<ShortSectionDto> updateSection(@RequestBody UpdateSectionDto createSectionDto) {
        Section section = sectionService.update(createSectionDto.id(), createSectionDto.name());
        return new GenericResponseDto<>(HttpStatus.OK.value(), sectionMapper.toShortDto(section));
    }

    @DeleteMapping("/{id}")
    public GenericResponseDto<Void> deleteSection(@PathVariable Long id) {
        sectionService.delete(id);
        return new GenericResponseDto<>(HttpStatus.NO_CONTENT.value(), null);
    }
}
