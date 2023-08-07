package br.edu.ahtl.database;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.edu.ahtl.interfaces.Database;
import br.edu.ahtl.models.Author;
import br.edu.ahtl.models.Book;
import br.edu.ahtl.models.Publisher;

public class LibraryRepository implements Database {

    private String mainPath = "./database";
    private String authorsPath = "./database/authors";
    private String publishersPath = "./database/publishers";
    private String reservedsPath = "./database/reserveds";
    private String borrowedsPath = "./database/borroweds";
    
    private static LibraryRepository instance;

    private LibraryRepository() {
        new File(mainPath).mkdir();
        new File(authorsPath).mkdir();
        new File(publishersPath).mkdir();
        new File(reservedsPath).mkdir();
        new File(borrowedsPath).mkdir();
    }

    public static LibraryRepository getInstance() {
        if (instance == null) {
            instance = new LibraryRepository();
        }
        return instance;
    }

    // Saves
    public void saveAuthor(Author author) throws IOException {
        String authorPath = authorsPath + "/" + author.getName();
        new File(authorPath).mkdir();

        for (Book book : author.getBooks()) {
            List<String> lines = new ArrayList<>();

            lines.add(book.getTitle());
            lines.add(book.getAuthorName());
            lines.add(book.getPublisherName());

            writeFile(authorPath, getFileName(book.getTitle()), lines);
        }
    }

    public void savePublisher(Publisher publisher) throws IOException {
        String publisherPath = publishersPath + "/" + publisher.getName();
        new File(publisherPath).mkdir();

        for (Book book : publisher.getBooks()) {
            List<String> lines = new ArrayList<>();

            lines.add(book.getTitle());
            lines.add(book.getAuthorName());
            lines.add(book.getPublisherName());

            writeFile(publisherPath, getFileName(book.getTitle()), lines);
        }
    }

    public void saveReservedBooks(Map<Book, String> reserveds) throws IOException {
        for (Map.Entry<Book, String> reserved : reserveds.entrySet()) {
            String pathName = reservedsPath + "/" + reserved.getValue();
            String filename = reserved.getKey().getTitle();

            new File(pathName).mkdir();
            writeFile(pathName, filename, new ArrayList<String>());
        }
    }

    public void saveBorrowedBooks(Map<Book, String> borroweds) throws IOException {
        for (Map.Entry<Book, String> borrowed : borroweds.entrySet()) {
            String pathName = borrowedsPath + "/" + borrowed.getValue();
            String filename = borrowed.getKey().getTitle();

            new File(pathName).mkdir();
            writeFile(pathName, filename, new ArrayList<String>());
        }
    }

    // Getters
    public List<Author> getAuthors() throws IOException {
        List<String> authorPaths = getNamesInPath(authorsPath);
        List<Author> authors = new ArrayList<>();

        for (String path : authorPaths) {
            authors.add(createAuthorFromPath(path));
        }

        return authors;
    }

    public List<Publisher> getPublishers() throws IOException {
        List<String> publishersPaths = getNamesInPath(publishersPath);
        List<Publisher> publishers = new ArrayList<>();

        for (String path : publishersPaths) {
            publishers.add(createPublisherFromPath(path));
        }

        return publishers;
    }

    public List<List<String>> getReserveds() {
        List<List<String>> reserveds = new ArrayList<>();

        for (String reserver : getNamesInPath(reservedsPath)) {
            List<String> reservedBooks = new ArrayList<>();
            reservedBooks.add(reserver);
            reservedBooks.addAll(getNamesInPath(reservedsPath + "/" + reserver));

            reserveds.add(reservedBooks);
        }

        return reserveds;
    }

    public List<List<String>> getBorroweds() {
        List<List<String>> borroweds = new ArrayList<>();

        for (String borrower : getNamesInPath(borrowedsPath)) {
            List<String> borrowedBooks = new ArrayList<>();
            borrowedBooks.add(borrower);
            borrowedBooks.addAll(getNamesInPath(borrowedsPath + "/" + borrower));

            borroweds.add(borrowedBooks);
        }

        return borroweds;
    }

    // Delete
    public void deleteReserve(String reserverName, Book book) {
        String reserverPath = reservedsPath + "/" + reserverName;
        File file = new File(reserverPath + "/" + book.getTitle());

        file.delete();

        if (getNamesInPath(reserverPath).isEmpty()) {
            File path = new File(reserverPath);
            path.delete();
        }
    }

    public void deleteBorrower(String borrowerName, Book book) {
        String borrowerPath = borrowedsPath + "/" + borrowerName;
        File file = new File(borrowerPath + "/" + book.getTitle());

        file.delete();

        if (getNamesInPath(borrowerPath).isEmpty()) {
            File path = new File(borrowerPath);
            path.delete();
        }
    }

    public void deleteBook(Book book) {
        String bookAuthorPath = authorsPath + "/" + book.getAuthorName() + "/" + getFileName(book.getTitle());
        String bookPublisherPath = publishersPath + "/" + book.getPublisherName() + "/" + getFileName(book.getTitle());

        File bookAuthor = new File(bookAuthorPath);
        File bookPublisher = new File(bookPublisherPath);

        bookAuthor.delete();
        bookPublisher.delete();
    }

    // Object utilities
    private Author createAuthorFromPath(String pathName) throws IOException {
        String authorPath = authorsPath + "/" + pathName;
        List<String> authorBooks = getNamesInPath(authorPath);
        Author author = new Author(pathName);

        for (String bookPath : authorBooks) {
            List<String> lines = Files.readAllLines(Paths.get(authorPath + "/" + bookPath), StandardCharsets.UTF_8);
            Publisher publisher = new Publisher(lines.get(2));
            author.createBook(lines.get(0), publisher);
        }

        return author;
    }

    private Publisher createPublisherFromPath(String pathName) {
        return new Publisher(pathName);
    }

    // Utility
    private void writeFile(String path, String filename, List<String> content) throws IOException {
        Path currentPath = Paths.get(path + "/" + filename);
        Files.write(currentPath, content, StandardCharsets.UTF_8);
    }

    private String getFileName(String identifier) {
        String[] words = identifier.split(" ");
        return String.join("-", words);
    }

    private List<String> getNamesInPath(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        List<String> filesName = new ArrayList<>();

        for (File file : files) {
            filesName.add(file.getName());
        }

        return filesName;
    }
}
