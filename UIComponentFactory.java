package com.library.app.factory;

import com.library.app.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


public class UIComponentFactory {
    
    private static final Logger logger = new Logger(UIComponentFactory.class.getName());
    
    // Cache for icons to improve performance
    private final Map<String, ImageIcon> iconCache = new HashMap<>();
    

    public JButton createButton(String text, String iconName, ActionListener actionListener) {
        JButton button = new JButton(text);
        
        if (iconName != null && !iconName.isEmpty()) {
            button.setIcon(createIcon(iconName));
        }
        
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        
        button.setFocusPainted(false);
        
        return button;
    }
    

    public ImageIcon createIcon(String iconName) {
        return createIcon(iconName, null);
    }

    public ImageIcon createIcon(String iconName, Color color) {
        // Check cache first
        String cacheKey = iconName + (color != null ? "_" + color.getRGB() : "");
        if (iconCache.containsKey(cacheKey)) {
            return iconCache.get(cacheKey);
        }
        
        // Use Feather SVG strings for icons
        String svgPath = getFeatherIconSvgString(iconName);
        if (svgPath == null) {
            logger.warn("Icon not found: " + iconName);
            return null;
        }
        
        try {
            // Create SVG icon with specified color or default black
            String colorHex = color != null ? 
                String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()) : 
                "#000000";
            
            String svgContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" " +
                "fill=\"none\" stroke=\"" + colorHex + "\" stroke-width=\"2\" stroke-linecap=\"round\" " +
                "stroke-linejoin=\"round\">" + svgPath + "</svg>";
            
            // Convert SVG to ImageIcon
            ImageIcon icon = new ImageIcon(svgContent.getBytes());
            iconCache.put(cacheKey, icon);
            return icon;
        } catch (Exception e) {
            logger.error("Error creating icon: " + iconName, e);
            return null;
        }
    }
    

    public JLabel createLabel(String text, String iconName) {
        JLabel label = new JLabel(text);
        
        if (iconName != null && !iconName.isEmpty()) {
            label.setIcon(createIcon(iconName));
        }
        
        return label;
    }
    

    public JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        return textField;
    }
    

    public JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        return passwordField;
    }
    

    public JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    

    public <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        return comboBox;
    }
    

    public JSpinner createSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        return spinner;
    }
    

    public JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        return checkBox;
    }
    

    public JRadioButton createRadioButton(String text, boolean selected) {
        JRadioButton radioButton = new JRadioButton(text, selected);
        return radioButton;
    }
    

    public JPanel createPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        return panel;
    }
    

    public JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        return scrollPane;
    }
    

    private String getFeatherIconSvgString(String iconName) {
        // Map icon names to Feather SVG path strings
        switch (iconName) {
            case "book":
                return "<path d=\"M4 19.5A2.5 2.5 0 0 1 6.5 17H20\"></path><path d=\"M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z\"></path>";
            case "book-open":
                return "<path d=\"M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z\"></path><path d=\"M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z\"></path>";
            case "user":
                return "<path d=\"M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2\"></path><circle cx=\"12\" cy=\"7\" r=\"4\"></circle>";
            case "user-plus":
                return "<path d=\"M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path><circle cx=\"8.5\" cy=\"7\" r=\"4\"></circle><line x1=\"20\" y1=\"8\" x2=\"20\" y2=\"14\"></line><line x1=\"23\" y1=\"11\" x2=\"17\" y2=\"11\"></line>";
            case "user-x":
                return "<path d=\"M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path><circle cx=\"8.5\" cy=\"7\" r=\"4\"></circle><line x1=\"18\" y1=\"8\" x2=\"23\" y2=\"13\"></line><line x1=\"23\" y1=\"8\" x2=\"18\" y2=\"13\"></line>";
            case "user-check":
                return "<path d=\"M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path><circle cx=\"8.5\" cy=\"7\" r=\"4\"></circle><polyline points=\"17 11 19 13 23 9\"></polyline>";
            case "users":
                return "<path d=\"M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path><circle cx=\"9\" cy=\"7\" r=\"4\"></circle><path d=\"M23 21v-2a4 4 0 0 0-3-3.87\"></path><path d=\"M16 3.13a4 4 0 0 1 0 7.75\"></path>";
            case "search":
                return "<circle cx=\"11\" cy=\"11\" r=\"8\"></circle><line x1=\"21\" y1=\"21\" x2=\"16.65\" y2=\"16.65\"></line>";
            case "plus":
                return "<line x1=\"12\" y1=\"5\" x2=\"12\" y2=\"19\"></line><line x1=\"5\" y1=\"12\" x2=\"19\" y2=\"12\"></line>";
            case "plus-circle":
                return "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle><line x1=\"12\" y1=\"8\" x2=\"12\" y2=\"16\"></line><line x1=\"8\" y1=\"12\" x2=\"16\" y2=\"12\"></line>";
            case "edit":
                return "<path d=\"M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7\"></path><path d=\"M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z\"></path>";
            case "trash-2":
                return "<polyline points=\"3 6 5 6 21 6\"></polyline><path d=\"M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2\"></path><line x1=\"10\" y1=\"11\" x2=\"10\" y2=\"17\"></line><line x1=\"14\" y1=\"11\" x2=\"14\" y2=\"17\"></line>";
            case "arrow-right-circle":
                return "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle><polyline points=\"12 16 16 12 12 8\"></polyline><line x1=\"8\" y1=\"12\" x2=\"16\" y2=\"12\"></line>";
            case "arrow-left-circle":
                return "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle><polyline points=\"12 8 8 12 12 16\"></polyline><line x1=\"16\" y1=\"12\" x2=\"8\" y2=\"12\"></line>";
            case "shopping-cart":
                return "<circle cx=\"9\" cy=\"21\" r=\"1\"></circle><circle cx=\"20\" cy=\"21\" r=\"1\"></circle><path d=\"M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6\"></path>";
            case "bar-chart":
                return "<line x1=\"12\" y1=\"20\" x2=\"12\" y2=\"10\"></line><line x1=\"18\" y1=\"20\" x2=\"18\" y2=\"4\"></line><line x1=\"6\" y1=\"20\" x2=\"6\" y2=\"16\"></line>";
            case "bar-chart-2":
                return "<line x1=\"18\" y1=\"20\" x2=\"18\" y2=\"10\"></line><line x1=\"12\" y1=\"20\" x2=\"12\" y2=\"4\"></line><line x1=\"6\" y1=\"20\" x2=\"6\" y2=\"14\"></line>";
            case "pie-chart":
                return "<path d=\"M21.21 15.89A10 10 0 1 1 8 2.83\"></path><path d=\"M22 12A10 10 0 0 0 12 2v10z\"></path>";
            case "clock":
                return "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle><polyline points=\"12 6 12 12 16 14\"></polyline>";
            case "calendar":
                return "<rect x=\"3\" y=\"4\" width=\"18\" height=\"18\" rx=\"2\" ry=\"2\"></rect><line x1=\"16\" y1=\"2\" x2=\"16\" y2=\"6\"></line><line x1=\"8\" y1=\"2\" x2=\"8\" y2=\"6\"></line><line x1=\"3\" y1=\"10\" x2=\"21\" y2=\"10\"></line>";
            case "alert-circle":
                return "<circle cx=\"12\" cy=\"12\" r=\"10\"></circle><line x1=\"12\" y1=\"8\" x2=\"12\" y2=\"12\"></line><line x1=\"12\" y1=\"16\" x2=\"12.01\" y2=\"16\"></line>";
            case "check-circle":
                return "<path d=\"M22 11.08V12a10 10 0 1 1-5.93-9.14\"></path><polyline points=\"22 4 12 14.01 9 11.01\"></polyline>";
            case "check":
                return "<polyline points=\"20 6 9 17 4 12\"></polyline>";
            case "check-square":
                return "<polyline points=\"9 11 12 14 22 4\"></polyline><path d=\"M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11\"></path>";
            case "x":
                return "<line x1=\"18\" y1=\"6\" x2=\"6\" y2=\"18\"></line><line x1=\"6\" y1=\"6\" x2=\"18\" y2=\"18\"></line>";
            case "refresh-cw":
                return "<polyline points=\"23 4 23 10 17 10\"></polyline><polyline points=\"1 20 1 14 7 14\"></polyline><path d=\"M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15\"></path>";
            case "dollar-sign":
                return "<line x1=\"12\" y1=\"1\" x2=\"12\" y2=\"23\"></line><path d=\"M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6\"></path>";
            case "download":
                return "<path d=\"M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4\"></path><polyline points=\"7 10 12 15 17 10\"></polyline><line x1=\"12\" y1=\"15\" x2=\"12\" y2=\"3\"></line>";
            case "printer":
                return "<polyline points=\"6 9 6 2 18 2 18 9\"></polyline><path d=\"M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2\"></path><rect x=\"6\" y=\"14\" width=\"12\" height=\"8\"></rect>";
            case "play":
                return "<polygon points=\"5 3 19 12 5 21 5 3\"></polygon>";
            case "trash":
                return "<polyline points=\"3 6 5 6 21 6\"></polyline><path d=\"M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2\"></path>";
            default:
                return null;
        }
    }
}
