package edu.tinkoff.ninjamireaclone.exception.handler;

import edu.tinkoff.ninjamireaclone.exception.AccountAlreadyExistsException;
import edu.tinkoff.ninjamireaclone.exception.ConflictException;
import edu.tinkoff.ninjamireaclone.exception.NoSuchRoleException;
import edu.tinkoff.ninjamireaclone.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.text.ParseException;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleClientError(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceed(MaxUploadSizeExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage());
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ProblemDetail handleAccountAlreadyExists(AccountAlreadyExistsException accountAlreadyExistsException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, accountAlreadyExistsException.getMessage());
    }

    @ExceptionHandler(NoSuchRoleException.class)
    public ProblemDetail handleNoSuchRole(NoSuchRoleException noSuchRoleException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, noSuchRoleException.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException badCredentialsException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, badCredentialsException.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException conflictException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, conflictException.getMessage());
    }

    @ExceptionHandler(ParseException.class)
    public ProblemDetail handleParseException(ParseException parseException) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, parseException.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }
}
