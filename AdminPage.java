package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class AdminPage {
    private Stage primaryStage;

    public AdminPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setFullScreenExitHint(""); // Disable the hint
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key for exiting full-screen

        // Create a StackPane to layer the image and the interface
        StackPane root = new StackPane();

        // Load the image from the resources folder
        Image image = new Image(getClass().getResourceAsStream("/images/shutterstock_749036344.jpg"
        		+ "")); // Update the image path
        ImageView imageView = new ImageView(image);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty()); // Make the image full-screen width
        imageView.fitHeightProperty().bind(primaryStage.heightProperty()); // Make the image full-screen height
        imageView.setPreserveRatio(false); // Stretch the image to fill the screen
        imageView.setOpacity(0.9); // Increase image opacity to make it more visible

        // Add a dark overlay to the image
        Rectangle overlay = new Rectangle();
        overlay.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.4)); // Dark overlay with 40% opacity
        overlay.widthProperty().bind(primaryStage.widthProperty());
        overlay.heightProperty().bind(primaryStage.heightProperty());

        // Create a VBox for the interface
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.getStyleClass().add("title");

        Button createQuizButton = new Button("Create Quiz");
        createQuizButton.getStyleClass().add("button");
        createQuizButton.setOnAction(e -> {
            CreateQuizPage createQuizPage = new CreateQuizPage(primaryStage);
            createQuizPage.show();
        });

        Button deleteQuizButton = new Button("Delete Quiz");
        deleteQuizButton.getStyleClass().add("button");
        deleteQuizButton.setOnAction(e -> showDeleteQuizWindow());

        Button viewAnalyticsButton = new Button("View Analytics");
        viewAnalyticsButton.getStyleClass().add("button");
        viewAnalyticsButton.setOnAction(e -> showAnalyticsWindow());

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        // Add components to the interface
        layout.getChildren().addAll(
            titleLabel, createQuizButton, deleteQuizButton, viewAnalyticsButton, backButton
        );

        // Add the image, overlay, and interface to the StackPane
        root.getChildren().addAll(imageView, overlay, layout);

        // Set up the scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("adminpage.css").toExternalForm()); // Use the new CSS file
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void showDeleteQuizWindow() {
        List<QuizInfo> quizzes = getAvailableQuizzes();
        if (quizzes.isEmpty()) {
            showAlert("No quizzes available to delete.");
            return;
        }

        ComboBox<String> topicComboBox = new ComboBox<>();
        ObservableList<String> topics = FXCollections.observableArrayList();
        for (QuizInfo quiz : quizzes) {
            topics.add(quiz.getTitle() + " - " + quiz.getCategory());
        }
        topicComboBox.setItems(topics);
        topicComboBox.setPromptText("Select a quiz to delete");

        Button submitButton = new Button("Delete");
        submitButton.getStyleClass().add("button");
        submitButton.setOnAction(event -> {
            String selectedTopic = topicComboBox.getValue();
            if (selectedTopic == null) {
                showAlert("Please select a quiz to delete.");
                return;
            }

            int quizId = -1;
            for (QuizInfo quiz : quizzes) {
                if ((quiz.getTitle() + " - " + quiz.getCategory()).equals(selectedTopic)) {
                    quizId = quiz.getQuizId();
                    break;
                }
            }

            if (quizId != -1) {
                Admin admin = new Admin();
                if (admin.deleteQuiz(quizId)) {
                    showAlert("Quiz deleted successfully!");
                } else {
                    showAlert("Failed to delete quiz. Please try again.");
                }
            } else {
                showAlert("Invalid selection. Please try again.");
            }
        });

        Button backButton = new Button("Back to Admin Dashboard");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(event -> ((Stage) backButton.getScene().getWindow()).close());

        VBox promptLayout = new VBox(20);
        promptLayout.setAlignment(Pos.CENTER);
        promptLayout.setPadding(new Insets(20));
        promptLayout.getStyleClass().add("vbox");

        Label promptTitle = new Label("Delete Quiz");
        promptTitle.getStyleClass().add("title");

        Label instructionLabel = new Label("Select a quiz to delete:");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555555;");

        promptLayout.getChildren().addAll(promptTitle, instructionLabel, topicComboBox, submitButton, backButton);

        Scene promptScene = new Scene(promptLayout, 600, 400);
        promptScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        Stage promptStage = new Stage();
        promptStage.setScene(promptScene);
        promptStage.setFullScreen(true);
        promptStage.show();
    }

    private void showAnalyticsWindow() {
        ObservableList<AnalyticsResult> results = fetchAnalyticsData();
        if (results.isEmpty()) {
            showAlert("No analytics data found.");
            return;
        }

        TableView<AnalyticsResult> tableView = new TableView<>();

        TableColumn<AnalyticsResult, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<AnalyticsResult, String> topicColumn = new TableColumn<>("Topic");
        topicColumn.setCellValueFactory(new PropertyValueFactory<>("topic"));

        TableColumn<AnalyticsResult, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        TableColumn<AnalyticsResult, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        tableView.getColumns().addAll(nameColumn, topicColumn, scoreColumn, timestampColumn);
        tableView.setItems(results);

        Button backButton = new Button("Back to Admin Dashboard");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(event -> ((Stage) backButton.getScene().getWindow()).close());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        Label promptTitle = new Label("Quiz Analytics");
        promptTitle.getStyleClass().add("title");

        layout.getChildren().addAll(promptTitle, tableView, backButton);

        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Quiz Analytics");
        stage.show();
    }

    private ObservableList<AnalyticsResult> fetchAnalyticsData() {
        ObservableList<AnalyticsResult> results = FXCollections.observableArrayList();
        String query = "SELECT u.username AS name, q.title AS topic, r.score, r.timestamp " +
                      "FROM results r " +
                      "JOIN users u ON r.user_id = u.user_id " +
                      "JOIN quizzes q ON r.quiz_id = q.quiz_id";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new AnalyticsResult(
                    rs.getString("name"),
                    rs.getString("topic"),
                    rs.getInt("score"),
                    rs.getTimestamp("timestamp").toString()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to fetch analytics data.");
        }
        return results;
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
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class AnalyticsResult {
        private String name;
        private String topic;
        private int score;
        private String timestamp;

        public AnalyticsResult(String name, String topic, int score, String timestamp) {
            this.name = name;
            this.topic = topic;
            this.score = score;
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public String getTopic() {
            return topic;
        }

        public int getScore() {
            return score;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}