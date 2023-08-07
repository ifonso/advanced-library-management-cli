package br.edu.ahtl.errors;

public class BookAlreadyReservedException extends Exception {
    public BookAlreadyReservedException() {
        super("you can't reserve a book that is already reserved");
    }
}
