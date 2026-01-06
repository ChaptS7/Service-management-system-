import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {

    private DatabaseManager databasemanager;

    // Dummy data for service options and their costs
    private final String[] services = {"Plumber", "Mechanic", "Electrician", "Carpenter", "Driver", "Cleaner"};
    private final double[] serviceCosts = {50.0, 80.0, 70.0, 60.0, 100.0, 40.0};

    // Dummy data for past bookings
    private final String[] pastBookings = {"Booking 1", "Booking 2", "Booking 3"};

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }

    // Start method to create the GUI
    @Override
    public void start(Stage primaryStage) {
        // Connect to the SQLite database
        databasemanager = new DatabaseManager("service_management.db");

        primaryStage.setTitle("Service Management System");

        // Create login page
        VBox loginPage = createLoginPage(primaryStage);

        // Display login page
        Scene scene = new Scene(loginPage, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create login page
    private VBox createLoginPage(Stage primaryStage) {
        VBox loginPage = new VBox(10);
        loginPage.setAlignment(Pos.CENTER);
        loginPage.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            // Perform login authentication
            boolean isAuthenticated = databasemanager.authenticateUser(usernameField.getText(), passwordField.getText());
            if (isAuthenticated) {
                // If authenticated, move to service selection page
                VBox serviceSelectionPage = createServiceSelectionPage(primaryStage);
                Scene scene = new Scene(serviceSelectionPage, 400, 300);
                primaryStage.setScene(scene);
            } else {
                // If not authenticated, show error message
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(event -> {
            // Move to account creation page
            VBox accountCreationPage = createAccountCreationPage(primaryStage);
            Scene scene = new Scene(accountCreationPage, 400, 400);
            primaryStage.setScene(scene);
        });

        loginPage.getChildren().addAll(usernameField, passwordField, loginButton, createAccountButton);
        return loginPage;
    }

    // Method to create account creation page
    private VBox createAccountCreationPage(Stage primaryStage) {
        VBox accountCreationPage = new VBox(10);
        accountCreationPage.setAlignment(Pos.CENTER);
        accountCreationPage.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.setPromptText("Gender");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(event -> {
            // Perform account creation
            boolean isSuccess = databasemanager.createUser(nameField.getText(), usernameField.getText(),
                    passwordField.getText(), phoneField.getText(), emailField.getText(),
                    genderComboBox.getValue(), addressField.getText());
            if (isSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Account Created", "Account created successfully.");
                // Move back to login page
                VBox loginPage = createLoginPage(primaryStage);
                Scene scene = new Scene(loginPage, 400, 300);
                primaryStage.setScene(scene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Account Creation Failed", "Failed to create account. Please try again.");
            }
        });

        accountCreationPage.getChildren().addAll(nameField, usernameField, passwordField, phoneField,
                emailField, genderComboBox, addressField, createAccountButton);
        return accountCreationPage;
    }

    // Method to create service selection page
    private VBox createServiceSelectionPage(Stage primaryStage) {
        VBox serviceSelectionPage = new VBox(10);
        serviceSelectionPage.setAlignment(Pos.CENTER);
        serviceSelectionPage.setPadding(new Insets(20));

        Label serviceLabel = new Label("Select Service:");
        ComboBox<String> serviceComboBox = new ComboBox<>();
        serviceComboBox.getItems().addAll(services);
        serviceComboBox.setPromptText("Select Service");

        Label timingLabel = new Label("Select Timing:");
        TextField timingField = new TextField();
        timingField.setPromptText("Enter Timing");

        Button bookServiceButton = new Button("Book Service");
        bookServiceButton.setOnAction(event -> {
            // Perform booking
            String selectedService = serviceComboBox.getValue();
            String timing = timingField.getText();
            // Dummy logic for booking, replace with actual logic
            double cost = serviceCosts[serviceComboBox.getItems().indexOf(selectedService)];
            showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed", "Service: " + selectedService +
                    "\nTiming: " + timing + "\nCost: $" + cost);
            // Move to payment page
            VBox paymentPage = createPaymentPage(primaryStage);
            Scene scene = new Scene(paymentPage, 400, 300);
            primaryStage.setScene(scene);
        });

        serviceSelectionPage.getChildren().addAll(serviceLabel, serviceComboBox, timingLabel, timingField, bookServiceButton);
        return serviceSelectionPage;
    }

    // Method to create payment page
    private VBox createPaymentPage(Stage primaryStage) {
        // Implementation for payment page
        return new VBox();
    }

    // Method to create thank you page
    private VBox createThankYouPage(Stage primaryStage) {
        // Implementation for thank you page
        return new VBox();
    }

    // Method to display an alert
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String dbName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:D:\\assi 2\\sqlite-jdbc-3.45.2.0.jar" + dbName);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // Create user table
            String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "phone TEXT," +
                    "email TEXT," +
                    "gender TEXT," +
                    "address TEXT)";
            statement.execute(createUserTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(String name, String username, String password, String phone, String email, String gender, String address) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (name, username, password, phone, email, gender, address) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, phone);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, address);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}