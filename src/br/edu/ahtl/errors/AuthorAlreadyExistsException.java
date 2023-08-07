package br.edu.ahtl.errors;

public class AuthorAlreadyExistsException extends Exception {
    public AuthorAlreadyExistsException() {
        super("author with that name already registered");
    }
}
