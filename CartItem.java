package com.library.app.cart;

import com.library.app.model.Book;

import java.util.List;


public class CartItem implements CartComponent {
    
    private Book book;
    

    public CartItem(Book book) {
        this.book = book;
    }
    

    public Book getBook() {
        return book;
    }
    

    public void setBook(Book book) {
        this.book = book;
    }
    
    @Override
    public String getName() {
        return book.getTitle();
    }
    
    @Override
    public int getItemCount() {
        return 1; // Each item counts as 1
    }
    
    @Override
    public void getItemsFlat(List<Book> books) {
        books.add(book);
    }
    
    @Override
    public void print(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        sb.append("- ").append(book.getTitle()).append(" (").append(book.getAuthor()).append(")");
        System.out.println(sb.toString());
    }
    
    @Override
    public String toString() {
        return book.getTitle();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CartItem other = (CartItem) obj;
        if (book == null) {
            return other.book == null;
        }
        
        return book.getId() == other.book.getId();
    }
    
    @Override
    public int hashCode() {
        return book != null ? book.getId() : 0;
    }
}
