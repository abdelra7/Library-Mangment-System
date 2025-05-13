package com.library.app.ui;

import com.library.app.factory.DialogFactory;
import com.library.app.factory.UIComponentFactory;
import com.library.app.model.Member;
import com.library.app.service.MemberService;
import com.library.app.util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class MemberPanel extends JPanel {

    private static final Logger logger = new Logger(MemberPanel.class.getName());
    
    private final MainWindow mainWindow;
    private final MemberService memberService;
    private final UIComponentFactory uiFactory;
    private final DialogFactory dialogFactory;
    
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    

    public MemberPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.memberService = new MemberService();
        this.uiFactory = new UIComponentFactory();
        this.dialogFactory = new DialogFactory();
        
        initializeUI();
        refreshData();
        logger.info("MemberPanel initialized");
    }
    

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 242, 245)); // Light gray background
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("إدارة أعضاء المكتبة");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(25, 118, 210));
        titleLabel.setHorizontalAlignment(JLabel.RIGHT);
        titleLabel.setIcon(uiFactory.createIcon("users"));
        titleLabel.setIconTextGap(10);
        titleLabel.setHorizontalTextPosition(JLabel.LEFT);
        
        JLabel statsLabel = new JLabel("Total Members: 0");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(100, 100, 100));
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(statsLabel, BorderLayout.SOUTH);
        
        // Create top panel with controls
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Create search panel with modern look
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create search field with placeholder text and rounded border
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        // Search type combo with better styling
        searchTypeCombo = new JComboBox<>(new String[]{"ID", "Name", "Email", "Phone"});
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
        searchButton.addActionListener(this::searchMembers);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        
        // Create button panel with modern styled buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        // Modern buttons with consistent styling
        JButton addButton = createActionButton("Add Member", "", new Color(76, 175, 80), this::addMember);
        JButton editButton = createActionButton("Edit", "", new Color(255, 152, 0), this::editMember);
        JButton deleteButton = createActionButton("Delete", "", new Color(244, 67, 54), this::deleteMember);
//        JButton viewHistoryButton = createActionButton("History", "", new Color(121, 85, 172), this::viewMemberHistory);
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
//        buttonPanel.add(viewHistoryButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create table for members with English column names
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Address", "Join Date", "Role", "Status", "Borrowed Books"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table with modern styling
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        memberTable.getTableHeader().setReorderingAllowed(false);
        memberTable.setRowHeight(30); // Taller rows for better readability
        memberTable.setIntercellSpacing(new Dimension(10, 5)); // More spacing between cells
        memberTable.setShowGrid(false); // Hide grid lines for modern look
        memberTable.setFillsViewportHeight(true);
        
        // Set table header style
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        memberTable.getTableHeader().setBackground(new Color(25, 118, 210));
//        memberTable.getTableHeader().setForeground(Color.WHITE);
        memberTable.getTableHeader().setPreferredSize(new Dimension(0, 35)); // Make header taller
        
        // Set alternating row colors
        memberTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set alternating row background colors
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                
                // Center-align the ID and Books Borrowed columns
                if (column == 0 || column == 8) {
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
        memberTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
        memberTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Address
        memberTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Join Date
        memberTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Role
        memberTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Status
        memberTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Books Borrowed
        
        // Create scroll pane with styled border
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
        
        // Status panel at the bottom with better styling
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        statusPanel.setBackground(new Color(250, 250, 250));
        
        JLabel totalMembersLabel = new JLabel("إجمالي الأعضاء: 0");
        totalMembersLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalMembersLabel.setForeground(new Color(70, 70, 70));
        statusPanel.add(totalMembersLabel, BorderLayout.WEST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    

    public void refreshData() {
        try {
            tableModel.setRowCount(0); // Clear existing data
            
            List<Member> members = memberService.getAllMembers();
            for (Member member : members) {
                Object[] rowData = {
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone(),
                    member.getAddress(),
                    member.getJoinDate(),
                    member.getRole(),
                    member.getStatus(),
                    member.getBorrowedCount()
                };
                tableModel.addRow(rowData);
            }
            
            // Update total members count in status bar
            JPanel statusPanel = (JPanel) getComponent(2);
            JLabel totalMembersLabel = (JLabel) ((BorderLayout) statusPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            totalMembersLabel.setText("Total Members: " + members.size());
            
            logger.info("Member data refreshed, " + members.size() + " members loaded");
        } catch (Exception e) {
            logger.error("Error refreshing member data", e);
            JOptionPane.showMessageDialog(this, 
                "Error loading members: " + e.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void searchMembers(ActionEvent e) {
        try {
            String searchText = searchField.getText().trim();
            String searchType = (String) searchTypeCombo.getSelectedItem();
            
            if (searchText.isEmpty()) {
                refreshData();
                return;
            }
            
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
            
            tableModel.setRowCount(0);
            for (Member member : results) {
                Object[] rowData = {
                    member.getId(),
                    member.getName(),
                    member.getEmail(),
                    member.getPhone(),
                    member.getAddress(),
                    member.getJoinDate(),
                    member.getRole(),
                    member.getStatus(),
                    member.getBorrowedCount()
                };
                tableModel.addRow(rowData);
            }
            
            // Update status
            JPanel statusPanel = (JPanel) getComponent(2);
            JLabel totalMembersLabel = (JLabel) ((BorderLayout) statusPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            totalMembersLabel.setText("Search Results: " + results.size() + " members found");
            
            logger.info("Member search completed, " + results.size() + " results for " + searchType + ": " + searchText);
        } catch (Exception ex) {
            logger.error("Error searching members", ex);
            JOptionPane.showMessageDialog(this, 
                "Error searching members: " + ex.getMessage(), 
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private JButton createActionButton(String text, String iconName, Color color, ActionListener actionListener) {
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
        button.addActionListener(actionListener);
        return button;
    }
    

    private void addMember(ActionEvent e) {
        try {
            Member newMember = dialogFactory.showAddMemberDialog(this);
            if (newMember != null) {
                memberService.addMember(newMember);
                refreshData();
                logger.info("New member added: " + newMember.getName());
                JOptionPane.showMessageDialog(this, 
                    "Member added successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.error("Error adding member", ex);
            JOptionPane.showMessageDialog(this, 
                "Error adding member: " + ex.getMessage(), 
                "Add Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void editMember(ActionEvent e) {
        try {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a member to edit", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int memberId = (int) tableModel.getValueAt(selectedRow, 0);
            Member selectedMember = memberService.getMemberById(memberId);
            
            Member updatedMember = dialogFactory.showEditMemberDialog(this, selectedMember);
            if (updatedMember != null) {
                memberService.updateMember(updatedMember);
                refreshData();
                logger.info("Member updated: " + updatedMember.getName());
                JOptionPane.showMessageDialog(this, 
                    "Member updated successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.error("Error editing member", ex);
            JOptionPane.showMessageDialog(this, 
                "Error editing member: " + ex.getMessage(), 
                "Edit Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void deleteMember(ActionEvent e) {
        try {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a member to delete", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int memberId = (int) tableModel.getValueAt(selectedRow, 0);
            String memberName = (String) tableModel.getValueAt(selectedRow, 1);
            int borrowedCount = (int) tableModel.getValueAt(selectedRow, 8);
            
            if (borrowedCount > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot delete member with borrowed books. Please return all books first.", 
                    "Delete Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete member \"" + memberName + "\"?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                memberService.deleteMember(memberId);
                refreshData();
                logger.info("Member deleted: " + memberName);
                JOptionPane.showMessageDialog(this, 
                    "Member deleted successfully", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            logger.error("Error deleting member", ex);
            JOptionPane.showMessageDialog(this, 
                "Error deleting member: " + ex.getMessage(), 
                "Delete Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void viewMemberHistory(ActionEvent e) {
        try {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a member to view history", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int memberId = (int) tableModel.getValueAt(selectedRow, 0);
            String memberName = (String) tableModel.getValueAt(selectedRow, 1);
            
            dialogFactory.showMemberHistoryDialog(this, memberId, memberName);
            logger.info("Viewed history for member: " + memberName);
        } catch (Exception ex) {
            logger.error("Error viewing member history", ex);
            JOptionPane.showMessageDialog(this, 
                "Error viewing member history: " + ex.getMessage(), 
                "History Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
