package br.edu.ahtl.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ahtl.errors.AuthorAlreadyExistsException;
import br.edu.ahtl.errors.BookAlreadyBorrowedException;
import br.edu.ahtl.errors.BookAlreadyExistsException;
import br.edu.ahtl.errors.BookAlreadyReservedException;
import br.edu.ahtl.errors.BookDeletionException;
import br.edu.ahtl.errors.BookItsBeeingReservedException;
import br.edu.ahtl.errors.PublisherAlreadyExistsException;
import br.edu.ahtl.interfaces.Database;
import br.edu.ahtl.interfaces.Library;
import br.edu.ahtl.models.Author;
import br.edu.ahtl.models.Book;
import br.edu.ahtl.models.Publisher;

public class LibraryService implements Library {
    
    private ArrayList<Author> authors;
    private ArrayList<Publisher> publishers;
    private ArrayList<Book> libraryBooks;

    private Map<Book, String> reservedBooks;
    private Map<Book, String> borrowedBooks;

    private Database database;

    public LibraryService(Database database) {
        authors = new ArrayList<>();
        publishers = new ArrayList<>();
        libraryBooks = new ArrayList<>();

        reservedBooks = new HashMap<>();
        borrowedBooks = new HashMap<>();

        this.database = database;

        try {
            List<Author> storedAuthors = database.getAuthors();
            List<Publisher> storedPublishers = database.getPublishers();
            List<List<String>> storedReservedBooks = database.getReserveds();
            List<List<String>> storedBorrowedBooks = database.getBorroweds();

            this.authors.addAll(storedAuthors);
            this.publishers.addAll(storedPublishers);

            for (Author author : this.authors) {
                for (Book book : author.getBooks()) {
                    for (Publisher publisher : this.publishers) {
                        if (book.getPublisherName().equals(publisher.getName())) {
                            publisher.publishBook(book);
                        }
                    }

                    this.libraryBooks.add(book);
                }
            }

            for (List<String> reserve : storedReservedBooks) {
                for (int i = 1; i < reserve.size(); i++) {
                    for (Book book : this.libraryBooks) {
                        if (reserve.get(i).equals(book.getTitle())) {
                            this.reservedBooks.put(book, reserve.get(0));
                        }
                    }
                }
            }

            for (List<String> borrow : storedBorrowedBooks) {
                for (int i = 1; i < borrow.size(); i++) {
                    for (Book book : this.libraryBooks) {
                        if (borrow.get(i).equals(book.getTitle())) {
                            this.borrowedBooks.put(book, borrow.get(0));
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao tentar acessar o banco de dados.");
            System.out.println(e);
        }
    }

    // Library actions
    public void reserveBook(String reserverName, Book book) throws BookAlreadyReservedException {
        if (bookAlreadyReserved(book.getTitle())) {
            throw new BookAlreadyReservedException();
        }

        reservedBooks.put(book, reserverName);

        try {
            database.saveReservedBooks(reservedBooks);
        } catch (Exception e) {
            System.out.println("Um erro ocorreu enquanto persistiamos os dados.");
        }
    }

    public void borrowBook(String borrowerName, Book book) throws BookAlreadyBorrowedException, BookItsBeeingReservedException {
        if (bookAlreadyBorrowed(book.getTitle())) {
            throw new BookAlreadyBorrowedException();
        }

        if (bookAlreadyReserved(book.getTitle())) {
            if (isBookReserver(book, borrowerName)) {
                borrowedBooks.put(book, borrowerName);
                reservedBooks.remove(book);
                database.deleteReserve(borrowerName, book);
            } else {
                throw new BookItsBeeingReservedException();
            }
        }

        borrowedBooks.put(book, borrowerName);

        try {
            database.saveBorrowedBooks(borrowedBooks);
        } catch (Exception e) {
            System.out.println("Um erro ocorreu enquanto persistiamos os dados.");
        }
    }

    public void returnBook(Book book) {
        String borrower = borrowedBooks.get(book);

        borrowedBooks.remove(book);
        database.deleteBorrower(borrower, book);
    }

    // Delete
    public void deleteBook(Book book) throws BookDeletionException {
        if (reservedBooks.get(book) != null || borrowedBooks.get(book) != null) {
            throw new BookDeletionException();
        }

        for (Publisher publisher : publishers) {
            if (publisher.getName().equals(book.getPublisherName())) {
                publisher.removePublishedBook(book);
            }
        }

        for (Author author : authors) {
            if (author.getName().equals(book.getAuthorName())) {
                author.removeBook(book);
            }
        }

        libraryBooks.remove(book);
        database.deleteBook(book);
    }

    // Create
    public void createBook(Author author, Publisher publisher, String title) throws BookAlreadyExistsException {
        if (bookAlreadyExists(title)) {
            throw new BookAlreadyExistsException();
        }

        Book book = author.createBook(title, publisher);
        
        libraryBooks.add(book);
        publisher.publishBook(book);

        try {
            database.saveAuthor(author);
            database.savePublisher(publisher);
        } catch (Exception e) {
            System.out.println("Um erro ocorreu enquanto persistiamos os dados.");
        }
    }

    public void createAuthor(String name) throws AuthorAlreadyExistsException {
        if (authorAlreadyExists(name)) {
            throw new AuthorAlreadyExistsException();
        }

        Author newAuthor = new Author(name);

        authors.add(newAuthor);

        try {
            database.saveAuthor(newAuthor);
        } catch (Exception e) {
            System.out.println("Um erro ocorreu enquanto persistiamos os dados.");
        }
    }

    public void createPublisher(String name) throws PublisherAlreadyExistsException {
        if (publisherAlreadyExists(name)) {
            throw new PublisherAlreadyExistsException();
        }

        Publisher newPublisher = new Publisher(name);

        publishers.add(newPublisher);

        try {
            database.savePublisher(newPublisher);
        } catch (Exception e) {
            System.out.println("Um erro ocorreu enquanto persistiamos os dados.");
        }
    }

    // Getters
    public ArrayList<Book> getLibraryBooks() {
        return libraryBooks;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public ArrayList<Publisher> getPublishers() {
        return publishers;
    }

    public Map<Book, String> getReservedBooks() {
        return reservedBooks;
    }

    public Map<Book, String> getBorrowedBooks() {
        return borrowedBooks;
    }

    // Utility
    private boolean bookAlreadyExists(String title) {
        return libraryBooks.stream().anyMatch(book -> book.getTitle().equals(title));
    }

    private boolean authorAlreadyExists(String name) {
        return authors.stream().anyMatch(author -> author.getName().equals(name));
    }

    private boolean publisherAlreadyExists(String name) {
        return publishers.stream().anyMatch(publisher -> publisher.getName().equals(name));
    }

    private boolean bookAlreadyBorrowed(String title) {
        return borrowedBooks
        .keySet().stream().anyMatch(book -> book.getTitle().equals(title));
    }

    private boolean bookAlreadyReserved(String title) {
        return reservedBooks
        .keySet().stream().anyMatch(book -> book.getTitle().equals(title));
    }

    private boolean isBookReserver(Book book, String reserverName) {
        return reservedBooks.get(book).equals(reserverName);
    }
}
