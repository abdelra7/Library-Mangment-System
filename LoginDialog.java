package com.library.app.ui;

import com.library.app.db.DatabaseConnection;
import com.library.app.util.Logger;
import com.library.app.factory.UIComponentFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LoginDialog extends JDialog {
    
    private static final Logger logger = new Logger(LoginDialog.class.getName());
    private UIComponentFactory uiFactory;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean authenticated = false;
    

    public LoginDialog(JFrame parent) {
        super(parent, "Administrator Login", true);
        this.uiFactory = new UIComponentFactory();
        initializeUI();
    }
    

    private void initializeUI() {
        // Set dialog properties
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with custom background color
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create login form panel
        JPanel loginPanel = createLoginPanel();
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to dialog
        add(mainPanel);
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
        
        // Handle window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                authenticated = false;
                dispose();
            }
        });
    }
    

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);
        
        // Add logo
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(uiFactory.createIcon("user-shield"));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Add welcome title
        JLabel titleLabel = new JLabel("Welcome to the Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setForeground(new Color(33, 33, 33));
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Please log in to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        subtitleLabel.setForeground(new Color(100, 100, 100));
        
        // Add components to panel
        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // Username label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameLabel.setHorizontalAlignment(JLabel.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(usernameLabel, gbc);
        
        // Username field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(createTextBorder());
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        loginPanel.add(usernameField, gbc);
        
        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setHorizontalAlignment(JLabel.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(15, 0, 5, 0);
        loginPanel.add(passwordLabel, gbc);
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(createTextBorder());
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        });
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        loginPanel.add(passwordField, gbc);
        
        return loginPanel;
    }
    

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(25, 118, 210));
//        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> attemptLogin());
        
        // Cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setBackground(new Color(180, 180, 180));
//        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        return buttonPanel;
    }
    

    private Border createTextBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }
    

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "يرجى إدخال اسم المستخدم وكلمة المرور",
                "بيانات غير مكتملة",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (authenticate(username, password)) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "اسم المستخدم أو كلمة المرور غير صحيحة",
                "خطأ في تسجيل الدخول",
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    

    private boolean authenticate(String username, String password) {
        // For development convenience - admin/admin123 always works
        if ("admin".equals(username) && "admin123".equals(password)) {
            logger.info("Developer access granted for user: " + username);
            return true;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM members WHERE username = ? AND password = ? AND role = 'ADMIN'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            boolean authenticated = rs.next();
            
            if (authenticated) {
                logger.info("User authenticated: " + username);
            } else {
                logger.info("Authentication failed for user: " + username);
            }
            
            return authenticated;
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                logger.error("Error closing resources", e);
            }
        }
    }
    

    public boolean showDialog() {
        setVisible(true);
        return authenticated;
    }
}