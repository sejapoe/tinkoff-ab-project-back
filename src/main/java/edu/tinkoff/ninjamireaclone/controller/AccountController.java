package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.account.request.UpdateAccountRequestDto;
import edu.tinkoff.ninjamireaclone.dto.account.response.AccountResponseDto;
import edu.tinkoff.ninjamireaclone.mapper.AccountMapper;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@Tag(name = "account", description = "Авторизация на сайте")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountMapper accountMapper;
    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable long id) {
        return ResponseEntity.ok(accountMapper.toAccountDto(accountService.getById(id)));
    }

    @PatchMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<AccountResponseDto> updateAccount(@ModelAttribute UpdateAccountRequestDto updateAccountRequestDto) {
        Account account = accountMapper.toAccount(updateAccountRequestDto);
        Account updated = accountService.update(account, updateAccountRequestDto.avatar());
        return ResponseEntity.ok(accountMapper.toAccountDto(updated));
    }
}
