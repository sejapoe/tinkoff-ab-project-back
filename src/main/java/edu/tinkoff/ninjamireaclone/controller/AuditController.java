package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.audit.PostAuditResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.AuditMapper;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import edu.tinkoff.ninjamireaclone.service.AuditService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
@Tag(name = "audit", description = "Аудит сущностей")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;
    private final AuditMapper auditMapper;
    private final AccountService accountService;

    @GetMapping
    public List<PostAuditResponseDto> getRevisions(@RequestParam Long id) {
        return auditService.getRevisions(id).stream().map(auditMapper::toPostAuditResponseDto).toList();
    }
}
