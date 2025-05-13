package com.library.app.ui;

import com.library.app.factory.DialogFactory;
import com.library.app.factory.UIComponentFactory;
import com.library.app.model.Book;
import com.library.app.service.BookService;
import com.library.app.util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for managing library books.
 */
public class BookPanel extends JPanel {

    private static final Logger logger = new Logger(BookPanel.class.getName());
    
    private final MainWindow mainWindow;
    private final BookService bookService;
    private final UIComponentFactory uiFactory;
    private final DialogFactory dialogFactory;
    
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    
    /**
     * Constructs a new BookPanel.
     * 
     * @param mainWindow The main application window
     */
    public BookPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.bookService = new BookService();
        this.uiFactory = new UIComponentFactory();
        this.dialogFactory = new DialogFactory();
        
        initializeUI();
        refreshData();
        logger.info("BookPanel initialized");
    }
    
    /**
     * Creates a styled action button with icon and color.
     * 
     * @param text Button text
     * @param iconName Icon name
     * @param color Button background color
     * @param actionListener Action listener
     * @return Styled button
     */
    private JButton createActionButton(String text, String iconName, Color color, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setIcon(uiFactory.createIcon(iconName));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
//        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        button.setIconTextGap(10);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addActionListener(actionListener);
        return button;
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245)); // Light gray background
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Library Books Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setIcon(uiFactory.createIcon("book"));
        titleLabel.setIconTextGap(10);
        titleLabel.setHorizontalTextPosition(JLabel.RIGHT);
        
//        JLabel statsLabel = new JLabel("Total Books: 0");
//        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
//        statsLabel.setForeground(new Color(100, 100, 100));
//
//        titlePanel.add(titleLabel, BorderLayout.NORTH);
//        titlePanel.add(statsLabel, BorderLayout.SOUTH);
        
        // Create top panel with controls
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Create search panel with modern look
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("search: ");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Create search field with placeholder text and rounded border
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        // Search type combo with better styling
        searchTypeCombo = new JComboBox<>(new String[]{"address", "author", " ISBN", "genre", "Publisher"});
        searchTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        searchTypeCombo.setBackground(Color.WHITE);
        searchTypeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Modern search button
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(25, 118, 210));
//        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        searchButton.setIcon(uiFactory.createIcon("search"));
        searchButton.setIconTextGap(8);
        searchButton.addActionListener(this::searchBooks);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Create button panel with modern styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        // Modern buttons with consistent styling
        JButton addButton = createActionButton("Add Book", "", new Color(76, 175, 80), this::addBook);
        JButton editButton = createActionButton("Edit", "", new Color(255, 152, 0), this::editBook);
//        JButton deleteButton = createActionButton("Delete", "", new Color(244, 67, 54), this::deleteBook);
        JButton addToCartButton = createActionButton("Add to Cart", "", new Color(121, 85, 172), this::addToCart);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
//        buttonPanel.add(deleteButton);
        buttonPanel.add(addToCartButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add panels to main layout
        JPanel mainTopPanel = new JPanel(new BorderLayout());
        mainTopPanel.setOpaque(false);
        mainTopPanel.add(titlePanel, BorderLayout.NORTH);
        mainTopPanel.add(topPanel, BorderLayout.CENTER);
        
        add(mainTopPanel, BorderLayout.NORTH);
        
        // Create table for books with English column names
        String[] columnNames = {"ID", "ISBN", "Title", "Author", "Publisher", "Year",
                               "Genre", "Status", "Location", "Total Copies", "Available Copies"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table with modern styling
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.setRowHeight(30); // Taller rows for better readability
        bookTable.setIntercellSpacing(new Dimension(10, 5)); // More spacing between cells
        bookTable.setShowGrid(false); // Hide grid lines for modern look
        bookTable.setFillsViewportHeight(true);
        
        // Set table header style
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookTable.getTableHeader().setBackground(new Color(25, 118, 210));
//        bookTable.getTableHeader().setForeground(Color.WHITE);
        bookTable.getTableHeader().setPreferredSize(new Dimension(0, 35)); // Make header taller
        
        // Set alternating row colors
        bookTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set alternating row background colors
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                
                // Center-align numeric columns
                if (column == 0 || column == 5 || column == 9 || column == 10) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                
                // Set border to create space between rows
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return c;
            }
        });
        
        // Set column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(100); // ISBN
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Publisher
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Publication Year
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Genre
        bookTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
        bookTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Location
        bookTable.getColumnModel().getColumn(9).setPreferredWidth(80);  // Total Copies
        bookTable.getColumnModel().getColumn(10).setPreferredWidth(80); // Available Copies
        
        // Create scroll pane with styled border
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Create a container panel for the table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        tablePanel.setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status panel at the bottom with better styling
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        statusPanel.setBackground(new Color(250, 250, 250));
        
//        JLabel totalBooksLabel = new JLabel("Total Books: "  );
//        totalBooksLabel.setFont(new Font("Arial", Font.BOLD, 14));
//        totalBooksLabel.setForeground(new Color(70, 70, 70));
//        statusPanel.add(totalBooksLabel, BorderLayout.WEST);
        
        // Add both panels to a container
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Refreshes the book table with the latest data from the database.
     */
    public void refreshData() {
        try {
            tableModel.setRowCount(0); // Clear existing data

            List<Book> books = bookService.getAllBooks();
            for (Book book : books) {
                Object[] rowData = {
                        book.getId(),
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getPublicationYear(),
                        book.getGenre(),
                        book.getStatus(),
                        book.getLocation(),
                        book.getTotalCopies(),
                        book.getAvailableCopies()
                };
                tableModel.addRow(rowData);
            }

            // ✅ تجنب ArrayIndexOutOfBoundsException
            if (getComponentCount() > 2 && getComponent(2) instanceof JPanel statusPanel) {
                JLabel totalBooksLabel = (JLabel) ((BorderLayout) statusPanel.getLayout())
                        .getLayoutComponent(BorderLayout.WEST);
                if (totalBooksLabel != null) {
                    totalBooksLabel.setText("Total Books: " + books.size());
                }
            }

            logger.info("Book data refreshed, " + books.size() + " books loaded");
        } catch (Exception e) {
            logger.error("Error refreshing book data", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading books: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Handles search button action.
     */
    private void searchBooks(ActionEvent e) {
        try {
            String searchText = searchField.getText().trim();
            String searchType = (String) searchTypeCombo.getSelectedItem();
            
            if (searchText.isEmpty()) {
                refreshData();
                return;
            }
            
            List<Book> results;
            switch (searchType) {
                case "Title":
                    results = bookService.searchBooksByTitle(searchText);
                    break;
                case "Author":
                    results = bookService.searchBooksByAuthor(searchText);
                    break;
                case "ISBN":
                    results = bookService.searchBooksByIsbn(searchText);
                    break;
                case "Genre":
                    results = bookService.searchBooksByGenre(searchText);
                    break;
                case "Publisher":
                    results = bookService.searchBooksByPublisher(searchText);
                    break;
                default:
                    results = bookService.searchBooksByTitle(searchText);
            }
            
            tableModel.setRowCount(0);
            for (Book book : results) {
                Object[] rowData = {
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublicationYear(),
                    book.getGenre(),
                    book.getStatus(),
                    book.getLocation(),
                    book.getTotalCopies(),
                    book.getAvailableCopies()
                };
                tableModel.addRow(rowData);
            }
            
            // Update status
            JPanel statusPanel = (JPanel) getComponent(2);
            JLabel totalBooksLabel = (JLabel) ((BorderLayout) statusPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            totalBooksLabel.setText("Search Results: " + results.size() + " books found");
            
            logger.info("Book search completed, " + results.size() + " results for " + searchType + ": " + searchText);
        } catch (Exception ex) {
            logger.error("Error searching books", ex);
            JOptionPane.showMessageDialog(this, 
                "Error searching books: " + ex.getMessage(), 
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles add book button action.
     */
    private void addBook(ActionEvent e) {
        try {
            Book newBook = dialogFactory.showAddBookDialog(this);
            if (newBook != null) {
                bookService.addBook(newBook);
                refreshData();
                logger.info("New book added: " + newBook.getTitle());
                JOptionPane.showMessageDialog(this, 
                    "Book added successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.error("Error adding book", ex);
            JOptionPane.showMessageDialog(this, 
                "Error adding book: " + ex.getMessage(), 
                "Add Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles edit book button action.
     */
    private void editBook(ActionEvent e) {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a book to edit", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            Book selectedBook = bookService.getBookById(bookId);
            
            Book updatedBook = dialogFactory.showEditBookDialog(this, selectedBook);
            if (updatedBook != null) {
                bookService.updateBook(updatedBook);
                refreshData();
                logger.info("Book updated: " + updatedBook.getTitle());
                JOptionPane.showMessageDialog(this, 
                    "Book updated successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.error("Error editing book", ex);
            JOptionPane.showMessageDialog(this, 
                "Error editing book: " + ex.getMessage(), 
                "Edit Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles delete book button action.
     */
    private void deleteBook(ActionEvent e) {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a book to delete", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            String bookTitle = (String) tableModel.getValueAt(selectedRow, 2);
            
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete \"" + bookTitle + "\"?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                bookService.deleteBook(bookId);
                refreshData();
                logger.info("Book deleted: " + bookTitle);
                JOptionPane.showMessageDialog(this, 
                    "Book deleted successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.error("Error deleting book", ex);
            JOptionPane.showMessageDialog(this, 
                "Error deleting book: " + ex.getMessage(), 
                "Delete Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles add to cart button action.
     */
    private void addToCart(ActionEvent e) {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a book to add to cart", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            int availableCopies = (int) tableModel.getValueAt(selectedRow, 10);
            
            if (availableCopies <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "This book is not available for borrowing", 
                    "Not Available", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Book selectedBook = bookService.getBookById(bookId);
            mainWindow.getCartPanel().addBookToCart(selectedBook);
            
            logger.info("Book added to cart: " + selectedBook.getTitle());
            JOptionPane.showMessageDialog(this, 
                "Book added to cart successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Switch to cart tab
            mainWindow.switchToTab(5); // Cart tab index
        } catch (Exception ex) {
            logger.error("Error adding book to cart", ex);
            JOptionPane.showMessageDialog(this, 
                "Error adding book to cart: " + ex.getMessage(), 
                "Cart Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
