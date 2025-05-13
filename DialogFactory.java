package com.library.app.factory;

import com.library.app.model.Book;
import com.library.app.model.Member;
import com.library.app.model.Transaction;
import com.library.app.util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


public class DialogFactory {
    
    private static final Logger logger = new Logger(DialogFactory.class.getName());
    

    public void showInformationDialog(Component parentComponent, String message, String title) {
        logger.info("Showing information dialog: " + title);
        JOptionPane.showMessageDialog(
            parentComponent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    

    public void showWarningDialog(Component parentComponent, String message, String title) {
        logger.warn("Showing warning dialog: " + title);
        JOptionPane.showMessageDialog(
            parentComponent,
            message,
            title,
            JOptionPane.WARNING_MESSAGE
        );
    }
    

    public void showErrorDialog(Component parentComponent, String message, String title) {
        logger.error("Showing error dialog: " + title);
        JOptionPane.showMessageDialog(
            parentComponent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    

    public int showConfirmDialog(Component parentComponent, String message, String title) {
        logger.info("Showing confirmation dialog: " + title);
        return JOptionPane.showConfirmDialog(
            parentComponent,
            message,
            title,
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
    }

    public String showInputDialog(Component parentComponent, String message, String title) {
        logger.info("Showing input dialog: " + title);
        return JOptionPane.showInputDialog(
            parentComponent,
            message,
            title,
            JOptionPane.QUESTION_MESSAGE
        );
    }
    

    public JDialog createCustomDialog(Component parentComponent, JPanel contentPanel, String title, boolean modal) {
        logger.info("Creating custom dialog: " + title);
        
        JDialog dialog;
        if (parentComponent instanceof Frame) {
            dialog = new JDialog((Frame) parentComponent, title, modal);
        } else if (parentComponent instanceof Dialog) {
            dialog = new JDialog((Dialog) parentComponent, title, modal);
        } else {
            dialog = new JDialog(new JFrame(), title, modal);
        }
        
        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        
        return dialog;
    }
    

    public Member showMemberSelectionDialog(Component parentComponent, List<Member> members) {
        logger.info("Showing member selection dialog with " + members.size() + " members");
        
        if (members.isEmpty()) {
            showInformationDialog(parentComponent, "No members found.", "Member Selection");
            return null;
        }
        
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Phone");
        
        for (Member member : members) {
            model.addRow(new Object[]{
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhone()
            });
        }
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        int result = JOptionPane.showConfirmDialog(
            parentComponent,
            scrollPane,
            "Select a Member",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION && table.getSelectedRow() != -1) {
            int selectedRow = table.getSelectedRow();
            int memberId = (Integer) model.getValueAt(selectedRow, 0);
            
            for (Member member : members) {
                if (member.getId() == memberId) {
                    return member;
                }
            }
        }
        
        return null;
    }
    

    

    public void showBorrowReceiptDialog(Component parentComponent, Member member, List<Transaction> transactions, String dueDate) {
        logger.info("Showing borrow receipt dialog for " + member.getName() + ", " + transactions.size() + " books");
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='width: 300px'>");
        sb.append("<h2>Borrow Receipt</h2>");
        sb.append("<p><b>Member:</b> ").append(member.getName()).append("</p>");
        sb.append("<p><b>Date:</b> ").append(new java.util.Date()).append("</p>");
        sb.append("<p><b>Due Date:</b> ").append(dueDate).append("</p>");
        sb.append("<p><b>Books:</b></p>");
        sb.append("<ol>");
        
        for (Transaction transaction : transactions) {
            sb.append("<li>").append(transaction.getBookId()).append("</li>");
        }
        
        sb.append("</ol>");
        sb.append("<p><i>Please return the books by the due date.</i></p>");
        sb.append("</body></html>");
        
        JOptionPane.showMessageDialog(
            parentComponent,
            new JLabel(sb.toString()),
            "Borrow Receipt",
            JOptionPane.INFORMATION_MESSAGE
        );
    }


    public Book showAddBookDialog(Component parentComponent) {
        logger.info("Showing add book dialog");
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField isbnField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        JTextField genreField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherField);
        panel.add(new JLabel("Publication Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);
        panel.add(new JLabel("Description:"));
        panel.add(descScrollPane);
        
        int result = JOptionPane.showConfirmDialog(
            parentComponent,
            panel,
            "Add New Book",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Book book = new Book();
                book.setIsbn(isbnField.getText().trim());
                book.setTitle(titleField.getText().trim());
                book.setAuthor(authorField.getText().trim());
                book.setPublisher(publisherField.getText().trim());
                
                try {
                    if (!yearField.getText().trim().isEmpty()) {
                        book.setPublicationYear(Integer.parseInt(yearField.getText().trim()));
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Invalid year format: " + yearField.getText());
                }
                
                book.setGenre(genreField.getText().trim());
                book.setDescription(descriptionArea.getText().trim());
                book.setStatus("AVAILABLE");
                book.setTotalCopies(1);
                book.setAvailableCopies(1);
                
                return book;
            } catch (Exception e) {
                logger.error("Error creating book from dialog input", e);
                showErrorDialog(
                    parentComponent,
                    "Error creating book: " + e.getMessage(),
                    "Add Book Error"
                );
            }
        }
        
        return null;
    }
    

    public Book showEditBookDialog(Component parentComponent, Book book) {
        logger.info("Showing edit book dialog for: " + book.getTitle());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Dimension fieldSize = new Dimension(150, 25);

        JTextField isbnField = new JTextField(book.getIsbn(), 20);
        isbnField.setPreferredSize(fieldSize);

        JTextField titleField = new JTextField(book.getTitle(), 20);
        titleField.setPreferredSize(fieldSize);

        JTextField authorField = new JTextField(book.getAuthor(), 20);
        authorField.setPreferredSize(fieldSize);

        JTextField publisherField = new JTextField(
                book.getPublisher() != null ? book.getPublisher() : "", 20);
        publisherField.setPreferredSize(fieldSize);

        JTextField yearField = new JTextField(
                book.getPublicationYear() != 0 ? String.valueOf(book.getPublicationYear()) : "", 20);
        yearField.setPreferredSize(fieldSize);

        JTextField genreField = new JTextField(
                book.getGenre() != null ? book.getGenre() : "", 20);
        genreField.setPreferredSize(fieldSize);

        JTextArea descriptionArea = new JTextArea(
                book.getDescription() != null ? book.getDescription() : "", 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(150, 60));

        String[] statusOptions = {"AVAILABLE", "BORROWED", "RESERVED", "DAMAGED", "LOST"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(book.getStatus());
        statusCombo.setPreferredSize(fieldSize);

        JTextField locationField = new JTextField(
                book.getLocation() != null ? book.getLocation() : "", 20);
        locationField.setPreferredSize(fieldSize);

        JTextField copiesField = new JTextField(String.valueOf(book.getTotalCopies()), 20);
        copiesField.setPreferredSize(fieldSize);

        JTextField availableField = new JTextField(String.valueOf(book.getAvailableCopies()), 20);
        availableField.setPreferredSize(fieldSize);

        // Helper method to add a row
        BiConsumer<String, Component> addRow = (label, field) -> {
            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(new JLabel(label), gbc);
            gbc.gridx = 1;
            panel.add(field, gbc);
        };

        gbc.gridy = -1; // Start from row 0
        addRow.accept("ISBN:", isbnField);
        addRow.accept("Title:", titleField);
        addRow.accept("Author:", authorField);
        addRow.accept("Publisher:", publisherField);
        addRow.accept("Publication Year:", yearField);
        addRow.accept("Genre:", genreField);
        addRow.accept("Status:", statusCombo);
        addRow.accept("Location:", locationField);
        addRow.accept("Total Copies:", copiesField);
        addRow.accept("Available Copies:", availableField);
        addRow.accept("Description:", descScrollPane);

        int result = JOptionPane.showConfirmDialog(
                parentComponent,
                panel,
                "Edit Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                Book updatedBook = book.copy();
                updatedBook.setIsbn(isbnField.getText().trim());
                updatedBook.setTitle(titleField.getText().trim());
                updatedBook.setAuthor(authorField.getText().trim());
                updatedBook.setPublisher(publisherField.getText().trim());

                try {
                    if (!yearField.getText().trim().isEmpty()) {
                        updatedBook.setPublicationYear(Integer.parseInt(yearField.getText().trim()));
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Invalid year format: " + yearField.getText());
                }

                updatedBook.setGenre(genreField.getText().trim());
                updatedBook.setDescription(descriptionArea.getText().trim());
                updatedBook.setStatus((String) statusCombo.getSelectedItem());
                updatedBook.setLocation(locationField.getText().trim());

                try {
                    updatedBook.setTotalCopies(Integer.parseInt(copiesField.getText().trim()));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid total copies format: " + copiesField.getText());
                }

                try {
                    updatedBook.setAvailableCopies(Integer.parseInt(availableField.getText().trim()));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid available copies format: " + availableField.getText());
                }

                return updatedBook;
            } catch (Exception e) {
                logger.error("Error updating book from dialog input", e);
                showErrorDialog(
                        parentComponent,
                        "Error updating book: " + e.getMessage(),
                        "Edit Book Error"
                );
            }
        }

        return null;
    }

    

    public Member showAddMemberDialog(Component parentComponent) {
        logger.info("Showing add member dialog");
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextArea addressArea = new JTextArea(3, 20);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        
        String[] roleOptions = {"REGULAR", "PREMIUM", "ADMIN"};
        JComboBox<String> roleCombo = new JComboBox<>(roleOptions);
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Address:"));
        panel.add(addressScrollPane);
        
        int result = JOptionPane.showConfirmDialog(
            parentComponent,
            panel,
            "Add New Member",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Member member = new Member();
                member.setName(nameField.getText().trim());
                member.setEmail(emailField.getText().trim());
                member.setPhone(phoneField.getText().trim());
                member.setAddress(addressArea.getText().trim());
                member.setRole((String) roleCombo.getSelectedItem());
                member.setJoinDate(new Date());
                member.setStatus("ACTIVE");
                
                return member;
            } catch (Exception e) {
                logger.error("Error creating member from dialog input", e);
                showErrorDialog(
                    parentComponent,
                    "Error creating member: " + e.getMessage(),
                    "Add Member Error"
                );
            }
        }
        
        return null;
    }
    

    public Member showEditMemberDialog(Component parentComponent, Member member) {
        logger.info("Showing edit member dialog for: " + member.getName());
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField nameField = new JTextField(member.getName(), 20);
        JTextField emailField = new JTextField(member.getEmail() != null ? member.getEmail() : "", 20);
        JTextField phoneField = new JTextField(member.getPhone() != null ? member.getPhone() : "", 20);
        JTextArea addressArea = new JTextArea(
            member.getAddress() != null ? member.getAddress() : "", 3, 20);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        
        String[] roleOptions = {"REGULAR", "PREMIUM", "ADMIN"};
        JComboBox<String> roleCombo = new JComboBox<>(roleOptions);
        roleCombo.setSelectedItem(member.getRole());
        
        String[] statusOptions = {"ACTIVE", "INACTIVE", "SUSPENDED"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(member.getStatus());
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        panel.add(new JLabel("Address:"));
        panel.add(addressScrollPane);
        
        int result = JOptionPane.showConfirmDialog(
            parentComponent,
            panel,
            "Edit Member",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Member updatedMember = member.copy();
                updatedMember.setName(nameField.getText().trim());
                updatedMember.setEmail(emailField.getText().trim());
                updatedMember.setPhone(phoneField.getText().trim());
                updatedMember.setAddress(addressArea.getText().trim());
                updatedMember.setRole((String) roleCombo.getSelectedItem());
                updatedMember.setStatus((String) statusCombo.getSelectedItem());
                
                return updatedMember;
            } catch (Exception e) {
                logger.error("Error updating member from dialog input", e);
                showErrorDialog(
                    parentComponent,
                    "Error updating member: " + e.getMessage(),
                    "Edit Member Error"
                );
            }
        }
        
        return null;
    }
    

    public void showMemberHistoryDialog(Component parentComponent, int memberId, String memberName) {
        logger.info("Showing member history dialog for: " + memberName + " (ID: " + memberId + ")");
        
        // This would typically fetch transaction history from a service
        // For now, we'll just show a placeholder message
        
        JOptionPane.showMessageDialog(
            parentComponent,
            "This feature would show the borrowing history for " + memberName + " (ID: " + memberId + ").",
            "Member History",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}