package com.library.app;

import com.library.app.db.DatabaseConnection;
import com.library.app.ui.LoginDialog;
import com.library.app.ui.MainWindow;
import com.library.app.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;


public class LibraryManagementSystem {

    private static final Logger logger = new Logger(LibraryManagementSystem.class.getName());

    public static void main(String[] args) {
        logger.info("Starting Library Management System");
        
        // Set custom UI properties and look and feel
        setupUILookAndFeel();
        
        // Initialize database
        initializeDatabase();
        
        // Launch the application with login dialog
        SwingUtilities.invokeLater(() -> {
            try {
                // Show splash screen
                JFrame splashFrame = createSplashScreen();
                splashFrame.setVisible(true);
                
                // Simulate loading
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("Loading splash screen interrupted", e);
                }
                
                // Close splash screen
                splashFrame.dispose();
                
                // Show login dialog
                JFrame tempFrame = new JFrame();
                LoginDialog loginDialog = new LoginDialog(tempFrame);
                boolean authenticated = loginDialog.showDialog();
                
                if (authenticated) {
                    // User authenticated, create main window
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    logger.info("Application UI launched successfully");
                } else {
                    // User canceled login
                    logger.info("Login canceled, exiting application");
                    System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Failed to start application UI", e);
                JOptionPane.showMessageDialog(null, 
                    "حدث خطأ أثناء بدء تشغيل التطبيق: " + e.getMessage(), 
                    "خطأ في بدء التشغيل", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    

    private static void setupUILookAndFeel() {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set custom colors and fonts
            Color primaryColor = new Color(25, 118, 210); // Material Blue
            Color accentColor = new Color(255, 160, 0);   // Material Amber
            
            // Update UI defaults
            UIManager.put("Button.background", new Color(245, 245, 245));
            UIManager.put("Button.foreground", new Color(33, 33, 33));
            UIManager.put("Button.select", primaryColor);
            UIManager.put("Button.focus", primaryColor);
            
            UIManager.put("TabbedPane.selected", primaryColor);
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
            
            UIManager.put("TextField.caretForeground", primaryColor);
            UIManager.put("TextField.selectionBackground", primaryColor.brighter());
            
            UIManager.put("Table.selectionBackground", primaryColor);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            
            UIManager.put("ProgressBar.foreground", primaryColor);
            UIManager.put("ProgressBar.selectionBackground", accentColor);
            
            // Set custom fonts
            Font defaultFont = new Font("Arial", Font.PLAIN, 12);
            Font boldFont = new Font("Arial", Font.BOLD, 12);
            Font largeFont = new Font("Arial", Font.BOLD, 14);
            
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", boldFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("TabbedPane.font", boldFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("CheckBox.font", defaultFont);
            UIManager.put("RadioButton.font", defaultFont);
            UIManager.put("OptionPane.messageFont", largeFont);
            UIManager.put("OptionPane.buttonFont", boldFont);
            
            logger.info("Custom UI look and feel applied successfully");
        } catch (Exception e) {
            logger.error("Failed to set UI look and feel", e);
        }
    }
    

    private static JFrame createSplashScreen() {
        JFrame splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        
        // Create main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(25, 118, 210), 2));
        panel.setBackground(Color.WHITE);
        
        // Create header
        JLabel titleLabel = new JLabel("نظام إدارة المكتبة");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Create icon
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(new ImageIcon()); // Fallback to no icon if image is missing
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create version label
        JLabel versionLabel = new JLabel("الإصدار 1.0.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Create progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(25, 118, 210));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(300, 4));
        
        // Create loading message
        JLabel loadingLabel = new JLabel("جاري تحميل النظام...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Create bottom panel for progress and message
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.add(progressBar, BorderLayout.NORTH);
        bottomPanel.add(loadingLabel, BorderLayout.CENTER);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50));
        
        // Add components to panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(iconLabel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.add(versionLabel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);
        
        // Add panel to frame
        splashFrame.add(panel);
        splashFrame.setSize(400, 300);
        splashFrame.setLocationRelativeTo(null);
        
        return splashFrame;
    }
    

    private static void initializeDatabase() {
        try {
            logger.info("Initializing database...");
            Connection conn = DatabaseConnection.getInstance().getConnection();
            
            // Read schema.sql file and execute SQL statements
            File schemaFile = new File("src/main/resources/schema.sql");
            InputStream is;
            
            if (schemaFile.exists()) {
                // Try to read from file system directly
                is = new FileInputStream(schemaFile);
                logger.info("Using schema.sql from filesystem: " + schemaFile.getAbsolutePath());
            } else {
                // Fallback to resource stream
                is = LibraryManagementSystem.class.getResourceAsStream("/schema.sql");
                if (is == null) {
                    logger.error("Could not find schema.sql file");
                    throw new IOException("Schema file not found");
                }
            }
            
            // Execute each SQL statement individually to ensure proper sequence
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines when determining statement endings
                    if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                        sb.append(line).append("\n");
                        continue;
                    }
                    
                    sb.append(line).append("\n");
                    
                    // Check if this line contains the end of an SQL statement
                    if (line.trim().endsWith(";")) {
                        String sql = sb.toString();
                        try (Statement stmt = conn.createStatement()) {
                            // Execute the SQL
                            stmt.execute(sql);
                            
                            // Log a preview of the executed SQL
                            String sqlPreview = sql.substring(0, Math.min(sql.length(), 50)).replaceAll("\\s+", " ");
                            logger.info("Executed SQL: " + sqlPreview + "...");
                            
                            // Add a small delay between statements to ensure proper ordering
                            Thread.sleep(100);
                        } catch (SQLException e) {
                            // Log the error but continue with other statements
                            logger.error("Error executing SQL: " + sql, e);
                            logger.error("SQLException details: " + e.getMessage());
                        }
                        sb = new StringBuilder();
                    }
                }
            }
            
            // Verify tables were created correctly
            try {
                logger.info("Verifying database tables...");
                int tableCount = 0;
                String[] expectedTables = {"books", "members", "transactions", "reservations", "settings"};
                
                for (String tableName : expectedTables) {
                    try (Statement stmt = conn.createStatement()) {
                        // A simple query to check if table exists
                        stmt.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1");
                        tableCount++;
                        logger.info("Table verified: " + tableName);
                    } catch (SQLException e) {
                        logger.error("Table verification failed for: " + tableName);
                        
                        // This is a critical table, create it directly
                        if ("members".equals(tableName)) {
                            try (Statement createStmt = conn.createStatement()) {
                                String createMembersTable = "CREATE TABLE IF NOT EXISTS members (" +
                                    "id SERIAL PRIMARY KEY, " +
                                    "name VARCHAR(255) NOT NULL, " +
                                    "email VARCHAR(255) UNIQUE, " +
                                    "phone VARCHAR(20), " +
                                    "address TEXT, " +
                                    "join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                    "expiry_date TIMESTAMP, " +
                                    "membership_type VARCHAR(50) DEFAULT 'REGULAR', " +
                                    "status VARCHAR(20) DEFAULT 'ACTIVE', " +
                                    "max_books INT DEFAULT 5, " +
                                    "role VARCHAR(20) DEFAULT 'MEMBER', " +
                                    "username VARCHAR(50) UNIQUE, " +
                                    "password VARCHAR(255)" +
                                    ")";
                                createStmt.execute(createMembersTable);
                                logger.info("Created missing members table");
                                
                                // Create admin user
                                String insertAdmin = "INSERT INTO members (name, email, phone, address, role, username, password) " +
                                    "VALUES ('المسؤول', 'admin@library.com', '000-000-0000', 'عنوان المكتبة', 'ADMIN', 'admin', 'admin123')";
                                createStmt.execute(insertAdmin);
                                logger.info("Created admin user");
                            } catch (SQLException ex) {
                                logger.error("Failed to create members table", ex);
                            }
                        }
                    }
                }
                
                logger.info("Table verification completed: " + tableCount + "/" + expectedTables.length + " tables exist");
            } catch (Exception e) {
                logger.error("Error during table verification", e);
            }
            
            logger.info("Database initialization completed successfully");
            
        } catch (IOException | InterruptedException e) {
            logger.error("Database initialization failed", e);
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage(), 
                "Error in DataBase",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}