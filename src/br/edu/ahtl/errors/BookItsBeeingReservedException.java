package br.edu.ahtl.errors;

public class BookItsBeeingReservedException extends Exception {
    public BookItsBeeingReservedException() {
        super("can't borrow a book that was reserved");
    }
}
