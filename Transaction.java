package com.library.app.model;

import java.util.Date;


public class Transaction {
    private int id;
    private int bookId;
    private int memberId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private String status; // BORROWED, RETURNED, OVERDUE, LOST, RENEWED
    private double fine;
    private String remarks;
    
    // Denormalized fields for display
    private String bookTitle;
    private String memberName;
    

    public Transaction() {
        this.borrowDate = new Date();
        this.status = "BORROWED";
        this.fine = 0.0;
    }
    

    public Transaction(int bookId, int memberId, Date borrowDate, Date dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "BORROWED";
        this.fine = 0.0;
    }
    

    public Transaction(int id, int bookId, int memberId, Date borrowDate, Date dueDate, 
                      Date returnDate, String status, double fine, String remarks, 
                      String bookTitle, String memberName) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
        this.remarks = remarks;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
    }

    // Getters and Setters
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", bookId=" + bookId +
               ", memberId=" + memberId +
               ", borrowDate=" + borrowDate +
               ", dueDate=" + dueDate +
               ", status='" + status + '\'' +
               '}';
    }


    public boolean isOverdue() {
        if ("RETURNED".equals(status)) {
            return false;
        }
        Date currentDate = new Date();
        return currentDate.after(dueDate);
    }
    

    

    public boolean renew(int renewalDays) {
        if ("BORROWED".equals(status) && !isOverdue()) {
            long dueTime = dueDate.getTime();
            dueDate = new Date(dueTime + (renewalDays * 24 * 60 * 60 * 1000));
            remarks = (remarks != null ? remarks + "; " : "") + "Renewed for " + renewalDays + " days";
            return true;
        }
        return false;
    }
}
