package br.edu.ahtl.errors;

public class BookAlreadyBorrowedException extends Exception {
    public BookAlreadyBorrowedException() {
        super("you can't borrow a book that is already borrowed");
    }
}
