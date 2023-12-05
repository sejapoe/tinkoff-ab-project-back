package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.annotation.IsAdmin;
import edu.tinkoff.ninjamireaclone.annotation.IsUser;
import edu.tinkoff.ninjamireaclone.dto.account.request.UpdateAccountRequestDto;
import edu.tinkoff.ninjamireaclone.dto.account.response.AccountResponseDto;
import edu.tinkoff.ninjamireaclone.dto.common.PageRequestDto;
import edu.tinkoff.ninjamireaclone.dto.common.PageResponseDto;
import edu.tinkoff.ninjamireaclone.exception.AccessDeniedException;
import edu.tinkoff.ninjamireaclone.mapper.AccountMapper;
import edu.tinkoff.ninjamireaclone.mapper.PageMapper;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Tag(name = "account", description = "Работа с аккаунтами")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final PageMapper pageMapper;

    @Operation(description = "Получение аккаунта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аккаунт найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> get(@PathVariable Long id) {
        var account = accountService.getById(id);
        var responseDto = accountMapper.toAccountResponseDto(account);
        log.info("Получен аккаунт " + responseDto.id());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(description = "Получение всех аккаунтов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аккаунт найден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
    })
    @IsAdmin
    @GetMapping
    public ResponseEntity<PageResponseDto<AccountResponseDto>> getAll(@ParameterObject PageRequestDto pageRequestDto) {
        var accounts = accountService.getAll(pageMapper.fromRequestDto(pageRequestDto));
        var response = accountMapper.toPageResponseDto(accounts);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Удаление аккаунта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аккаунт удален"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @IsAdmin
    @DeleteMapping
    public ResponseEntity<Long> delete(@RequestParam Long id) {
        var accountId = accountService.deleteAccount(id);
        log.info("Удален аккаунт " + accountId);
        return ResponseEntity.ok(accountId);
    }

    @Operation(description = "Редактирование аккаунта")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аккаунт изменен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
    })
    @IsUser
    @PatchMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AccountResponseDto> updateAccount(@ModelAttribute UpdateAccountRequestDto updateAccountRequestDto) {
        if (accountService.checkFakeId(updateAccountRequestDto.id())) {
            throw new AccessDeniedException("Редактирование чужого профиля");
        }
        Account account = accountMapper.toAccount(updateAccountRequestDto);
        Account updated = accountService.update(account, updateAccountRequestDto.avatar());
        log.info("Изменен аккаунт " + updated.getId());
        return ResponseEntity.ok(accountMapper.toAccountResponseDto(updated));
    }
}
