package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RegistrationPage {
    private Stage primaryStage;

    public RegistrationPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

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

        // Create a VBox for the registration form
        VBox registrationForm = new VBox(15); // Increased spacing
        registrationForm.setAlignment(Pos.CENTER);
        registrationForm.setPadding(new Insets(20));
        registrationForm.getStyleClass().add("vbox");

        Label titleLabel = new Label("Register");
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

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.getStyleClass().add("password-field");
        confirmPasswordField.setMaxWidth(300);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.getStyleClass().add("text-field");
        emailField.setMaxWidth(300);

        Label roleLabel = new Label("Role:");
        ToggleGroup roleGroup = new ToggleGroup();
        HBox roleContainer = new HBox(10); // Container for role buttons
        roleContainer.setAlignment(Pos.CENTER);

        RadioButton studentButton = new RadioButton("Student");
        studentButton.setToggleGroup(roleGroup);
        studentButton.setSelected(true); // Default selection
        studentButton.getStyleClass().add("radio-button");

        RadioButton adminButton = new RadioButton("Admin");
        adminButton.setToggleGroup(roleGroup);
        adminButton.getStyleClass().add("radio-button");

        roleContainer.getChildren().addAll(studentButton, adminButton);

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String email = emailField.getText();
            String role = studentButton.isSelected() ? "student" : "admin";

            if (password.equals(confirmPassword)) {
                User user = new User();
                if (user.register(username, password, email, role)) {
                    System.out.println("Registration successful!");

                    // Add a success notification
                    javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Registration Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Your registration was successful!");
                    successAlert.showAndWait();

                    LoginPage loginPage = new LoginPage();
                    loginPage.start(primaryStage);
                } else {
                    System.out.println("Registration failed!");
                    showAlert("Registration failed. Please try again.");
                }
            } else {
                showAlert("Passwords do not match. Please try again.");
            }
        });

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        // Add components to the registration form
        registrationForm.getChildren().addAll(
            titleLabel, usernameLabel, usernameField, passwordLabel, passwordContainer,
            showPasswordCheckbox, confirmPasswordLabel, confirmPasswordField, emailLabel, emailField,
            roleLabel, roleContainer, registerButton, backButton
        );

        // Create a StackPane to layer the image, overlay, and registration form
        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, overlay, registrationForm);

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