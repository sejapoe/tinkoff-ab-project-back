package edu.tinkoff.ninjamireaclone.exception.handler;

import edu.tinkoff.ninjamireaclone.dto.GenericResponseDto;
import edu.tinkoff.ninjamireaclone.exception.NotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public GenericResponseDto<String> handleClientError(NotFoundException ex) {
        return new GenericResponseDto<>(404, ex.getMessage());
    }
}
