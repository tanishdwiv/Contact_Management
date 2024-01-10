import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.awt.GridLayout;
import java.awt.Dimension;




public class Contact extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/contact_manager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456789";

    private Connection connection;

    public Contact(Connection connection) {
        this.connection = connection;

        initComponents();
        createAndShowGUI();
    }

    private void initComponents() {
        setTitle("Contact Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton addContactButton = new JButton("Add Contact");
        JButton editContactButton = new JButton("Edit Contact");
        JButton searchContactButton = new JButton("Search Contact");
        JButton deleteContactButton = new JButton("Delete Contact");
        JButton listAllContactsButton = new JButton("List All Contacts");
        JButton exitButton = new JButton("Exit");

        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Other button actions...
        editContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    editContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        searchContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    searchContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        deleteContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteContact();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        listAllContactsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    listAllContacts();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeConnection();
                System.exit(0);
            }
        });








        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(addContactButton)
                        .addComponent(editContactButton)
                        .addComponent(searchContactButton)
                        .addComponent(deleteContactButton)
                        .addComponent(listAllContactsButton)
                        .addComponent(exitButton))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(addContactButton)
                .addComponent(editContactButton)
                .addComponent(searchContactButton)
                .addComponent(deleteContactButton)
                .addComponent(listAllContactsButton)
                .addComponent(exitButton)
        );

        pack();
    }

    private void createAndShowGUI() {
        setLocationRelativeTo(null);
        setVisible(true);
    }


    // ... (Existing code)

    private void addContact() throws SQLException {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField categoryField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Category Name:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Contact", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String categoryName = categoryField.getText();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int categoryId = getOrCreateCategoryId(connection, categoryName);

            // Check if a contact with the same name and email already exists
            if (isDuplicateContact(name, email)) {
                int option = JOptionPane.showConfirmDialog(null,
                        "A contact with the same name and email already exists. Do you want to add it anyway?",
                        "Duplicate Contact",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Contact not added.");
                    return;
                }
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO contacts (name, email, phone, category_id, timestamp) VALUES (?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, phone);
                preparedStatement.setInt(4, categoryId);
                preparedStatement.setTimestamp(5, getCurrentTimestamp());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Contact added successfully!");

                    // Insert into contact_history table
                    try (PreparedStatement historyStatement = connection.prepareStatement(
                            "INSERT INTO contact_history (contact_name, timestamp) VALUES (?, ?)")) {
                        historyStatement.setString(1, name);
                        historyStatement.setTimestamp(2, getCurrentTimestamp());
                        historyStatement.executeUpdate();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add contact.");
                }
            }
        }

    }

// ... (Existing code)

    // Other methods...
    private Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }
    private boolean isDuplicateContact(String name, String email) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM contacts WHERE name = ? AND email = ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
        return false;
    }
    private void editContact() throws SQLException {
        String[] options = {"Edit by contact ID", "Edit by contact name"};
        int editOption = JOptionPane.showOptionDialog(null, "Choose an option to edit contact:", "Edit Contact", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (editOption) {
            case 0:
                editContactByID();
                break;
            case 1:
                editContactByName();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid option. Returning to main menu.");
        }
    }
    private void editContactByID() throws SQLException {
        String contactIdString = JOptionPane.showInputDialog("Enter contact ID to edit:");
        if (contactIdString != null && !contactIdString.isEmpty()) {
            int contactId = Integer.parseInt(contactIdString);
            editContactDetails(contactId);
        }
    }


    private void editContactDetails(int contactId) throws SQLException {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField categoryField = new JTextField();

        panel.add(new JLabel("Enter new contact name (compulsory):"));
        panel.add(nameField);
        panel.add(new JLabel("Enter new contact phone (compulsory):"));
        panel.add(phoneField);
        panel.add(new JLabel("Enter new contact email:"));
        panel.add(emailField);
        panel.add(new JLabel("Enter new category name:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Contact", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String categoryName = categoryField.getText();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name and Phone are compulsory fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int categoryId = getOrCreateCategoryId(connection, categoryName);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE contacts SET name = ?, phone = ?, email = ?, category_id = ? WHERE id = ?")) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, phone);
                preparedStatement.setString(3, email);
                preparedStatement.setInt(4, categoryId);
                preparedStatement.setInt(5, contactId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Contact updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update contact. Contact ID might not exist.");
                }
            }
        }
    }

    private void editContactByName() throws SQLException {
        String contactName = JOptionPane.showInputDialog("Enter contact name to edit:");
        if (contactName != null && !contactName.isEmpty()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, phone, email FROM contacts WHERE name = ?")) {
                preparedStatement.setString(1, contactName);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int contactId = resultSet.getInt("id");
                    String currentPhone = resultSet.getString("phone");
                    String currentEmail = resultSet.getString("email");

                    editContactDetails(contactId, contactName, currentPhone, currentEmail);
                } else {
                    JOptionPane.showMessageDialog(null, "Contact not found. Returning to the main menu.");
                }
            }
        }
    }

    private void editContactDetails(int contactId, String currentName, String currentPhone, String currentEmail) throws SQLException {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField nameField = new JTextField(currentName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField emailField = new JTextField(currentEmail);
        JTextField categoryField = new JTextField();

        panel.add(new JLabel("Enter new contact name (compulsory):"));
        panel.add(nameField);
        panel.add(new JLabel("Enter new contact phone (compulsory):"));
        panel.add(phoneField);
        panel.add(new JLabel("Enter new contact email:"));
        panel.add(emailField);
        panel.add(new JLabel("Enter new category name:"));
        panel.add(categoryField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Contact", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String categoryName = categoryField.getText();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name and Phone are compulsory fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int categoryId = getOrCreateCategoryId(connection, categoryName);

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE contacts SET name = ?, phone = ?, email = ?, category_id = ? WHERE id = ?")) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, phone);
                preparedStatement.setString(3, email);
                preparedStatement.setInt(4, categoryId);
                preparedStatement.setInt(5, contactId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Contact updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update contact. Contact ID might not exist.");
                }
            }
        }
    }




    private void searchContact() throws SQLException {
        String searchTerm = JOptionPane.showInputDialog("Enter search term:");
        if (searchTerm != null && !searchTerm.isEmpty()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT c.id, c.name, c.email, c.phone, cat.name as category FROM contacts c " +
                            "JOIN categories cat ON c.category_id = cat.id " +
                            "WHERE c.name LIKE ? OR c.email LIKE ? OR c.phone LIKE ? OR cat.name LIKE ?")) {
                preparedStatement.setString(1, "%" + searchTerm + "%");
                preparedStatement.setString(2, "%" + searchTerm + "%");
                preparedStatement.setString(3, "%" + searchTerm + "%");
                preparedStatement.setString(4, "%" + searchTerm + "%");

                ResultSet resultSet = preparedStatement.executeQuery();

                StringBuilder resultMessage = new StringBuilder();
                while (resultSet.next()) {
                    resultMessage.append("ID: ").append(resultSet.getInt("id")).append("\n");
                    resultMessage.append("Name: ").append(resultSet.getString("name")).append("\n");
                    resultMessage.append("Email: ").append(resultSet.getString("email")).append("\n");
                    resultMessage.append("Phone: ").append(resultSet.getString("phone")).append("\n");
                    resultMessage.append("Category: ").append(resultSet.getString("category")).append("\n---------------------------\n");
                }

                if (resultMessage.length() > 0) {
                    JOptionPane.showMessageDialog(null, resultMessage.toString());
                } else {
                    JOptionPane.showMessageDialog(null, "No contacts found for the search term: " + searchTerm);
                }
            }
        }
    }


    private void deleteContact() throws SQLException {
        String contactName = JOptionPane.showInputDialog("Enter contact name to delete:");
        if (contactName != null && !contactName.isEmpty()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM contacts WHERE name = ?")) {
                preparedStatement.setString(1, contactName);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Contact deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete contact. Contact name might not exist.");
                }
            }
        }
    }
    private void listAllContacts() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT c.id, c.name, c.email, c.phone, cat.name as category FROM contacts c " +
                        "JOIN categories cat ON c.category_id = cat.id")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);

            while (resultSet.next()) {
                textArea.append("ID: " + resultSet.getInt("id") + "\n");
                textArea.append("Name: " + resultSet.getString("name") + "\n");
                textArea.append("Email: " + resultSet.getString("email") + "\n");
                textArea.append("Phone: " + resultSet.getString("phone") + "\n");
                textArea.append("Category: " + resultSet.getString("category") + "\n---------------------------\n");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(null, scrollPane, "All Contacts", JOptionPane.PLAIN_MESSAGE);
        }
    }





    private int getOrCreateCategoryId(Connection connection, String categoryName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT IGNORE INTO categories (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, categoryName);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                // Category already exists, fetch its ID
                try (PreparedStatement selectStatement = connection.prepareStatement(
                        "SELECT id FROM categories WHERE name = ?")) {
                    selectStatement.setString(1, categoryName);
                    ResultSet resultSet = selectStatement.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        }
        return -1;
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            Contact contactApp = new Contact(connection);
            contactApp.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    contactApp.closeConnection();
                    System.exit(0);
                }
            });
        } else {
            System.out.println("Failed to establish a database connection.");
        }
    }
}