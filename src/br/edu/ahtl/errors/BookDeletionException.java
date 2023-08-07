package br.edu.ahtl.errors;

public class BookDeletionException extends Exception {
    public BookDeletionException() {
        super("could not delete book");
    }
}
