package com.library.app.ui;

import com.library.app.factory.UIComponentFactory;
import com.library.app.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainWindow extends JFrame {

    private static final Logger logger = new Logger(MainWindow.class.getName());
    private static final String APP_TITLE = "Library Management System";
    
    private JTabbedPane tabbedPane;
    private BookPanel bookPanel;
    private MemberPanel memberPanel;
//    private BorrowingPanel borrowingPanel;
    private ReturnPanel returnPanel;
    private CartPanel cartPanel;
    
    private final UIComponentFactory uiFactory;
    

    public MainWindow() {
        uiFactory = new UIComponentFactory();
        initializeUI();
        setupWindowListeners();
        logger.info("MainWindow initialized");

        // تم تمرير الـ bookPanel إلى الـ returnPanel
        returnPanel = new ReturnPanel(this, bookPanel);
    }



    private void initializeUI() {
        setTitle("Library Management System");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set custom icon
        setIconImage(uiFactory.createIcon("book").getImage());
        
        // Create main panel with BorderLayout and padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(240, 242, 245)); // Light gray background
        
        // Create header panel with application title
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for main content with better styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(50, 50, 50));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        
        // Initialize panels
        bookPanel = new BookPanel(this);
        memberPanel = new MemberPanel(this);
//        borrowingPanel = new BorrowingPanel(this);
        returnPanel = new ReturnPanel(this, bookPanel);


        cartPanel = new CartPanel(this);
        
        // Add panels to tabbed pane with icons
        tabbedPane.addTab("Books", uiFactory.createIcon(""), bookPanel, "Manage library books");
        tabbedPane.addTab("Members", uiFactory.createIcon(""), memberPanel, "Manage library members");
//        tabbedPane.addTab("Borrow", uiFactory.createIcon("arrow-right-circle"), borrowingPanel, "Borrow books to members");
        tabbedPane.addTab("Return", uiFactory.createIcon(""), returnPanel, "Return books from members");
        tabbedPane.addTab("Cart", uiFactory.createIcon(""), cartPanel, "View current book selection");
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Create status bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 102, 153));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel(APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel logoLabel = new JLabel(uiFactory.createIcon("book-open"));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel dateTimeLabel = new JLabel(new java.util.Date().toString());
        dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusBar.add(dateTimeLabel, BorderLayout.EAST);
        
        // Update time every second
        Timer timer = new Timer(1000, e -> dateTimeLabel.setText(new java.util.Date().toString()));
        timer.start();
        
        return statusBar;
    }
    

    private void setupWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    MainWindow.this,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    logger.info("Application exit requested by user");
                    dispose();
                    System.exit(0);
                }
            }
        });
    }
    

    public void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
    }
    

    public CartPanel getCartPanel() {
        return cartPanel;
    }
    

    public void refreshAllPanels() {
        bookPanel.refreshData();
        memberPanel.refreshData();
//        borrowingPanel.refreshData();
        returnPanel.refreshData();
        cartPanel.refreshData();
    }
}
