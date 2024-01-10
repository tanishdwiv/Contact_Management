import java.sql.*;
import java.util.Scanner;

public class ContactManagement {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/contact_manager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456789";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            createTablesIfNotExists(connection);

            Scanner scanner = new Scanner(System.in);
            int choice;
            do {
                System.out.println("1. Add Contact");
                System.out.println("2. Edit Contact");
                System.out.println("3. Search Contact");
                System.out.println("4. Delete Contact");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        addContact(connection);
                        break;
                    case 2:
                        editContact(connection);
                        break;
                    case 3:
                        searchContact(connection);
                        break;
                    case 4:
                        deleteContact(connection);
                        break;
                }
            } while (choice != 5);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExists(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS contact_manager");
            statement.executeUpdate("USE contact_manager");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(255) NOT NULL)");

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS contacts (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255)," +
                    "phone VARCHAR(20)," +
                    "category_id INT," +
                    "FOREIGN KEY (category_id) REFERENCES categories(id))");
        }
    }

    private static void addContact(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter contact name: ");
        String name = scanner.nextLine();

        System.out.print("Enter contact email: ");
        String email = scanner.nextLine();

        System.out.print("Enter contact phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine();

        int categoryId = getOrCreateCategoryId(connection, categoryName);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO contacts (name, email, phone, category_id) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setInt(4, categoryId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contact added successfully!");
            } else {
                System.out.println("Failed to add contact.");
            }
        }
    }

    private static void editContact(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option to edit contact:");
        System.out.println("1. Edit by contact ID");
        System.out.println("2. Edit by contact name");
        System.out.print("Enter your choice: ");
        int editOption = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (editOption) {
            case 1:
                editContactByID(connection);
                break;
            case 2:
                editContactByName(connection);
                break;
            default:
                System.out.println("Invalid option. Returning to main menu.");
        }
    }

    private static void editContactByID(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter contact ID to edit: ");
        int contactId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        editContactDetails(connection, contactId);
    }

    private static void editContactByName(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter contact name to edit: ");
        String contactName = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM contacts WHERE name = ?")) {
            preparedStatement.setString(1, contactName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int contactId = resultSet.getInt("id");
                editContactDetails(connection, contactId);
            } else {
                System.out.println("Contact not found. Returning to main menu.");
            }
        }
    }

    private static void editContactDetails(Connection connection, int contactId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter new contact name: ");
        String name = scanner.nextLine();

        System.out.print("Enter new contact email: ");
        String email = scanner.nextLine();

        System.out.print("Enter new contact phone: ");
        String phone = scanner.nextLine();

        System.out.print("Enter new category name: ");
        String categoryName = scanner.nextLine();

        int categoryId = getOrCreateCategoryId(connection, categoryName);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE contacts SET name = ?, email = ?, phone = ?, category_id = ? WHERE id = ?")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setInt(4, categoryId);
            preparedStatement.setInt(5, contactId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contact updated successfully!");
            } else {
                System.out.println("Failed to update contact. Contact ID might not exist.");
            }
        }
    }

    private static void searchContact(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT c.id, c.name, c.email, c.phone, cat.name as category FROM contacts c " +
                        "JOIN categories cat ON c.category_id = cat.id " +
                        "WHERE c.name LIKE ? OR c.email LIKE ? OR c.phone LIKE ? OR cat.name LIKE ?")) {
            preparedStatement.setString(1, "%" + searchTerm + "%");
            preparedStatement.setString(2, "%" + searchTerm + "%");
            preparedStatement.setString(3, "%" + searchTerm + "%");
            preparedStatement.setString(4, "%" + searchTerm + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Phone: " + resultSet.getString("phone"));
                System.out.println("Category: " + resultSet.getString("category"));
                System.out.println("---------------------------");
            }
        }
    }
    private static void deleteContact(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter contact name to delete: ");
        String contactName = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM contacts WHERE name = ?")) {
            preparedStatement.setString(1, contactName);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contact deleted successfully!");
            } else {
                System.out.println("Failed to delete contact. Contact name might not exist.");
            }
        }
    }


    private static int getOrCreateCategoryId(Connection connection, String categoryName) throws SQLException {
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
}
