package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LoginPage extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Quiz Application - Login");
        primaryStage.setFullScreenExitHint(""); // Disable the hint
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key for exiting full-screen
        setupUI();
    }

    private void setupUI() {
        // Load the image from the Images folder
        Image image = new Image(getClass().getResourceAsStream("/images/Quiz-Graphic-for-DUL-Mag-Blog-scaled.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());
        imageView.setPreserveRatio(false);
        imageView.setOpacity(0.8); // Increase image opacity to make it more visible

        // Add a dark overlay to the image
        Rectangle overlay = new Rectangle();
        overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4)); // Light overlay with 40% opacity
        overlay.widthProperty().bind(primaryStage.widthProperty());
        overlay.heightProperty().bind(primaryStage.heightProperty());

        // Create a VBox for the login form
        VBox loginForm = new VBox(15); // Increased spacing
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(20));
        loginForm.getStyleClass().add("vbox");

        Label titleLabel = new Label("Quiz Application");
        titleLabel.getStyleClass().add("title");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("text-field");
        usernameField.setMaxWidth(300);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("password-field");
        passwordField.setMaxWidth(300);

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Enter your password");
        visiblePasswordField.getStyleClass().add("text-field");
        visiblePasswordField.setMaxWidth(300);
        visiblePasswordField.setVisible(false); // Initially hidden

        // StackPane to hold passwordField and visiblePasswordField
        StackPane passwordContainer = new StackPane();
        passwordContainer.getChildren().addAll(passwordField, visiblePasswordField);

        // Show Password Checkbox
        CheckBox showPasswordCheckbox = new CheckBox("Show Password");
        showPasswordCheckbox.getStyleClass().add("checkbox");
        showPasswordCheckbox.setOnAction(e -> {
            if (showPasswordCheckbox.isSelected()) {
                // Show the password in plain text
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                // Hide the password
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
            }
        });

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
            User user = new User();
            int userId = user.login(username, password);
            if (userId != -1) {
                System.out.println("Login successful!");
                if (user.getRole().equals("admin")) {
                    AdminPage adminPage = new AdminPage(primaryStage);
                    adminPage.show();
                } else {
                    QuizApp quizApp = new QuizApp(primaryStage, userId);
                    quizApp.show();
                }
            } else {
                System.out.println("Invalid credentials!");
                showAlert("Invalid username or password. Please try again.");
            }
        });

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");
        registerButton.setOnAction(e -> {
            RegistrationPage registrationPage = new RegistrationPage(primaryStage);
            registrationPage.show();
        });

        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("button");
        exitButton.setOnAction(e -> primaryStage.close());

        // Add components to the login form
        loginForm.getChildren().addAll(
            titleLabel, usernameLabel, usernameField, passwordLabel, passwordContainer,
            showPasswordCheckbox, loginButton, registerButton, exitButton
        );

        // Create a StackPane to layer the image, overlay, and login form
        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, overlay, loginForm);

        // Set up the scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("login-registration.css").toExternalForm()); // Use the new CSS file
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    
    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}