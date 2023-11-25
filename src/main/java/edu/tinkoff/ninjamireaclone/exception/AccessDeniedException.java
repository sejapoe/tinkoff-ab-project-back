package edu.tinkoff.ninjamireaclone.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super("Доступ запрещен: " + message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super("Доступ запрещен: " + message, cause);
    }
}
