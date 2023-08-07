package br.edu.ahtl.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import br.edu.ahtl.errors.AuthorAlreadyExistsException;
import br.edu.ahtl.errors.BookAlreadyBorrowedException;
import br.edu.ahtl.errors.BookAlreadyExistsException;
import br.edu.ahtl.errors.BookAlreadyReservedException;
import br.edu.ahtl.errors.BookDeletionException;
import br.edu.ahtl.errors.BookItsBeeingReservedException;
import br.edu.ahtl.errors.PublisherAlreadyExistsException;

import br.edu.ahtl.interfaces.Library;
import br.edu.ahtl.models.Author;
import br.edu.ahtl.models.Book;
import br.edu.ahtl.models.Publisher;

public class ComandLineInterface {

    private Library library;

    public ComandLineInterface(Library libraryService) {
        this.library = libraryService;
    }
    
    public void main() {
        Scanner scanner = new Scanner(System.in);
        int input = -1;

        while (input != 0) {
            clearScreen();

            System.out.println("Opções:\n");
            System.out.println("[0] Encerrar o programa\n");
            
            System.out.println("[1] Listar livros na biblioteca");
            System.out.println("[2] Listar autores");
            System.out.println("[3] Listar editoras");
            System.out.println("[4] Listar livros reservados");
            System.out.println("[5] Listar livros emprestados\n");

            System.out.println("[6] Remover livro da biblioteca");
            System.out.println("[7] Adicionar livro a biblioteca");
            System.out.println("[8] Adicionar autor");
            System.out.println("[9] Adicionar editora\n");

            System.out.println("[10] Emprestar livro da biblioteca");
            System.out.println("[11] Reservar livro da biblioteca");
            System.out.println("[12] Devolver livro para a biblioteca\n");

            input = getIntInput(scanner);

            switch (input) {
                case 0:
                    continue;
                case 1:
                    listLibraryBooks(scanner);
                    break;
                case 2:
                    listAuthors(scanner);
                    break;
                case 3:
                    listPublishers(scanner);
                    break;
                case 4:
                    listReservedBooks(scanner);
                    break;
                case 5:
                    listBorrowedBooks(scanner);
                    break;
                case 6:
                    removeBook(scanner);
                    break;
                case 7:
                    createBook(scanner);
                    break;
                case 8:
                    createAuthor(scanner);
                    break;
                case 9:
                    createPublisher(scanner);
                    break;
                case 10:
                    borrowBook(scanner);
                    break;
                case 11:
                    reserveBook(scanner);
                    break;
                case 12:
                    returnBook(scanner);
                    break;
                default:
                    printErrorMessage(scanner, "Entrada inválida! Pressione \"Enter\" para tentar novamente.");
            }
        }
    }

    // Library actions
    private void borrowBook(Scanner in) {
        clearScreen();
        System.out.println("Qual seu nome?");
        System.out.printf("-> ");

        String name = in.nextLine();

        clearScreen();
        System.out.println("Qual livro você deseja emprestar?\n");

        ArrayList<Book> books = library.getLibraryBooks();

        for (int i = 0; i < books.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, books.get(i).getTitle());
        }

        System.out.println();

        int bookNumber = getIntInput(in);

        if (bookNumber > books.size() || bookNumber < 1) {
            printErrorMessage(in, "O livro selecionado é inválido.\nPressione \"Enter\" para continuar.");
            return;
        }

        try {
            library.borrowBook(name, books.get(bookNumber - 1));
        } catch (BookAlreadyBorrowedException e) {
            printErrorMessage(in, "Livro não disponível para empréstimo.\nPressione \"Enter\" para continuar.");
        } catch (BookItsBeeingReservedException e) {
            printErrorMessage(in, "Livro reservado não pode ser emprestado.\nPressione \"Enter\" para continuar."); 
        }
    }

    private void reserveBook(Scanner in) {
        clearScreen();
        Map<Book, String> borrowedBooks = library.getBorrowedBooks();
        ArrayList<Book> books = new ArrayList<>();

        borrowedBooks.keySet().stream().forEach(book -> books.add(book));

        if (borrowedBooks.isEmpty()) {
            printErrorMessage(in, "Nenhum livro está sendo emprestado no momento para reservar.\nPressione \"Enter\" para continuar.");
            return;
        }
        
        System.out.println("Qual seu nome?");
        System.out.printf("-> ");

        String name = in.nextLine();

        clearScreen();
        System.out.println("Qual livro você deseja reservar?\n");

        for (int i = 0; i < books.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, books.get(i).getTitle());
        }

        System.out.println();

        int bookNumber = getIntInput(in);

        if (bookNumber > books.size() || bookNumber < 1) {
            printErrorMessage(in, "O livro selecionado é inválido.\nPressione \"Enter\" para continuar.");
            return;
        }

        try {
            library.reserveBook(name, books.get(bookNumber - 1));
        } catch (BookAlreadyReservedException e) {
            printErrorMessage(in, "O livro selecionado já foi reservado.\nPressione \"Enter\" para continuar.");
        }
    }

    private void returnBook(Scanner in) {
        Collection<String> bookBorrowers = library.getBorrowedBooks().values();
        ArrayList<Book> books = new ArrayList<>();

        clearScreen();
        System.out.println("Qual o seu nome?");
        System.out.printf("-> ");

        String borrowerName = in.nextLine();

        clearScreen();
        if (!bookBorrowers.contains(borrowerName)) {
            printErrorMessage(in, "Usuário \"" + borrowerName + "\" não tem nenhum livro emprestado.\nPressione \"Enter\" para continuar.");
            return;
        }

        for (Map.Entry<Book, String> entry : library.getBorrowedBooks().entrySet()) {
            if (entry.getValue().equals(borrowerName)) {
                books.add(entry.getKey());
            }
        }

        System.out.println("Qual livro você deseja devolver?\n");

        for (int i = 0; i < books.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, books.get(i).getTitle());
        }

        System.out.println();

        int bookNumber = getIntInput(in);

        if (bookNumber > books.size() || bookNumber < 1) {
            printErrorMessage(in, "O livro selecionado é inválido.\nPressione \"Enter\" para continuar.");
            return;
        }

        library.returnBook(books.get(bookNumber - 1));
    }

    // Read
    private void listLibraryBooks(Scanner in) {
        clearScreen();
        ArrayList<Book> books = library.getLibraryBooks();

        if (books.isEmpty()) {
            printErrorMessage(in, "A biblioteca está vazia.\nPressione \"Enter\" para continuar.");
            return;
        }

        for (Book book : books) {
            System.out.printf("Livro: %s\n", book.getTitle());
            System.out.printf("Autor: %s\n", book.getAuthorName());
            System.out.printf("Editora: %s\n\n", book.getPublisherName());
        }

        System.out.println("Pressione \"Enter\" para continuar.");
        in.nextLine();
    }

    private void listAuthors(Scanner in) {
        clearScreen();
        ArrayList<Author> authors = library.getAuthors();

        if (authors.isEmpty()) {
            printErrorMessage(in, "Não existem autores cadastrados na biblioteca.\nPressione \"Enter\" para continuar.");
            return;
        }

        for (int i = 0; i < authors.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, authors.get(i).getName());
            System.out.printf("\t Possui %d livros.\n\n", authors.get(i).getBooks().size());
        }

        System.out.println("Pressione \"Enter\" para continuar.");
        in.nextLine();
    }

    private void listPublishers(Scanner in) {
        clearScreen();
        ArrayList<Publisher> publishers = library.getPublishers();

        if (publishers.isEmpty()) {
            printErrorMessage(in, "Não existem editoras cadastrados na biblioteca.\nPressione \"Enter\" para continuar.");
            return;
        }

        for (int i = 0; i < publishers.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, publishers.get(i).getName());
            System.out.printf("\t Possui %d livros publicados.\n\n", publishers.get(i).getBooks().size());
        }

        System.out.println("Pressione \"Enter\" para continuar.");
        in.nextLine();
    }

    private void listReservedBooks(Scanner in) {
        clearScreen();
        Map<Book, String> reservedBooks = library.getReservedBooks();

        if (reservedBooks.isEmpty()) {
            printErrorMessage(in, "Nenhum livro da biblioteca está sendo reservado.\nPressione \"Enter\" para continuar.");
            return; 
        }

        for (Map.Entry<Book, String> entry : reservedBooks.entrySet()) {
            Book book = entry.getKey();
            String student = entry.getValue();

            System.out.printf("Livro: %s\n", book.getTitle());
            System.out.printf("Aluno: %s\n\n", student);
        }

        System.out.println("Pressione \"Enter\" para continuar.");
        in.nextLine();
    }

    private void listBorrowedBooks(Scanner in) {
        clearScreen();
        Map<Book, String> borrowedBooks = library.getBorrowedBooks();

        if (borrowedBooks.isEmpty()) {
            printErrorMessage(in, "Nenhum livro da biblioteca está sendo emprestado.\nPressione \"Enter\" para continuar.");
            return; 
        }

        for (Map.Entry<Book, String> entry : borrowedBooks.entrySet()) {
            Book book = entry.getKey();
            String borrower = entry.getValue();

            System.out.printf("Livro: %s\n", book.getTitle());
            System.out.printf("Comodatário: %s\n\n", borrower);
        }

        System.out.println("Pressione \"Enter\" para continuar.");
        in.nextLine();
    }

    // Delete
    private void removeBook(Scanner in) {
        clearScreen();
        ArrayList<Book> books = library.getLibraryBooks();

        if (books.isEmpty()) {
            printErrorMessage(in, "A biblioteca está vazia.");
            return;
        }

        System.out.println("Qual livro você deseja remover da biblioteca?\n");

        for (int i = 0; i < books.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, books.get(i).getTitle());
        }
        System.out.println();

        int bookNumber = getIntInput(in);

        if (bookNumber > books.size() || bookNumber < 1) {
            printErrorMessage(in, "O livro selecionado é inválido.\nPressione \"Enter\" para continuar.");
            return;
        }

        Book book = books.get(bookNumber - 1);

        try {
            library.deleteBook(book);
        } catch (BookDeletionException e) {
            printErrorMessage(in, "Não é possível remover livros emprestados ou reservados.\nPressione \"Enter\" para continuar.");
        }
    }

    // Creators
    private void createAuthor(Scanner in) {
        clearScreen();
        System.out.println("Qual o nome do autor?");
        System.out.printf("-> ");

        String authorName = in.nextLine();
        // Chamar o método e verificar por erros
        try {
            library.createAuthor(authorName);
        } catch (AuthorAlreadyExistsException e) {
            printErrorMessage(in, "Autor com nome \"" + authorName + "\" já existe. Pressione \"Enter\" para continuar.");
        }
    }

    private void createPublisher(Scanner in) {
        clearScreen();
        System.out.println("Qual o nome da editora?");
        System.out.printf("-> ");

        String publisherName = in.nextLine();
        // Chamar o método e verificar por erros
        try {
            library.createPublisher(publisherName);
        } catch (PublisherAlreadyExistsException e) {
            printErrorMessage(in, "Editora com nome \"" + publisherName + "\" já existe.\nPressione \"Enter\" para continuar.");
        }
    }

    private void createBook(Scanner in) {
        clearScreen();

        ArrayList<Author> authors = library.getAuthors();
        ArrayList<Publisher> publishers = library.getPublishers();
        
        if (authors.isEmpty()) {
            printErrorMessage(in, "É necessário ter autores cadastrados para registrar um livro.\nPressione \"Enter\" para continuar.");
            return;
        }

        if (publishers.isEmpty()) {
            printErrorMessage(in, "É necessário ter editoras cadastradas para registrar um livro.\nPressione \"Enter\" para continuar.");
            return; 
        }

        // Getting author
        System.out.println("Quem é o autor do livro?\n");

        for (int i = 0; i < authors.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, authors.get(i).getName());
        }

        System.out.println();
        int authorNumber = getIntInput(in);

        if (authorNumber > authors.size() || authorNumber < 1) {
            printErrorMessage(in, "O autor selecionado é inválido.\nPressione \"Enter\" para continuar.");
            return;
        }

        // Getting publisher
        clearScreen();
        System.out.println("Qual é a editora do livro?\n");

        for (int i = 0; i < publishers.size(); i++) {
            System.out.printf("[%d] %s\n", i+1, publishers.get(i).getName());
        }

        System.out.println();
        int publisherNumber = getIntInput(in);

        if (publisherNumber > publishers.size() || publisherNumber < 1) {
            printErrorMessage(in, "A editora selecionado é inválida.\nPressione \"Enter\" para continuar.");
            return;
        }

        // Getting book title
        clearScreen();
        System.out.println("Qual o título do livro?");
        System.out.printf("-> ");
        
        String bookTitle = in.nextLine();

        try {
            library.createBook(authors.get(authorNumber - 1), publishers.get(publisherNumber - 1), bookTitle);
        } catch (BookAlreadyExistsException e) {
            printErrorMessage(in, "Livro com título \"" + bookTitle + "\" já existe.\nPressione \"Enter\" para continuar.");
        }
    }

    // Utility functions
    private int getIntInput(Scanner in) {
        while (true) {
            if (in.hasNextInt()) {
                int input = in.nextInt();
                in.nextLine();
                return input;
            } else {
                printErrorMessage(in, "Entrada inválida!\nPressione \"Enter\" para tentar novamente.");
            }
        }
    }

    private void printErrorMessage(Scanner in, String message) {
        System.out.println("\n" + message + "\n");
        in.nextLine();
        clearScreen();
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("powershell", "clear").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\nEste programa não suporta o CMD, por favor use um shell válido.\n");
        }
    }
}
