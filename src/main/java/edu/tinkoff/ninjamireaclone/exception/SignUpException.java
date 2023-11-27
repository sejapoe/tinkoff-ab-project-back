package edu.tinkoff.ninjamireaclone.exception;

public class SignUpException extends RuntimeException {

    public SignUpException(String message) {
        super(message);
    }

    public SignUpException(String message, Throwable cause) {
        super(message, cause);
    }
}
