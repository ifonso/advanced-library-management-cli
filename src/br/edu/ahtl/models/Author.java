package br.edu.ahtl.models;

import java.util.ArrayList;

public class Author {

    private String name;
    private ArrayList<Book> books;
    
    public Author(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public Author(String name, ArrayList<Book> books) {
        this.name = name;
        this.books = books;
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public ArrayList<Book> getBooks() {
        return this.books;
    }

    // Utility
    public Book createBook(String bookTitle, Publisher publisher) {
        Book book = new Book(bookTitle, this.name, publisher.getName());
        this.books.add(book);
        return book;
    }

    public void removeBook(Book book) {
        this.books.remove(book);
    }
}
