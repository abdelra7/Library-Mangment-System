package com.library.app.db;

import com.library.app.model.Book;
import com.library.app.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class BookDAO {
    
    private static final Logger logger = new Logger(BookDAO.class.getName());
    

    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Book book = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                book = mapResultSetToBook(rs);
            }
            
            logger.info("Retrieved book by ID: " + id);
            return book;
        } catch (SQLException e) {
            logger.error("Error retrieving book by ID: " + id, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Book> getAllBooks() throws SQLException {
        String sql = "SELECT * FROM books ORDER BY title";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
            
            logger.info("Retrieved all books: " + books.size() + " records");
            return books;
        } catch (SQLException e) {
            logger.error("Error retrieving all books", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public int addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, publisher, publication_year, " +
                     "genre, description, status, location, total_copies, available_copies, " +
                     "cover_image, language, page_count, price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublicationYear());
            stmt.setString(6, book.getGenre());
            stmt.setString(7, book.getDescription());
            stmt.setString(8, book.getStatus());
            stmt.setString(9, book.getLocation());
            stmt.setInt(10, book.getTotalCopies());
            stmt.setInt(11, book.getAvailableCopies());
            stmt.setString(12, book.getCoverImage());
            stmt.setString(13, book.getLanguage());
            stmt.setInt(14, book.getPageCount());
            stmt.setDouble(15, book.getPrice());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                book.setId(id);
                logger.info("Added new book: " + book.getTitle() + " with ID: " + id);
                return id;
            } else {
                throw new SQLException("Creating book failed, no ID obtained.");
            }
        } catch (SQLException e) {
            logger.error("Error adding book: " + book.getTitle(), e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }

    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, " +
                     "publication_year = ?, genre = ?, description = ?, status = ?, " +
                     "location = ?, total_copies = ?, available_copies = ?, cover_image = ?, " +
                     "language = ?, page_count = ?, price = ? " +
                     "WHERE id = ?";
                     
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublicationYear());
            stmt.setString(6, book.getGenre());
            stmt.setString(7, book.getDescription());
            stmt.setString(8, book.getStatus());
            stmt.setString(9, book.getLocation());
            stmt.setInt(10, book.getTotalCopies());
            stmt.setInt(11, book.getAvailableCopies());
            stmt.setString(12, book.getCoverImage());
            stmt.setString(13, book.getLanguage());
            stmt.setInt(14, book.getPageCount());
            stmt.setDouble(15, book.getPrice());
            stmt.setInt(16, book.getId());
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Updated book ID: " + book.getId() + ", rows affected: " + affectedRows);
        } catch (SQLException e) {
            logger.error("Error updating book ID: " + book.getId(), e);
            throw e;
        } finally {
            closeResources(null, stmt);
        }
    }
    

    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted book ID: " + id + ", rows affected: " + affectedRows);
        } catch (SQLException e) {
            logger.error("Error deleting book ID: " + id, e);
            throw e;
        } finally {
            closeResources(null, stmt);
        }
    }
    

    public List<Book> searchBooksByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) ORDER BY title";
        return searchBooks(sql, "%" + title + "%");
    }
    

    public List<Book> searchBooksByAuthor(String author) throws SQLException {
        String sql = "SELECT * FROM books WHERE LOWER(author) LIKE LOWER(?) ORDER BY author, title";
        return searchBooks(sql, "%" + author + "%");
    }
    

    public List<Book> searchBooksByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE LOWER(isbn) LIKE LOWER(?) ORDER BY title";
        return searchBooks(sql, "%" + isbn + "%");
    }
    

    public List<Book> searchBooksByGenre(String genre) throws SQLException {
        String sql = "SELECT * FROM books WHERE LOWER(genre) LIKE LOWER(?) ORDER BY title";
        return searchBooks(sql, "%" + genre + "%");
    }
    

    public List<Book> searchBooksByPublisher(String publisher) throws SQLException {
        String sql = "SELECT * FROM books WHERE LOWER(publisher) LIKE LOWER(?) ORDER BY title";
        return searchBooks(sql, "%" + publisher + "%");
    }
    

    public List<Book> getPopularBooks(int limit) throws SQLException {
        String sql = "SELECT b.*, COUNT(t.id) as borrow_count " +
                     "FROM books b " +
                     "LEFT JOIN transactions t ON b.id = t.book_id " +
                     "GROUP BY b.id " +
                     "ORDER BY borrow_count DESC " +
                     "LIMIT ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
            
            logger.info("Retrieved popular books: " + books.size() + " records");
            return books;
        } catch (SQLException e) {
            logger.error("Error retrieving popular books", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Book> getRecentBooks(int limit) throws SQLException {
        String sql = "SELECT * FROM books ORDER BY id DESC LIMIT ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
            
            logger.info("Retrieved recent books: " + books.size() + " records");
            return books;
        } catch (SQLException e) {
            logger.error("Error retrieving recent books", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    private List<Book> searchBooks(String sql, String searchParam) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, searchParam);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
            
            logger.info("Search results: " + books.size() + " books found");
            return books;
        } catch (SQLException e) {
            logger.error("Error searching books with param: " + searchParam, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setGenre(rs.getString("genre"));
        book.setDescription(rs.getString("description"));
        book.setStatus(rs.getString("status"));
        book.setLocation(rs.getString("location"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setCoverImage(rs.getString("cover_image"));
        book.setLanguage(rs.getString("language"));
        book.setPageCount(rs.getInt("page_count"));
        book.setPrice(rs.getDouble("price"));
        return book;
    }
    

    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            logger.error("Error closing database resources", e);
        }
    }
}
