package edu.tinkoff.ninjamireaclone.exception;

public class NoSuchPrivilegeException extends RuntimeException {

    public NoSuchPrivilegeException(String roleName) {
        super("Роль " + roleName + " не существует");
    }

    public NoSuchPrivilegeException(String roleName, Throwable cause) {
        super("Роль " + roleName + " не существует", cause);
    }
}