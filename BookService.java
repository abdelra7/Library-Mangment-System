package com.library.app.service;

import com.library.app.db.BookDAO;
import com.library.app.model.Book;
import com.library.app.util.Logger;

import java.util.ArrayList;
import java.util.List;


public class BookService {
    
    private static final Logger logger = new Logger(BookService.class.getName());
    private final BookDAO bookDAO;
    

    public BookService() {
        this.bookDAO = new BookDAO();
    }
    

    public Book getBookById(int id) throws Exception {
        try {
            return bookDAO.getBookById(id);
        } catch (Exception e) {
            logger.error("Error getting book by ID: " + id, e);
            throw new Exception("Error retrieving book: " + e.getMessage());
        }
    }
    

    public List<Book> getAllBooks() throws Exception {
        try {
            return bookDAO.getAllBooks();
        } catch (Exception e) {
            logger.error("Error getting all books", e);
            throw new Exception("Error retrieving books: " + e.getMessage());
        }
    }
    

    public List<Book> getAvailableBooks() throws Exception {
        try {
            List<Book> allBooks = bookDAO.getAllBooks();
            List<Book> availableBooks = new ArrayList<>();
            
            for (Book book : allBooks) {
                if (book.getAvailableCopies() > 0) {
                    availableBooks.add(book);
                }
            }
            
            return availableBooks;
        } catch (Exception e) {
            logger.error("Error getting available books", e);
            throw new Exception("Error retrieving available books: " + e.getMessage());
        }
    }
    

    public int addBook(Book book) throws Exception {
        try {
            // Validate input
            if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Book title cannot be empty");
            }
            
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                throw new IllegalArgumentException("Book author cannot be empty");
            }
            
            if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
                throw new IllegalArgumentException("Book ISBN cannot be empty");
            }
            
            // Set default values if not provided
            if (book.getStatus() == null || book.getStatus().trim().isEmpty()) {
                book.setStatus("AVAILABLE");
            }
            
            if (book.getTotalCopies() <= 0) {
                book.setTotalCopies(1);
            }
            
            if (book.getAvailableCopies() <= 0) {
                book.setAvailableCopies(book.getTotalCopies());
            }
            
            return bookDAO.addBook(book);
        } catch (Exception e) {
            logger.error("Error adding book: " + book.getTitle(), e);
            throw new Exception("Error adding book: " + e.getMessage());
        }
    }
    

    public void updateBook(Book book) throws Exception {
        try {
            // Validate input
            if (book.getId() <= 0) {
                throw new IllegalArgumentException("Invalid book ID");
            }
            
            if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Book title cannot be empty");
            }
            
            if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
                throw new IllegalArgumentException("Book author cannot be empty");
            }
            
            if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
                throw new IllegalArgumentException("Book ISBN cannot be empty");
            }
            
            // Ensure available copies doesn't exceed total copies
            if (book.getAvailableCopies() > book.getTotalCopies()) {
                book.setAvailableCopies(book.getTotalCopies());
            }
            
            // Update status based on availability
            book.updateStatus();
            
            bookDAO.updateBook(book);
        } catch (Exception e) {
            logger.error("Error updating book: " + book.getTitle(), e);
            throw new Exception("Error updating book: " + e.getMessage());
        }
    }


    public void deleteBook(int id) throws Exception {
        try {
            // Check if the book exists
            Book book = bookDAO.getBookById(id);
            if (book == null) {
                throw new IllegalArgumentException("Book does not exist");
            }
            
            // Check if the book has active borrowings
            if (book.getAvailableCopies() < book.getTotalCopies()) {
                throw new IllegalStateException("Cannot delete book with active borrowings");
            }
            
            bookDAO.deleteBook(id);
        } catch (Exception e) {
            logger.error("Error deleting book with ID: " + id, e);
            throw new Exception("Error deleting book: " + e.getMessage());
        }
    }
    

    public List<Book> searchBooksByTitle(String title) throws Exception {
        try {
            return bookDAO.searchBooksByTitle(title);
        } catch (Exception e) {
            logger.error("Error searching books by title: " + title, e);
            throw new Exception("Error searching books: " + e.getMessage());
        }
    }
    

    public List<Book> searchBooksByAuthor(String author) throws Exception {
        try {
            return bookDAO.searchBooksByAuthor(author);
        } catch (Exception e) {
            logger.error("Error searching books by author: " + author, e);
            throw new Exception("Error searching books: " + e.getMessage());
        }
    }
    

    public List<Book> searchBooksByIsbn(String isbn) throws Exception {
        try {
            return bookDAO.searchBooksByIsbn(isbn);
        } catch (Exception e) {
            logger.error("Error searching books by ISBN: " + isbn, e);
            throw new Exception("Error searching books: " + e.getMessage());
        }
    }
    

    public List<Book> searchBooksByGenre(String genre) throws Exception {
        try {
            return bookDAO.searchBooksByGenre(genre);
        } catch (Exception e) {
            logger.error("Error searching books by genre: " + genre, e);
            throw new Exception("Error searching books: " + e.getMessage());
        }
    }
    

    public List<Book> searchBooksByPublisher(String publisher) throws Exception {
        try {
            return bookDAO.searchBooksByPublisher(publisher);
        } catch (Exception e) {
            logger.error("Error searching books by publisher: " + publisher, e);
            throw new Exception("Error searching books: " + e.getMessage());
        }
    }
    

    public List<Book> searchAvailableBooks(String searchText) throws Exception {
        try {
            List<Book> results = new ArrayList<>();
            
            // Search in different fields
            results.addAll(bookDAO.searchBooksByTitle(searchText));
            results.addAll(bookDAO.searchBooksByAuthor(searchText));
            results.addAll(bookDAO.searchBooksByIsbn(searchText));
            results.addAll(bookDAO.searchBooksByGenre(searchText));
            results.addAll(bookDAO.searchBooksByPublisher(searchText));
            
            // Remove duplicates and keep only available books
            List<Book> availableResults = new ArrayList<>();
            List<Integer> addedIds = new ArrayList<>();
            
            for (Book book : results) {
                if (!addedIds.contains(book.getId()) && book.getAvailableCopies() > 0) {
                    availableResults.add(book);
                    addedIds.add(book.getId());
                }
            }
            
            return availableResults;
        } catch (Exception e) {
            logger.error("Error searching available books: " + searchText, e);
            throw new Exception("Error searching books: " + e.getMessage());
        }
    }
    

    public List<Book> getPopularBooks(int limit) throws Exception {
        try {
            return bookDAO.getPopularBooks(limit);
        } catch (Exception e) {
            logger.error("Error getting popular books", e);
            throw new Exception("Error retrieving popular books: " + e.getMessage());
        }
    }
    

    public List<Book> getRecentBooks(int limit) throws Exception {
        try {
            return bookDAO.getRecentBooks(limit);
        } catch (Exception e) {
            logger.error("Error getting recent books", e);
            throw new Exception("Error retrieving recent books: " + e.getMessage());
        }
    }
}
