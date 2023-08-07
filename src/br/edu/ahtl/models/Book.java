package br.edu.ahtl.models;

public class Book {

    private String title;
    private String authorName;
    private String publisherName;

    public Book(String title, String authorName, String publisherName) {
        this.title = title;
        this.authorName = authorName;
        this.publisherName = publisherName;
    }

    // Getters
    public String getTitle() {
        return this.title;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public String getPublisherName() {
        return this.publisherName;
    }

    // Setters
    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }
}