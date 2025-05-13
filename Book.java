package com.library.app.model;

import java.util.Objects;


public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String genre;
    private String description;
    private String status; // AVAILABLE, BORROWED, RESERVED, DAMAGED, LOST
    private String location; // Shelf or section information
    private int totalCopies;
    private int availableCopies;
    private String coverImage; // Path or URL to cover image
    private String language;
    private int pageCount;
    private double price;
    

    public Book() {
        this.status = "AVAILABLE";
        this.totalCopies = 1;
        this.availableCopies = 1;
    }
    

    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = "AVAILABLE";
        this.totalCopies = 1;
        this.availableCopies = 1;
    }
    

    public Book(int id, String isbn, String title, String author, String publisher, 
               int publicationYear, String genre, String description, String status, 
               String location, int totalCopies, int availableCopies, String coverImage, 
               String language, int pageCount, double price) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.description = description;
        this.status = status;
        this.location = location;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.coverImage = coverImage;
        this.language = language;
        this.pageCount = pageCount;
        this.price = price;
    }

    // Getters and Setters
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id ||
               (isbn != null && isbn.equals(book.isbn));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
               "id=" + id +
               ", isbn='" + isbn + '\'' +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", status='" + status + '\'' +
               ", availableCopies=" + availableCopies +
               '}';
    }


    public Book copy() {
        Book copy = new Book();
        copy.id = this.id;
        copy.isbn = this.isbn;
        copy.title = this.title;
        copy.author = this.author;
        copy.publisher = this.publisher;
        copy.publicationYear = this.publicationYear;
        copy.genre = this.genre;
        copy.description = this.description;
        copy.status = this.status;
        copy.location = this.location;
        copy.totalCopies = this.totalCopies;
        copy.availableCopies = this.availableCopies;
        copy.coverImage = this.coverImage;
        copy.language = this.language;
        copy.pageCount = this.pageCount;
        copy.price = this.price;
        return copy;
    }


    public void updateStatus() {
        if (availableCopies <= 0) {
            status = "BORROWED";
        } else if (availableCopies < totalCopies) {
            status = "PARTIALLY_AVAILABLE";
        } else {
            status = "AVAILABLE";
        }
    }
    

    public boolean isAvailable() {
        return "AVAILABLE".equals(status) || "PARTIALLY_AVAILABLE".equals(status);
    }
    

    public String getCategory() {
        return genre;
    }
    

    public int getPublishYear() {
        return publicationYear;
    }
}
