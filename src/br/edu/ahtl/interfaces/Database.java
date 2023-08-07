package br.edu.ahtl.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import br.edu.ahtl.models.Author;
import br.edu.ahtl.models.Book;
import br.edu.ahtl.models.Publisher;

public interface Database {
    
    void saveAuthor(Author author) throws IOException;
    void savePublisher(Publisher publisher) throws IOException;
    void saveReservedBooks(Map<Book, String> reserveds) throws IOException;
    void saveBorrowedBooks(Map<Book, String> borroweds) throws IOException;

    List<Author> getAuthors() throws IOException;
    List<Publisher> getPublishers() throws IOException;
    List<List<String>> getReserveds();
    List<List<String>> getBorroweds();

    void deleteReserve(String reserverName, Book book);
    void deleteBorrower(String borrowerName, Book book);
    void deleteBook(Book book);
}
