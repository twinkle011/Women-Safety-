package com.shesafe;

import com.shesafe.database.DatabaseConnection;
import com.shesafe.model.EmergencyContact;
import com.shesafe.model.User;
import com.shesafe.service.ContactService;
import com.shesafe.service.SessionManager;
import com.shesafe.service.ValidationException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Screen 5: EmergencyContactsScreen
 * Manages trusted emergency contacts for the authenticated user.
 * Phase 4: Supports phone numbers and guardian emails for automated SOS notifications.
 */
public class EmergencyContactsScreen extends JFrame {

    private User currentUser;
    private final ContactService contactService = new ContactService();

    private JTextField txtContactName;
    private JTextField txtContactPhone;
    private JComboBox<String> cmbRelationship;
    private JTable contactsTable;
    private DefaultTableModel tableModel;

    public EmergencyContactsScreen() {
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Emergency Contacts", 880, 620);
        initUI();
    }

    public EmergencyContactsScreen(User user) {
        if (user != null) {
            SessionManager.login(user);
        }
        if (!SessionManager.requireLogin(this)) {
            return;
        }
        this.currentUser = SessionManager.getCurrentUser();
        UITheme.configureFrame(this, "Emergency Contacts", 880, 620);
        initUI();
    }

    public EmergencyContactsScreen(String userName) {
        this(new User(1, userName, userName.toLowerCase().replace(" ", "") + "@shesafe.com", "9876543210", null, null));
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Header Banner
        JPanel header = UITheme.createHeaderBanner(
                "Emergency Contacts Directory (MySQL)",
                "Guardians for: " + currentUser.getFullName() + " (User ID #" + currentUser.getUserId() + ")"
        );

        // Content Area
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // LEFT: Add Contact Form Card
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(320, 420));

        JLabel formTitle = new JLabel("Add New Guardian");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.PURPLE_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        formCard.add(formTitle);

        // Contact Name
        JLabel lblName = new JLabel("Contact Name:");
        lblName.setFont(UITheme.FONT_BODY_BOLD);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtContactName = UITheme.createTextField(15);
        txtContactName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtContactName.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblName);
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(txtContactName);
        formCard.add(Box.createRigidArea(new Dimension(0, 12)));

        // Contact Phone / Email
        JLabel lblPhone = new JLabel("Phone Number or Email:");
        lblPhone.setFont(UITheme.FONT_BODY_BOLD);
        lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtContactPhone = UITheme.createTextField(15);
        txtContactPhone.setToolTipText("Enter a 10-digit mobile number, helpline (e.g. 1091), or guardian email");
        txtContactPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtContactPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblPhone);
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(txtContactPhone);
        formCard.add(Box.createRigidArea(new Dimension(0, 12)));

        // Relationship
        JLabel lblRel = new JLabel("Relationship:");
        lblRel.setFont(UITheme.FONT_BODY_BOLD);
        lblRel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] relationships = {"Parent / Guardian", "Sibling", "Spouse / Partner", "Close Friend", "Work Colleague", "Police / Helpline"};
        cmbRelationship = new JComboBox<>(relationships);
        cmbRelationship.setFont(UITheme.FONT_BODY);
        cmbRelationship.setBackground(Color.WHITE);
        cmbRelationship.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbRelationship.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblRel);
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(cmbRelationship);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add Button
        JButton btnAddContact = UITheme.createPrimaryButton("+ Add to Emergency Circle");
        btnAddContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAddContact.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAddContact.addActionListener(e -> handleAddContact());
        formCard.add(btnAddContact);

        formCard.add(Box.createVerticalGlue());

        // RIGHT: Contacts List Table & Action Bar
        JPanel rightPanel = new JPanel(new BorderLayout(0, 12));
        rightPanel.setOpaque(false);

        String[] columnNames = {"ID", "Name", "Contact (Phone / Email)", "Relationship"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        contactsTable = new JTable(tableModel);
        contactsTable.setFont(UITheme.FONT_BODY);
        contactsTable.setRowHeight(32);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsTable.setGridColor(UITheme.BORDER_COLOR);
        contactsTable.setShowGrid(true);

        JTableHeader tableHeader = contactsTable.getTableHeader();
        tableHeader.setFont(UITheme.FONT_BODY_BOLD);
        tableHeader.setBackground(UITheme.PURPLE_LIGHT);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        contactsTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        contactsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        contactsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        contactsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        loadContacts();

        JScrollPane scrollPane = new JScrollPane(contactsTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Action Buttons Bar (Refresh, View, Delete, Back)
        JPanel tableActionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tableActionBar.setOpaque(false);

        JButton btnRefresh = UITheme.createSecondaryButton("🔄 Refresh List");
        JButton btnView = UITheme.createSecondaryButton("🔍 View Details");
        JButton btnDelete = new JButton("🗑 Delete Contact");
        btnDelete.setFont(UITheme.FONT_BODY_BOLD);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setBackground(UITheme.SOS_RED);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.SOS_RED, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        JButton btnBack = UITheme.createBackButton();

        btnRefresh.addActionListener(e -> loadContacts());
        btnView.addActionListener(e -> handleViewContact());
        btnDelete.addActionListener(e -> handleDeleteContact());
        btnBack.addActionListener(e -> handleBack());

        tableActionBar.add(btnRefresh);
        tableActionBar.add(btnView);
        tableActionBar.add(btnDelete);
        tableActionBar.add(btnBack);

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(tableActionBar, BorderLayout.SOUTH);

        contentPanel.add(formCard, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void loadContacts() {
        tableModel.setRowCount(0);
        try {
            List<EmergencyContact> contacts = contactService.getContactsForCurrentUser();
            for (EmergencyContact contact : contacts) {
                tableModel.addRow(new Object[]{
                        contact.getContactId(),
                        contact.getContactName(),
                        contact.getContactPhone(),
                        contact.getRelationship()
                });
            }
        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Session Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Load Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleAddContact() {
        String name = txtContactName.getText();
        String phone = txtContactPhone.getText();
        String relationship = (String) cmbRelationship.getSelectedItem();

        try {
            EmergencyContact newContact = contactService.addContact(name, phone, relationship);

            JOptionPane.showMessageDialog(this,
                    "Emergency Contact saved successfully in MySQL!\n"
                    + "Name: " + newContact.getContactName() + " (Contact ID #" + newContact.getContactId() + ")\n"
                    + "Bound to: " + currentUser.getFullName() + " (User #" + currentUser.getUserId() + ")",
                    "Contact Saved",
                    JOptionPane.INFORMATION_MESSAGE);

            txtContactName.setText("");
            txtContactPhone.setText("");
            cmbRelationship.setSelectedIndex(0);

            loadContacts();

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException se) {
            JOptionPane.showMessageDialog(this,
                    DatabaseConnection.getConnectionErrorMessage(se),
                    "Database Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleViewContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a contact row from the table.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int contactId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String name = tableModel.getValueAt(selectedRow, 1).toString();
        String phone = tableModel.getValueAt(selectedRow, 2).toString();
        String rel = tableModel.getValueAt(selectedRow, 3).toString();

        String details = "EMERGENCY CONTACT DETAILS (MySQL):\n\n"
                + "• Contact ID: #" + contactId + "\n"
                + "• Guardian Name: " + name + "\n"
                + "• Phone / Email: " + phone + "\n"
                + "• Relationship: " + rel + "\n"
                + "• Account Owner: " + currentUser.getFullName() + " (User #" + currentUser.getUserId() + ")\n"
                + "• Storage: shesafe_db.emergency_contacts";

        JOptionPane.showMessageDialog(this, details, "Contact Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDeleteContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a contact from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int contactId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String name = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + name + "' (Contact ID #" + contactId + ") from MySQL?",
                "Confirm Contact Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = contactService.deleteContact(contactId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this,
                            "Contact '" + name + "' was deleted from database.",
                            "Contact Deleted",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadContacts();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete contact.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ValidationException ve) {
                JOptionPane.showMessageDialog(this, ve.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException se) {
                JOptionPane.showMessageDialog(this,
                        DatabaseConnection.getConnectionErrorMessage(se),
                        "Database Delete Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleBack() {
        new DashboardScreen().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmergencyContactsScreen("Priya Sharma").setVisible(true));
    }
}
