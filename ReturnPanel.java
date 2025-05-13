package com.library.app.ui;

import com.library.app.factory.DialogFactory;
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
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;


public class ReturnPanel extends JPanel {

    private static final Logger logger = new Logger(ReturnPanel.class.getName());
    private BookPanel bookPanel;

    private final MainWindow mainWindow;
    private final BookService bookService;
    private final MemberService memberService;
    private final TransactionService transactionService;
    private final UIComponentFactory uiFactory;
    private final DialogFactory dialogFactory;
    
    private JTextField memberSearchField;
    private JComboBox<String> memberSearchTypeCombo;
    private JTable borrowedBooksTable;
    private DefaultTableModel tableModel;
    private Member selectedMember;
    private JLabel selectedMemberLabel;
    private JLabel borrowedCountLabel;
    

    public ReturnPanel(MainWindow mainWindow, BookPanel bookPanel) {
        this.mainWindow = mainWindow;
        this.bookPanel = bookPanel;  // ← احفظه في المتغير
        this.bookService = new BookService();
        this.memberService = new MemberService();
        this.transactionService = new TransactionService();
        this.uiFactory = new UIComponentFactory();
        this.dialogFactory = new DialogFactory();

        initializeUI();
        logger.info("ReturnPanel initialized");
    }



    private JButton createActionButtonn(String text, String iconName, Color color, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);

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


    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245)); // Light gray background
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Book Returns");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setIcon(uiFactory.createIcon("corner-down-left"));
        titleLabel.setIconTextGap(10);
        titleLabel.setHorizontalTextPosition(JLabel.RIGHT);
        
        JLabel subTitleLabel = new JLabel("Process book returns and update inventory");
        subTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(100, 100, 100));
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subTitleLabel, BorderLayout.SOUTH);
        
        // Create top panel with member selection
        JPanel topPanel = createTopPanel();
        
        // Create header container
        JPanel headerContainer = new JPanel(new BorderLayout(0, 10));
        headerContainer.setOpaque(false);
        headerContainer.add(titlePanel, BorderLayout.NORTH);
        headerContainer.add(topPanel, BorderLayout.CENTER);
        
        add(headerContainer, BorderLayout.NORTH);
        
        // Create table panel
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
        
        // Member search panel with modern styling
        JPanel memberSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        memberSearchPanel.setOpaque(false);
        
        JLabel memberSearchLabel = new JLabel("Find Member:");
        memberSearchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create search field with rounded border
        memberSearchField = new JTextField(15);
        memberSearchField.setFont(new Font("Arial", Font.PLAIN, 14));
        memberSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        // Search type combo with better styling
        memberSearchTypeCombo = new JComboBox<>(new String[]{"ID", "Name", "Email", "Phone"});
        memberSearchTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        memberSearchTypeCombo.setBackground(Color.WHITE);
        memberSearchTypeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Modern styled buttons
        JButton searchMemberButton = createActionButton("Search", "", new Color(25, 118, 210), this::searchMember);
        JButton selectMemberButton = createActionButton("Select", "", new Color(76, 175, 80), this::selectMember);
        JButton clearMemberButton = createActionButton("Clear", "", new Color(158, 158, 158), this::clearMember);
        
        memberSearchPanel.add(memberSearchLabel);
        memberSearchPanel.add(memberSearchField);
        memberSearchPanel.add(memberSearchTypeCombo);
        memberSearchPanel.add(searchMemberButton);
        memberSearchPanel.add(selectMemberButton);
        memberSearchPanel.add(clearMemberButton);
        
        topPanel.add(memberSearchPanel, BorderLayout.NORTH);
        
        // Selected member info panel with modern card styling
        JPanel memberInfoPanel = new JPanel();
        memberInfoPanel.setLayout(new BoxLayout(memberInfoPanel, BoxLayout.Y_AXIS));
        memberInfoPanel.setOpaque(false);
        memberInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Decorative card for member info
        JPanel memberInfoCard = new JPanel();
        memberInfoCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        memberInfoCard.setBackground(new Color(232, 245, 233)); // Light green background
        memberInfoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 237, 222), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel memberInfoLabel = new JLabel("Current Member:");
        memberInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        selectedMemberLabel = new JLabel("No member selected");
        selectedMemberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectedMemberLabel.setForeground(new Color(76, 175, 80));
        
        borrowedCountLabel = new JLabel("Books Borrowed: 0");
        borrowedCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        borrowedCountLabel.setIcon(uiFactory.createIcon("book"));
        borrowedCountLabel.setIconTextGap(8);
        
        memberInfoCard.add(memberInfoLabel);
        memberInfoCard.add(selectedMemberLabel);
        memberInfoCard.add(Box.createHorizontalStrut(20)); // Add some space
        memberInfoCard.add(borrowedCountLabel);
        
        memberInfoPanel.add(memberInfoCard);
        
        topPanel.add(memberInfoPanel, BorderLayout.SOUTH);
        
        return topPanel;
    }
    

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Borrowed Books"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        tablePanel.setBackground(Color.WHITE);
        
        // Create book table
        String[] columnNames = {"Transaction ID", "Book ID", "Title", "Borrow Date", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        borrowedBooksTable = new JTable(tableModel);
        borrowedBooksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        borrowedBooksTable.getTableHeader().setReorderingAllowed(false);
        borrowedBooksTable.setRowHeight(30); // Taller rows for better readability
        borrowedBooksTable.setIntercellSpacing(new Dimension(10, 5)); // More spacing between cells
        borrowedBooksTable.setShowGrid(false); // Hide grid lines for modern look
        borrowedBooksTable.setFillsViewportHeight(true);
        
        // Set table header style
        borrowedBooksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        borrowedBooksTable.getTableHeader().setBackground(new Color(25, 118, 210));
//        borrowedBooksTable.getTableHeader().setForeground(Color.WHITE);
        borrowedBooksTable.getTableHeader().setPreferredSize(new Dimension(0, 35)); // Make header taller
        
        // Custom renderer for overdue dates (red text)
        DefaultTableCellRenderer dueDateRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                        boolean isSelected, boolean hasFocus, 
                                                        int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 4) { // Due Date column
                    String status = (String) table.getValueAt(row, 5);
                    if ("OVERDUE".equals(status)) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(table.getForeground());
                    }
                }
                
                return c;
            }
        };
        
        borrowedBooksTable.getColumnModel().getColumn(4).setCellRenderer(dueDateRenderer);
        
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
//        JButton refreshButton = createActionButton("Refresh", "refresh-cw", new Color(25, 118, 210), e -> refreshBorrowedBooks());
        JButton returnBookButton = createActionButton("Return Book", "", new Color(76, 175, 80), this::returnBook);
        JButton returnAllButton = createActionButton("Return All Books", "", new Color(76, 175, 80), this::returnAllBooks);
        
//        buttonPanel.add(refreshButton);
        buttonPanel.add(returnBookButton);
        buttonPanel.add(returnAllButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add info panel on the left
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        
        JLabel infoLabel = new JLabel("Select a book to return or use 'Return All Books' to process multiple returns");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setIcon(uiFactory.createIcon("info"));
        infoLabel.setIconTextGap(8);
        
        infoPanel.add(infoLabel);
        bottomPanel.add(infoPanel, BorderLayout.WEST);
        
        return bottomPanel;
    }
    

    public void refreshData() {
        if (selectedMember != null) {
            refreshBorrowedBooks();
        } else {
            tableModel.setRowCount(0);
        }
    }
    

    private void searchMember(ActionEvent e) {
        try {
            String searchText = memberSearchField.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter search criteria", 
                    "Search Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String searchType = (String) memberSearchTypeCombo.getSelectedItem();
            List<Member> results;
            
            switch (searchType) {
                case "ID":
                    try {
                        int id = Integer.parseInt(searchText);
                        results = memberService.searchMembersById(id);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Please enter a valid member ID", 
                            "Invalid Input", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    break;
                case "Name":
                    results = memberService.searchMembersByName(searchText);
                    break;
                case "Email":
                    results = memberService.searchMembersByEmail(searchText);
                    break;
                case "Phone":
                    results = memberService.searchMembersByPhone(searchText);
                    break;
                default:
                    results = memberService.searchMembersByName(searchText);
            }
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No members found matching your criteria", 
                    "Search Result", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // If one member found, select automatically
            if (results.size() == 1) {
                selectedMember = results.get(0);
                updateMemberInfo();
                refreshBorrowedBooks();
                return;
            }
            
            // If multiple members found, show selection dialog
            Member selectedMember = dialogFactory.showMemberSelectionDialog(this, results);
            if (selectedMember != null) {
                this.selectedMember = selectedMember;
                updateMemberInfo();
                refreshBorrowedBooks();
            }
            
        } catch (Exception ex) {
            logger.error("Error searching for member", ex);
            JOptionPane.showMessageDialog(this, 
                "Error searching for member: " + ex.getMessage(), 
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void selectMember(ActionEvent e) {
        try {
            List<Member> allMembers = memberService.getAllMembers();
            if (allMembers.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No members found in the system", 
                    "No Members", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Member selectedMember = dialogFactory.showMemberSelectionDialog(this, allMembers);
            if (selectedMember != null) {
                this.selectedMember = selectedMember;
                updateMemberInfo();
                refreshBorrowedBooks();
            }
        } catch (Exception ex) {
            logger.error("Error selecting member", ex);
            JOptionPane.showMessageDialog(this, 
                "Error selecting member: " + ex.getMessage(), 
                "Selection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void clearMember(ActionEvent e) {
        selectedMember = null;
        memberSearchField.setText("");
        selectedMemberLabel.setText("No member selected");
        borrowedCountLabel.setText("Books Borrowed: 0");
        tableModel.setRowCount(0);
    }
    

    private void updateMemberInfo() {
        if (selectedMember != null) {
            selectedMemberLabel.setText(selectedMember.getName() + " (ID: " + selectedMember.getId() + ")");
            borrowedCountLabel.setText("Books Borrowed: " + selectedMember.getBorrowedCount());
            memberSearchField.setText(selectedMember.getName());
            logger.info("Member selected: " + selectedMember.getName());
        }
    }
    

    private JButton createActionButton(String text, String iconName, Color color, ActionListener listener) {
        JButton button = new JButton(text);
        button.setIcon(uiFactory.createIcon(iconName));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
//        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setIconTextGap(10);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addActionListener(listener);
        return button;
    }
    

    private void refreshBorrowedBooks() {
        try {
            if (selectedMember == null) {
                return;
            }
            
            tableModel.setRowCount(0);
            List<Transaction> transactions = transactionService.getActiveBorrowingsByMember(selectedMember.getId());
            
            for (Transaction transaction : transactions) {
                // Check if overdue
                String status = transaction.getStatus();
                
                if (transaction.getDueDate().before(new Date()) && "BORROWED".equals(status)) {
                    status = "OVERDUE";
                }
                
                Object[] rowData = {
                    transaction.getId(),
                    transaction.getBookId(),
                    transaction.getBookTitle(),
                    DateUtil.formatDate(transaction.getBorrowDate()),
                    DateUtil.formatDate(transaction.getDueDate()),
                    status
                };
                tableModel.addRow(rowData);
            }
            
            logger.info("Loaded " + transactions.size() + " borrowed books for member: " + selectedMember.getName());
            
            // Refresh member data to get current borrowed count
            selectedMember = memberService.getMemberById(selectedMember.getId());
            updateMemberInfo();
            
        } catch (Exception e) {
            logger.error("Error refreshing borrowed books", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading borrowed books: " + e.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void returnBook(ActionEvent e) {
        try {
            if (selectedMember == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a member first", 
                    "Member Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int selectedRow = borrowedBooksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a book to return", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
            int bookId = (int) tableModel.getValueAt(selectedRow, 1);
            String bookTitle = (String) tableModel.getValueAt(selectedRow, 2);
            
            // Process return
            Transaction transaction = transactionService.getTransactionById(transactionId);
            transaction.setReturnDate(new Date());
            transaction.setStatus("RETURNED");
            
            transactionService.updateTransaction(transaction);

            // Update book availability
            Book book = bookService.getBookById(bookId);
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookService.updateBook(book);

            
            // Update member's borrowed count
            selectedMember.setBorrowedCount(selectedMember.getBorrowedCount() - 1);
            memberService.updateMember(selectedMember);
            
            // Show confirmation dialog
            JOptionPane.showMessageDialog(this, 
                "Book \"" + bookTitle + "\" has been returned successfully.", 
                "Return Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the view
            refreshBorrowedBooks();
            bookPanel.refreshData(); // ← يحدث جدول الكتب بعد الإرجاع

// افترض إن عندك دالة بتحدث جدول الكتب

            logger.info("Book returned: " + bookTitle + " by member: " + selectedMember.getName());
            
        } catch (Exception ex) {
            logger.error("Error returning book", ex);
            JOptionPane.showMessageDialog(this, 
                "Error returning book: " + ex.getMessage(), 
                "Return Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void returnAllBooks(ActionEvent e) {
        try {
            if (selectedMember == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a member first", 
                    "Member Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int rowCount = tableModel.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(this, 
                    "This member has no books to return", 
                    "No Books", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Confirm return
            String message = "Return all " + rowCount + " book(s)?";
            
            int choice = JOptionPane.showConfirmDialog(this, 
                message, 
                "Confirm Return All", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Process returns
            Date returnDate = new Date();
            for (int i = 0; i < rowCount; i++) {
                int transactionId = (int) tableModel.getValueAt(i, 0);
                int bookId = (int) tableModel.getValueAt(i, 1);
                
                // Update transaction
                Transaction transaction = transactionService.getTransactionById(transactionId);
                transaction.setReturnDate(returnDate);
                transaction.setStatus("RETURNED");
                
                transactionService.updateTransaction(transaction);
                
                // Update book availability
                Book book = bookService.getBookById(bookId);
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookService.updateBook(book);
            }
            
            // Update member's borrowed count
            selectedMember.setBorrowedCount(0);
            memberService.updateMember(selectedMember);
            
            // Show confirmation dialog
            JOptionPane.showMessageDialog(this, 
                "All " + rowCount + " book(s) have been returned successfully.", 
                "Return Complete", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the view
            refreshBorrowedBooks();
            bookPanel.refreshData();
            logger.info("All books returned for member: " + selectedMember.getName());
            
        } catch (Exception ex) {
            logger.error("Error returning all books", ex);
            JOptionPane.showMessageDialog(this, 
                "Error returning books: " + ex.getMessage(), 
                "Return Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

}
