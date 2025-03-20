package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizApp {
    private Stage primaryStage;
    private int userId;

    public QuizApp(Stage primaryStage, int userId) {
        this.primaryStage = primaryStage;
        this.userId = userId;
    }
    public void show() {
        primaryStage.setFullScreenExitHint(""); // Disable the hint
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key for exiting full-screen

        // Create a StackPane to layer the image and the interface
        StackPane root = new StackPane();

        // Load the image from the resources folder
        Image image = new Image(getClass().getResourceAsStream("/images/344201-PAOSF6-481.jpg")); // Update the image path
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty()); // Make the image full-screen width
        imageView.fitHeightProperty().bind(primaryStage.heightProperty()); // Make the image full-screen height
        imageView.setPreserveRatio(false); // Stretch the image to fill the screen
        imageView.setOpacity(0.9); // Increase image opacity to make it more visible

        // Add a light overlay to the image
        Rectangle overlay = new Rectangle();
        overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3)); // Light overlay with 30% opacity
        overlay.widthProperty().bind(primaryStage.widthProperty());
        overlay.heightProperty().bind(primaryStage.heightProperty());

        // Create a VBox for the interface
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        // Title Label
        Label titleLabel = new Label("Quiz Application");
        titleLabel.getStyleClass().add("title");

        // Buttons
        Button viewQuizzesButton = new Button("View Available Quizzes");
        viewQuizzesButton.getStyleClass().add("button");
        viewQuizzesButton.setOnAction(e -> showAvailableQuizzes());

        Button viewResultsButton = new Button("View My Results");
        viewResultsButton.getStyleClass().add("button");
        viewResultsButton.setOnAction(e -> {
            Result result = new Result();
            result.generateReport(userId);
        });

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        // Add components to the interface
        layout.getChildren().addAll(
            titleLabel,             // Title at the top
            viewQuizzesButton,      // Buttons below the title
            viewResultsButton,
            backButton
        );

        // Add the image, overlay, and interface to the StackPane
        root.getChildren().addAll(imageView, overlay, layout);

        // Set up the scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("quizapp.css").toExternalForm()); // Use the new CSS file
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    private void showAvailableQuizzes() {
        List<QuizInfo> quizzes = getAvailableQuizzes();
        if (quizzes.isEmpty()) {
            showAlert("No quizzes available.");
            return;
        }

        // Create a StackPane to layer the image and the interface
        StackPane root = new StackPane();

        // Load the image from the resources folder
        Image image = new Image(getClass().getResourceAsStream("/images/7426187.jpg")); // Update the image path
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty()); // Make the image full-screen width
        imageView.fitHeightProperty().bind(primaryStage.heightProperty()); // Make the image full-screen height
        imageView.setPreserveRatio(false); // Stretch the image to fill the screen
        imageView.setOpacity(0.9); // Increase image opacity to make it more visible

        // Add a light overlay to the image
        Rectangle overlay = new Rectangle();
        overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3)); // Light overlay with 30% opacity
        overlay.widthProperty().bind(primaryStage.widthProperty());
        overlay.heightProperty().bind(primaryStage.heightProperty());

        // Create a VBox for the interface
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        // Title Label (Added first to ensure it's at the top)
        Label titleLabel = new Label("Available Quizzes");
        titleLabel.getStyleClass().add("title");

        // Add the title to the layout first
        layout.getChildren().add(titleLabel);

        // Add buttons for each quiz
        for (QuizInfo quiz : quizzes) {
            Button quizButton = new Button(quiz.getTitle() + " - " + quiz.getCategory());
            quizButton.getStyleClass().add("button");
            quizButton.setOnAction(e -> {
                Quiz quizApp = new Quiz(primaryStage, userId, quiz.getQuizId());
                quizApp.startQuiz();
            });
            layout.getChildren().add(quizButton);
        }

        // Back Button (Added last to ensure it's at the bottom)
        Button backButton = new Button("Back to Dashboard");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> show());

        // Add the back button to the layout
        layout.getChildren().add(backButton);

        // Add the image, overlay, and interface to the StackPane
        root.getChildren().addAll(imageView, overlay, layout);

        // Set up the scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("showAvailableQuizzes.css").toExternalForm()); // Use the new CSS file
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    
    private List<QuizInfo> getAvailableQuizzes() {
        List<QuizInfo> quizzes = new ArrayList<>();
        String query = "SELECT quiz_id, title, category FROM quizzes";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quizzes.add(new QuizInfo(
                    rs.getInt("quiz_id"),
                    rs.getString("title"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to fetch quizzes from the database.");
        }
        return quizzes;
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

class QuizInfo {
    private int quizId;
    private String title;
    private String category;

    public QuizInfo(int quizId, String title, String category) {
        this.quizId = quizId;
        this.title = title;
        this.category = category;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }
}