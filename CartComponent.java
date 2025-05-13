package com.library.app.cart;

import com.library.app.model.Book;

import java.util.List;


public interface CartComponent {
    

    String getName();
    

    int getItemCount();
    

    void getItemsFlat(List<Book> books);
    

    void print(int indent);
}
