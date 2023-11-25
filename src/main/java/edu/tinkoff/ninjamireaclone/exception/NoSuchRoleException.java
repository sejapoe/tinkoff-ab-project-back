package edu.tinkoff.ninjamireaclone.exception;

public class NoSuchRoleException extends RuntimeException {

    public NoSuchRoleException(String roleName) {
        super("Роль " + roleName + " не существует");
    }

    public NoSuchRoleException(String roleName, Throwable cause) {
        super("Роль " + roleName + " не существует", cause);
    }
}
