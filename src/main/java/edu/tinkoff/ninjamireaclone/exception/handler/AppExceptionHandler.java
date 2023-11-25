package edu.tinkoff.ninjamireaclone.exception.handler;

import edu.tinkoff.ninjamireaclone.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleClientError(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException badCredentialsException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, badCredentialsException.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException accessDeniedException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ProblemDetail handleAccountAlreadyExists(AccountAlreadyExistsException accountAlreadyExistsException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, accountAlreadyExistsException.getMessage());
    }

    @ExceptionHandler(NoSuchRoleException.class)
    public ProblemDetail handleNoSuchRole(NoSuchRoleException noSuchRoleException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, noSuchRoleException.getMessage());
    }

    @ExceptionHandler(SignUpException.class)
    public ProblemDetail handleSignUp(SignUpException signUpException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, signUpException.getMessage());
    }
}
