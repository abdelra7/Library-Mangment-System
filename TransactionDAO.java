package com.library.app.db;

import com.library.app.model.Transaction;
import com.library.app.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TransactionDAO {
    
    private static final Logger logger = new Logger(TransactionDAO.class.getName());
    

    public Transaction getTransactionById(int id) throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "WHERE t.id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Transaction transaction = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                transaction = mapResultSetToTransaction(rs);
            }
            
            logger.info("Retrieved transaction by ID: " + id);
            return transaction;
        } catch (SQLException e) {
            logger.error("Error retrieving transaction by ID: " + id, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "ORDER BY t.borrow_date DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
            
            logger.info("Retrieved all transactions: " + transactions.size() + " records");
            return transactions;
        } catch (SQLException e) {
            logger.error("Error retrieving all transactions", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Transaction> getActiveBorrowingsByMember(int memberId) throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "WHERE t.member_id = ? AND t.status IN ('BORROWED', 'OVERDUE') " +
                     "ORDER BY t.due_date";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
            
            logger.info("Retrieved active borrowings for member ID " + memberId + 
                       ": " + transactions.size() + " records");
            return transactions;
        } catch (SQLException e) {
            logger.error("Error retrieving active borrowings for member ID: " + memberId, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Transaction> getTransactionsByMember(int memberId) throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "WHERE t.member_id = ? " +
                     "ORDER BY t.borrow_date DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
            
            logger.info("Retrieved transactions for member ID " + memberId + 
                       ": " + transactions.size() + " records");
            return transactions;
        } catch (SQLException e) {
            logger.error("Error retrieving transactions for member ID: " + memberId, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Transaction> getTransactionsByBook(int bookId) throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "WHERE t.book_id = ? " +
                     "ORDER BY t.borrow_date DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
            
            logger.info("Retrieved transactions for book ID " + bookId + 
                       ": " + transactions.size() + " records");
            return transactions;
        } catch (SQLException e) {
            logger.error("Error retrieving transactions for book ID: " + bookId, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Transaction> getOverdueTransactions() throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "WHERE t.status = 'BORROWED' AND t.due_date < CURRENT_TIMESTAMP " +
                     "ORDER BY t.due_date";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transaction.setStatus("OVERDUE"); // Mark as overdue for display purposes
                transactions.add(transaction);
            }
            
            logger.info("Retrieved overdue transactions: " + transactions.size() + " records");
            return transactions;
        } catch (SQLException e) {
            logger.error("Error retrieving overdue transactions", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public int addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (book_id, member_id, borrow_date, due_date, " +
                     "return_date, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
                     
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, transaction.getBookId());
            stmt.setInt(2, transaction.getMemberId());
            stmt.setTimestamp(3, transaction.getBorrowDate() != null ? 
                              new Timestamp(transaction.getBorrowDate().getTime()) : null);
            stmt.setTimestamp(4, transaction.getDueDate() != null ? 
                              new Timestamp(transaction.getDueDate().getTime()) : null);
            stmt.setTimestamp(5, transaction.getReturnDate() != null ? 
                              new Timestamp(transaction.getReturnDate().getTime()) : null);
            stmt.setString(6, transaction.getStatus());
            stmt.setString(7, transaction.getRemarks());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                transaction.setId(id);
                logger.info("Added new transaction with ID: " + id);
                return id;
            } else {
                throw new SQLException("Creating transaction failed, no ID obtained.");
            }
        } catch (SQLException e) {
            logger.error("Error adding transaction", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public void updateTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET book_id = ?, member_id = ?, borrow_date = ?, " +
                     "due_date = ?, return_date = ?, status = ?, notes = ? " +
                     "WHERE id = ?";
                     
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, transaction.getBookId());
            stmt.setInt(2, transaction.getMemberId());
            stmt.setTimestamp(3, transaction.getBorrowDate() != null ? 
                              new Timestamp(transaction.getBorrowDate().getTime()) : null);
            stmt.setTimestamp(4, transaction.getDueDate() != null ? 
                              new Timestamp(transaction.getDueDate().getTime()) : null);
            stmt.setTimestamp(5, transaction.getReturnDate() != null ? 
                              new Timestamp(transaction.getReturnDate().getTime()) : null);
            stmt.setString(6, transaction.getStatus());
            stmt.setString(7, transaction.getRemarks());
            stmt.setInt(8, transaction.getId());
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Updated transaction ID: " + transaction.getId() + 
                       ", rows affected: " + affectedRows);
        } catch (SQLException e) {
            logger.error("Error updating transaction ID: " + transaction.getId(), e);
            throw e;
        } finally {
            closeResources(null, stmt);
        }
    }
    

    public void deleteTransaction(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted transaction ID: " + id + ", rows affected: " + affectedRows);
        } catch (SQLException e) {
            logger.error("Error deleting transaction ID: " + id, e);
            throw e;
        } finally {
            closeResources(null, stmt);
        }
    }
    

    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) 
            throws SQLException {
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.id " +
                     "JOIN members m ON t.member_id = m.id " +
                     "WHERE (t.borrow_date BETWEEN ? AND ?) OR " +
                     "(t.return_date BETWEEN ? AND ?) " +
                     "ORDER BY t.borrow_date DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            stmt.setTimestamp(3, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(4, new Timestamp(endDate.getTime()));
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = mapResultSetToTransaction(rs);
                transactions.add(transaction);
            }
            
            logger.info("Retrieved transactions by date range: " + transactions.size() + " records");
            return transactions;
        } catch (SQLException e) {
            logger.error("Error retrieving transactions by date range", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setBookId(rs.getInt("book_id"));
        transaction.setMemberId(rs.getInt("member_id"));
        
        Timestamp borrowDate = rs.getTimestamp("borrow_date");
        if (borrowDate != null) {
            transaction.setBorrowDate(new Date(borrowDate.getTime()));
        }
        
        Timestamp dueDate = rs.getTimestamp("due_date");
        if (dueDate != null) {
            transaction.setDueDate(new Date(dueDate.getTime()));
        }
        
        Timestamp returnDate = rs.getTimestamp("return_date");
        if (returnDate != null) {
            transaction.setReturnDate(new Date(returnDate.getTime()));
        }
        
        transaction.setStatus(rs.getString("status"));
        transaction.setRemarks(rs.getString("notes"));
        transaction.setBookTitle(rs.getString("book_title"));
        transaction.setMemberName(rs.getString("member_name"));
        
        return transaction;
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
