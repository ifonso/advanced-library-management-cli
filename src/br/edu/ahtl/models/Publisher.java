package br.edu.ahtl.models;

import java.util.ArrayList;

public class Publisher {

    private String name;
    private ArrayList<Book> books;

    public Publisher(String name) {
        this.name = name;
        this.books = new ArrayList<>();
    }

    public Publisher(String name, ArrayList<Book> books) {
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
    public void publishBook(Book book) {
        this.books.add(book);
    }

    public void removePublishedBook(Book book) {
        this.books.remove(book);
    }
}
