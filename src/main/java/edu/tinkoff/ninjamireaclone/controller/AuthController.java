package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.dto.auth.request.JwtRequestDto;
import edu.tinkoff.ninjamireaclone.dto.auth.request.SignUpRequestDto;
import edu.tinkoff.ninjamireaclone.dto.auth.response.JwtResponseDto;
import edu.tinkoff.ninjamireaclone.exception.AccessDeniedException;
import edu.tinkoff.ninjamireaclone.exception.SignUpException;
import edu.tinkoff.ninjamireaclone.mapper.AccountMapper;
import edu.tinkoff.ninjamireaclone.model.Account;
import edu.tinkoff.ninjamireaclone.model.Role;
import edu.tinkoff.ninjamireaclone.service.AccountService;
import edu.tinkoff.ninjamireaclone.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "Авторизация на сайте")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody JwtRequestDto requestDto) {
        var userDetails = accountService.loadUserByUsername(requestDto.name());
        var accountId = ((Account) userDetails).getId();
        JwtResponseDto jwtResponseDto = getResponseDto((Account) userDetails, requestDto.name(), requestDto.password());
        return ResponseEntity.ok(jwtResponseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtResponseDto> signUp(@RequestBody SignUpRequestDto requestDto) {
        if (!requestDto.password().equals(requestDto.confirmPassword())) {
            throw new SignUpException("Пароли не совпадают");
        }
        Account createdAccount = accountService.createAccount(accountMapper.toAccount(requestDto));
        JwtResponseDto jwtResponseDto = getResponseDto(createdAccount, requestDto.name(), requestDto.password());
        return ResponseEntity.ok(jwtResponseDto);
    }

    @NotNull
    private JwtResponseDto getResponseDto(Account account, String name, String password) {
        return new JwtResponseDto(
                account.getId(),
                account.getName(),
                authService.createAuthToken(account, name, password),
                account.getRoles().stream().map(Role::getName).toList()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException accessDeniedException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
    }

    @ExceptionHandler(SignUpException.class)
    public ProblemDetail handleSignUp(SignUpException signUpException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, signUpException.getMessage());
    }
}
