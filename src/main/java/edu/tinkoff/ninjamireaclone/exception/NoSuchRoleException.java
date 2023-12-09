package edu.tinkoff.ninjamireaclone.exception;

public class NoSuchRoleException extends RuntimeException {

    public NoSuchRoleException(String privilegeName) {
        super("Роль " + privilegeName + " не существует");
    }

    public NoSuchRoleException(String privilegeName, Throwable cause) {
        super("Роль " + privilegeName + " не существует", cause);
    }
}
