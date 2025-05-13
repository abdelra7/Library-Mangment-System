package com.library.app.service;

import com.library.app.db.TransactionDAO;
import com.library.app.model.Transaction;
import com.library.app.util.DateUtil;
import com.library.app.util.Logger;

import java.util.Date;
import java.util.List;


public class TransactionService {
    
    private static final Logger logger = new Logger(TransactionService.class.getName());
    private final TransactionDAO transactionDAO;
    

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }
    

    public Transaction getTransactionById(int id) throws Exception {
        try {
            return transactionDAO.getTransactionById(id);
        } catch (Exception e) {
            logger.error("Error getting transaction by ID: " + id, e);
            throw new Exception("Error retrieving transaction: " + e.getMessage());
        }
    }
    

    public List<Transaction> getAllTransactions() throws Exception {
        try {
            return transactionDAO.getAllTransactions();
        } catch (Exception e) {
            logger.error("Error getting all transactions", e);
            throw new Exception("Error retrieving transactions: " + e.getMessage());
        }
    }
    

    public List<Transaction> getActiveBorrowingsByMember(int memberId) throws Exception {
        try {
            return transactionDAO.getActiveBorrowingsByMember(memberId);
        } catch (Exception e) {
            logger.error("Error getting active borrowings for member ID: " + memberId, e);
            throw new Exception("Error retrieving borrowings: " + e.getMessage());
        }
    }
    

    public List<Transaction> getTransactionsByMember(int memberId) throws Exception {
        try {
            return transactionDAO.getTransactionsByMember(memberId);
        } catch (Exception e) {
            logger.error("Error getting transactions for member ID: " + memberId, e);
            throw new Exception("Error retrieving transactions: " + e.getMessage());
        }
    }
    

    public List<Transaction> getTransactionsByBook(int bookId) throws Exception {
        try {
            return transactionDAO.getTransactionsByBook(bookId);
        } catch (Exception e) {
            logger.error("Error getting transactions for book ID: " + bookId, e);
            throw new Exception("Error retrieving transactions: " + e.getMessage());
        }
    }
    

    public List<Transaction> getOverdueTransactions() throws Exception {
        try {
            return transactionDAO.getOverdueTransactions();
        } catch (Exception e) {
            logger.error("Error getting overdue transactions", e);
            throw new Exception("Error retrieving overdue transactions: " + e.getMessage());
        }
    }
    

    public int addTransaction(Transaction transaction) throws Exception {
        try {
            // Validate input
            if (transaction.getBookId() <= 0) {
                throw new IllegalArgumentException("Invalid book ID");
            }
            
            if (transaction.getMemberId() <= 0) {
                throw new IllegalArgumentException("Invalid member ID");
            }
            
            // Set default values if not provided
            if (transaction.getBorrowDate() == null) {
                transaction.setBorrowDate(new Date());
            }
            
            if (transaction.getDueDate() == null) {
                // Default due date: 14 days from borrow date
                transaction.setDueDate(DateUtil.calculateDueDate(transaction.getBorrowDate(), 14));
            }
            
            if (transaction.getStatus() == null || transaction.getStatus().trim().isEmpty()) {
                transaction.setStatus("BORROWED");
            }
            
            return transactionDAO.addTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error adding transaction for book ID: " + transaction.getBookId() + 
                         ", member ID: " + transaction.getMemberId(), e);
            throw new Exception("Error adding transaction: " + e.getMessage());
        }
    }
    

    public void updateTransaction(Transaction transaction) throws Exception {
        try {
            // Validate input
            if (transaction.getId() <= 0) {
                throw new IllegalArgumentException("Invalid transaction ID");
            }
            
            if (transaction.getBookId() <= 0) {
                throw new IllegalArgumentException("Invalid book ID");
            }
            
            if (transaction.getMemberId() <= 0) {
                throw new IllegalArgumentException("Invalid member ID");
            }
            
            transactionDAO.updateTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error updating transaction ID: " + transaction.getId(), e);
            throw new Exception("Error updating transaction: " + e.getMessage());
        }
    }
    

    public void deleteTransaction(int id) throws Exception {
        try {
            // Check if the transaction exists
            Transaction transaction = transactionDAO.getTransactionById(id);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction does not exist");
            }
            
            transactionDAO.deleteTransaction(id);
        } catch (Exception e) {
            logger.error("Error deleting transaction with ID: " + id, e);
            throw new Exception("Error deleting transaction: " + e.getMessage());
        }
    }
    

    public void returnBook(int transactionId, Date returnDate) throws Exception {
        try {
            Transaction transaction = transactionDAO.getTransactionById(transactionId);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction does not exist");
            }
            
            if ("RETURNED".equals(transaction.getStatus())) {
                throw new IllegalStateException("Book has already been returned");
            }
            
            transaction.setReturnDate(returnDate);
            transaction.setStatus("RETURNED");
            
            transactionDAO.updateTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error returning book for transaction ID: " + transactionId, e);
            throw new Exception("Error returning book: " + e.getMessage());
        }
    }
    

    public void renewBook(int transactionId, int renewalDays) throws Exception {
        try {
            Transaction transaction = transactionDAO.getTransactionById(transactionId);
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction does not exist");
            }
            
            if (!"BORROWED".equals(transaction.getStatus())) {
                throw new IllegalStateException("Only borrowed books can be renewed");
            }
            
            if (transaction.isOverdue()) {
                throw new IllegalStateException("Overdue books cannot be renewed");
            }
            
            // Calculate new due date
            Date newDueDate = DateUtil.calculateDueDate(transaction.getDueDate(), renewalDays);
            transaction.setDueDate(newDueDate);
            
            // Add renewal remark
            String remark = "Renewed for " + renewalDays + " days on " + DateUtil.formatDate(new Date());
            if (transaction.getRemarks() != null && !transaction.getRemarks().isEmpty()) {
                transaction.setRemarks(transaction.getRemarks() + "; " + remark);
            } else {
                transaction.setRemarks(remark);
            }
            
            transactionDAO.updateTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error renewing book for transaction ID: " + transactionId, e);
            throw new Exception("Error renewing book: " + e.getMessage());
        }
    }
    

    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws Exception {
        try {
            return transactionDAO.getTransactionsByDateRange(startDate, endDate);
        } catch (Exception e) {
            logger.error("Error getting transactions by date range", e);
            throw new Exception("Error retrieving transactions: " + e.getMessage());
        }
    }
    

}
