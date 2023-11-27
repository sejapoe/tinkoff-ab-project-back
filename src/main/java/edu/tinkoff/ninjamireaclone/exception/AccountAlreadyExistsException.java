package edu.tinkoff.ninjamireaclone.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String accountName) {
        super("Аккаунт с именем " + accountName + " уже существует");
    }

    public AccountAlreadyExistsException(String accountName, Throwable cause) {
        super("Аккаунт с именем " + accountName + " уже существует", cause);
    }
}
