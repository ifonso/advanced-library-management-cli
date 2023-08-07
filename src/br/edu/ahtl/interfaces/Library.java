package br.edu.ahtl.interfaces;

import java.util.ArrayList;
import java.util.Map;

import br.edu.ahtl.errors.AuthorAlreadyExistsException;
import br.edu.ahtl.errors.BookAlreadyBorrowedException;
import br.edu.ahtl.errors.BookAlreadyExistsException;
import br.edu.ahtl.errors.BookAlreadyReservedException;
import br.edu.ahtl.errors.BookDeletionException;
import br.edu.ahtl.errors.BookItsBeeingReservedException;
import br.edu.ahtl.errors.PublisherAlreadyExistsException;

import br.edu.ahtl.models.Author;
import br.edu.ahtl.models.Book;
import br.edu.ahtl.models.Publisher;

public interface Library {

    void deleteBook(Book book) throws BookDeletionException;
    void borrowBook(String borrowerName, Book book) throws BookAlreadyBorrowedException, BookItsBeeingReservedException;
    void reserveBook(String reserverName, Book book) throws BookAlreadyReservedException;
    void returnBook(Book book);

    void createBook(Author author, Publisher publisher, String title) throws BookAlreadyExistsException;
    void createAuthor(String name) throws AuthorAlreadyExistsException;
    void createPublisher(String name) throws PublisherAlreadyExistsException;

    ArrayList<Book> getLibraryBooks();
    ArrayList<Author> getAuthors();
    ArrayList<Publisher> getPublishers();
    Map<Book, String> getReservedBooks();
    Map<Book, String> getBorrowedBooks();
}
