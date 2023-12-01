package edu.tinkoff.ninjamireaclone.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super("Конфликт: " + message);
    }

    public ConflictException(String message, Throwable cause) {
        super("Конфликт: " + message, cause);
    }
}
