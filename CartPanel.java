package com.library.app.ui;

import com.library.app.cart.CartComponent;
import com.library.app.cart.CartComposite;
import com.library.app.cart.CartItem;
import com.library.app.factory.UIComponentFactory;
import com.library.app.model.Book;
import com.library.app.model.Member;
import com.library.app.model.Transaction;
import com.library.app.service.BookService;
import com.library.app.service.MemberService;
import com.library.app.service.TransactionService;
import com.library.app.util.DateUtil;
import com.library.app.util.Logger;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class CartPanel extends JPanel {

    private static final Logger logger = new Logger(CartPanel.class.getName());
    
    private final MainWindow mainWindow;
    private final BookService bookService;
    private final MemberService memberService;
    private final TransactionService transactionService;
    private final UIComponentFactory uiFactory;
    
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalItemsLabel;
    private JComboBox<String> memberCombo;
    private JButton checkoutButton;
    

    private JButton createActionButton(String text, String iconName, Color color, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        //button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set icon if available
        if (iconName != null && !iconName.isEmpty()) {
            button.setIcon(uiFactory.createIcon(iconName));
            button.setIconTextGap(8);
        }
        
        // Add action listener
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        
        return button;
    }
    
    // Composite pattern implementation
    private CartComposite cartRoot;
    

    public CartPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.transactionService = new TransactionService();
        this.uiFactory = new UIComponentFactory();
        
        // Initialize the cart root (Composite pattern)
        this.cartRoot = new CartComposite("Root");
        
        initializeUI();
        logger.info("CartPanel initialized");
    }
    

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245)); // Light gray background
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Borrowing Cart");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setIcon(uiFactory.createIcon("shopping-cart"));
//        titleLabel.setIconTextGap(10);
        titleLabel.setHorizontalTextPosition(JLabel.RIGHT);
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Create top panel with title and info
        JPanel topPanel = createTopPanel();
        
        // Create header container
        JPanel headerContainer = new JPanel(new BorderLayout(0, 10));
        headerContainer.setOpaque(false);
        headerContainer.add(titlePanel, BorderLayout.NORTH);
        headerContainer.add(topPanel, BorderLayout.CENTER);
        
        add(headerContainer, BorderLayout.NORTH);
        
        // Create cart table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Create bottom panel with action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Member Selection"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create member selection panel with modern styling
        JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        memberPanel.setOpaque(false);
        
        JLabel memberLabel = new JLabel("Select Member:");
        memberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Style the combo box
        memberCombo = new JComboBox<>();
        memberCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        memberCombo.setBackground(Color.WHITE);
        memberCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        memberCombo.setPreferredSize(new Dimension(400, 35));
        
        loadMembersIntoComboBox();
        
        // Style the refresh button
//        JButton refreshMembersButton = createActionButton("", "refresh-cw", new Color(25, 118, 210), e -> loadMembersIntoComboBox());
//        refreshMembersButton.setToolTipText("Refresh member list");
        
        memberPanel.add(memberLabel);
        memberPanel.add(memberCombo);
//        memberPanel.add(refreshMembersButton);
        
        topPanel.add(memberPanel, BorderLayout.WEST);
        
        // Create cart info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        
        totalItemsLabel = new JLabel("Total Items: 0");
        totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalItemsLabel.setForeground(new Color(70, 70, 70));
        
        infoPanel.add(totalItemsLabel);
        
        topPanel.add(infoPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    


    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Cart Items"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.setBackground(Color.WHITE);
        
        // Create cart table
        String[] columnNames = {"Book ID", "Title", "Author", "ISBN", "Item Type"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getTableHeader().setReorderingAllowed(false);
        cartTable.setRowHeight(30); // Taller rows for better readability
        cartTable.setIntercellSpacing(new Dimension(10, 5)); // More spacing between cells
        cartTable.setShowGrid(false); // Hide grid lines for modern look
        cartTable.setFillsViewportHeight(true);
        
        // Set table header style
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(new Color(25, 118, 210));
//        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setPreferredSize(new Dimension(0, 35)); // Make header taller
        
        // Set alternating row colors
        cartTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set alternating row background colors
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                
                // Center-align the Book ID column
                if (column == 0) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }
                
                // Set border to create space between rows
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton removeButton = createActionButton("Remove Item", "", new Color(244, 67, 54), this::removeFromCart);
        JButton clearButton = createActionButton("Clear Cart", "", new Color(158, 158, 158), this::clearCart);
        checkoutButton = createActionButton("Checkout", "", new Color(76, 175, 80), this::checkout);
        
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(checkoutButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Description panel with modern styling
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descPanel.setOpaque(false);
        
        JLabel descLabel = new JLabel("Select books to borrow, then click Checkout to complete the transaction.");
        descLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setIcon(uiFactory.createIcon("info"));
        descLabel.setIconTextGap(10);
        
        descPanel.add(descLabel);
        
        bottomPanel.add(descPanel, BorderLayout.WEST);
        
        return bottomPanel;
    }
    

    private void loadMembersIntoComboBox() {
        try {
            memberCombo.removeAllItems();

            List<Member> members = memberService.getAllMembers();

            // ✅ ترتيب القائمة حسب الـ ID قبل الإضافة
            members.sort(Comparator.comparingInt(Member::getId));

            for (Member member : members) {
                memberCombo.addItem(member.getId() + " - " + member.getName());
            }

        } catch (Exception e) {
            logger.error("Error loading members into combo box", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading members: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }



    public void refreshData() {
        tableModel.setRowCount(0);
        
        List<CartComponent> items = cartRoot.getChildren();
        for (CartComponent component : items) {
            if (component instanceof CartItem) {
                CartItem item = (CartItem) component;
                Book book = item.getBook();
                
                Object[] rowData = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    "Single Book"
                };
                
                tableModel.addRow(rowData);
            } else if (component instanceof CartComposite) {
                CartComposite composite = (CartComposite) component;
                Object[] rowData = {
                    -1, // No ID for composites
                    composite.getName(),
                    composite.getChildren().size() + " items",
                    "",
                    "Book Group"
                };
                
                tableModel.addRow(rowData);
            }
        }
        
        // Update total items label
        totalItemsLabel.setText("Total Items: " + cartRoot.getItemCount());
        
        // Enable/disable checkout button based on cart contents
        checkoutButton.setEnabled(!items.isEmpty());
    }
    

    public void addBookToCart(Book book) {
        try {
            // Check if book already exists in cart
            for (CartComponent component : cartRoot.getChildren()) {
                if (component instanceof CartItem) {
                    CartItem item = (CartItem) component;
                    if (item.getBook().getId() == book.getId()) {
                        JOptionPane.showMessageDialog(this, 
                            "This book is already in your cart", 
                            "Duplicate Item", 
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
            
            // Add new book to cart
            CartItem newItem = new CartItem(book);
            cartRoot.add(newItem);
            
            refreshData();
            logger.info("Book added to cart: " + book.getTitle());
        } catch (Exception e) {
            logger.error("Error adding book to cart", e);
            JOptionPane.showMessageDialog(this, 
                "Error adding book to cart: " + e.getMessage(), 
                "Cart Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    public void createBookGroup(String groupName, List<Book> books) {
        try {
            CartComposite group = new CartComposite(groupName);
            
            for (Book book : books) {
                group.add(new CartItem(book));
            }
            
            cartRoot.add(group);
            refreshData();
            
            logger.info("Created book group: " + groupName + " with " + books.size() + " books");
        } catch (Exception e) {
            logger.error("Error creating book group", e);
            JOptionPane.showMessageDialog(this, 
                "Error creating book group: " + e.getMessage(), 
                "Cart Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void removeFromCart(ActionEvent e) {
        try {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select an item to remove", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String itemType = (String) tableModel.getValueAt(selectedRow, 4);
            CartComponent component = cartRoot.getChildren().get(selectedRow);
            
            if ("Book Group".equals(itemType)) {
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Do you want to remove the entire group?", 
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            cartRoot.remove(component);
            refreshData();
            
            logger.info("Item removed from cart: " + component.getName());
        } catch (Exception ex) {
            logger.error("Error removing item from cart", ex);
            JOptionPane.showMessageDialog(this, 
                "Error removing item from cart: " + ex.getMessage(), 
                "Cart Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void clearCart(ActionEvent e) {
        try {
            if (cartRoot.getChildren().isEmpty()) {
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(this, 
                "Clear all items from the cart?", 
                "Confirm Clear", 
                JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                cartRoot = new CartComposite("Root");
                refreshData();
                logger.info("Cart cleared");
            }
        } catch (Exception ex) {
            logger.error("Error clearing cart", ex);
            JOptionPane.showMessageDialog(this, 
                "Error clearing cart: " + ex.getMessage(), 
                "Cart Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void checkout(ActionEvent e) {
        try {
            if (cartRoot.getChildren().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Cart is empty. Please add books before checkout.", 
                    "Empty Cart", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (memberCombo.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a member for checkout", 
                    "Member Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get selected member
            String memberSelection = (String) memberCombo.getSelectedItem();
            int memberId = Integer.parseInt(memberSelection.split(" - ")[0]);
            Member member = memberService.getMemberById(memberId);
            
            // Flatten cart items (extract individual books from groups)
            List<Book> books = new ArrayList<>();
            cartRoot.getItemsFlat(books);
            
            // Check member borrow limit
            int currentlyBorrowed = member.getBorrowedCount();
            int toBeBorrowed = books.size();
            int maxBorrowLimit = 5; // Default limit
            
            if ("PREMIUM".equalsIgnoreCase(member.getRole())) {
                maxBorrowLimit = 10;
            } else if ("ADMIN".equalsIgnoreCase(member.getRole())) {
                maxBorrowLimit = 15;
            }
            
            if (currentlyBorrowed + toBeBorrowed > maxBorrowLimit) {
                JOptionPane.showMessageDialog(this, 
                    "Member has reached their borrowing limit.\n" +
                    "Current: " + currentlyBorrowed + ", Attempting to borrow: " + toBeBorrowed + 
                    ", Maximum allowed: " + maxBorrowLimit, 
                    "Limit Reached", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirm checkout
            int choice = JOptionPane.showConfirmDialog(this, 
                "Proceed with checkout for " + books.size() + " book(s) for " + member.getName() + "?", 
                "Confirm Checkout", 
                JOptionPane.YES_NO_OPTION);
                
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Process borrowing
            List<Transaction> transactions = new ArrayList<>();
            Date borrowDate = new Date();
            Date dueDate = DateUtil.calculateDueDate(borrowDate, 14); // 2 weeks by default
            
            for (Book book : books) {
                // Check book availability again to be safe
                Book freshBook = bookService.getBookById(book.getId());
                if (freshBook.getAvailableCopies() <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Book \"" + book.getTitle() + "\" is no longer available.\n" +
                        "Please remove it from your cart and try again.", 
                        "Book Unavailable", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Transaction transaction = new Transaction();
                transaction.setBookId(book.getId());
                transaction.setMemberId(member.getId());
                transaction.setBorrowDate(borrowDate);
                transaction.setDueDate(dueDate);
                transaction.setStatus("BORROWED");
                transaction.setBookTitle(book.getTitle());
                transaction.setMemberName(member.getName());
                
                transactions.add(transaction);
            }
            
            // Save transactions to database and update books
            for (Transaction transaction : transactions) {
                transactionService.addTransaction(transaction);
                
                // Update book availability
                Book book = bookService.getBookById(transaction.getBookId());
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                bookService.updateBook(book);
            }
            
            // Update member's borrowed count
            member.setBorrowedCount(member.getBorrowedCount() + books.size());
            memberService.updateMember(member);
            
            // Show checkout success message
            JOptionPane.showMessageDialog(this, 
                books.size() + " book(s) borrowed successfully by " + member.getName() + ".\n" +
                "Due date: " + DateUtil.formatDate(dueDate), 
                "Checkout Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear cart after successful checkout
            cartRoot = new CartComposite("Root");
            refreshData();
            
            // Refresh the BorrowingPanel data
            mainWindow.refreshAllPanels();
            
            logger.info("Checkout completed: " + books.size() + " books for member ID " + member.getId());
        } catch (Exception ex) {
            logger.error("Error during checkout", ex);
            JOptionPane.showMessageDialog(this, 
                "Error during checkout: " + ex.getMessage(), 
                "Checkout Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
