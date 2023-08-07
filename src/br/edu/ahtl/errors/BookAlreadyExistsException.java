package br.edu.ahtl.errors;

public class BookAlreadyExistsException extends Exception {
    public BookAlreadyExistsException() {
        super("book with that title already registered");
    }
}
