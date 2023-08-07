package br.edu.ahtl.errors;

public class PublisherAlreadyExistsException extends Exception {
    public PublisherAlreadyExistsException() {
        super("publisher with that name already registered");
    }
}
