package com.library.app.cart;

import com.library.app.model.Book;

import java.util.ArrayList;
import java.util.List;


public class CartComposite implements CartComponent {
    
    private String name;
    private List<CartComponent> children;
    

    public CartComposite(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }
    

    public void add(CartComponent component) {
        children.add(component);
    }
    

    public boolean remove(CartComponent component) {
        return children.remove(component);
    }
    

    public List<CartComponent> getChildren() {
        return children;
    }
    

    public void clear() {
        children.clear();
    }
    
    @Override
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int getItemCount() {
        int count = 0;
        for (CartComponent component : children) {
            count += component.getItemCount();
        }
        return count;
    }
    
    @Override
    public void getItemsFlat(List<Book> books) {
        for (CartComponent component : children) {
            component.getItemsFlat(books);
        }
    }
    
    @Override
    public void print(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        sb.append("+ ").append(name).append(" (").append(getItemCount()).append(" items)");
        System.out.println(sb.toString());
        
        for (CartComponent component : children) {
            component.print(indent + 1);
        }
    }
    
    @Override
    public String toString() {
        return name + " (" + getItemCount() + " items)";
    }
    

    public CartComponent findByName(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        
        for (CartComponent component : children) {
            if (component instanceof CartComposite) {
                CartComponent found = ((CartComposite) component).findByName(name);
                if (found != null) {
                    return found;
                }
            } else if (component.getName().equals(name)) {
                return component;
            }
        }
        
        return null;
    }
    

    public CartItem findByBookId(int bookId) {
        for (CartComponent component : children) {
            if (component instanceof CartItem) {
                CartItem item = (CartItem) component;
                if (item.getBook().getId() == bookId) {
                    return item;
                }
            } else if (component instanceof CartComposite) {
                CartItem found = ((CartComposite) component).findByBookId(bookId);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }
}
