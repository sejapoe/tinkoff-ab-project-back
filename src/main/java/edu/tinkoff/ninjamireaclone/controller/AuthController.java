package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.auth.request.JwtRequestDto;
import edu.tinkoff.ninjamireaclone.dto.auth.request.SignUpRequestDto;
import edu.tinkoff.ninjamireaclone.dto.auth.response.JwtResponseDto;
import edu.tinkoff.ninjamireaclone.dto.auth.response.SignUpResponseDto;
import edu.tinkoff.ninjamireaclone.exception.AccountAlreadyExistsException;
import edu.tinkoff.ninjamireaclone.exception.ResourceNotFoundException;
import edu.tinkoff.ninjamireaclone.exception.SignUpException;
import edu.tinkoff.ninjamireaclone.mapper.AccountMapper;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import edu.tinkoff.ninjamireaclone.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "Авторизация на сайте")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    public ResponseEntity<JwtResponseDto> createAuthToken(@RequestBody JwtRequestDto requestDto) {
        var userDetails = accountService.loadUserByUsername(requestDto.name());
        var accountId = ((Account) userDetails).getId();
        return ResponseEntity.ok(new JwtResponseDto(accountId, authService.createAuthToken(userDetails, requestDto.name(), requestDto.password())));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {
        if (!requestDto.password().equals(requestDto.confirmPassword())) {
            throw new SignUpException("Пароли не совпадают");
        }
        try {
            accountService.getByName(requestDto.name());
            throw new AccountAlreadyExistsException("Пользователь с указанным именем уже существует");
        }
        catch (ResourceNotFoundException ex) {
            Account createdAccount = accountService.createAccount(accountMapper.toAccount(requestDto));
            return ResponseEntity.ok(accountMapper.toAccountDto(createdAccount));
        }
    }
}
