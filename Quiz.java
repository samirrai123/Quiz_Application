package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Stage primaryStage;
    private int userId;
    private int quizId;
    private int timeLeft = 60; // Time limit in seconds
    private Label timeLabel;

    public Quiz(Stage primaryStage, int userId, int quizId) {
        this.primaryStage = primaryStage;
        this.userId = userId;
        this.quizId = quizId;
        questions = new ArrayList<>();
        loadQuestionsFromDatabase();
    }

    private void loadQuestionsFromDatabase() {
        String query = "SELECT question_text, option1, option2, option3, option4, correct_option FROM questions WHERE quiz_id = ?";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String questionText = rs.getString("question_text");
                String[] options = {
                    rs.getString("option1"),
                    rs.getString("option2"),
                    rs.getString("option3"),
                    rs.getString("option4")
                };
                int correctOption = rs.getInt("correct_option");
                questions.add(new Question(questionText, options, correctOption - 1)); // Convert to 0-based index
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to load questions from the database.");
        }
    }

    public void startQuiz() {
        primaryStage.setFullScreenExitHint(""); // Disable the hint
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key for exiting full-screen

        if (questions.isEmpty()) {
            showAlert("No questions available for this quiz.");
            return;
        }
        startTimer();
        showNextQuestion();
    }

    private void startTimer() {
        timeLabel = new Label("Time Left: " + timeLeft);
        timeLabel.getStyleClass().add("time-label");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timeLabel.setText("Time Left: " + timeLeft);
            if (timeLeft <= 0) {
                endQuiz();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void showNextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            endQuiz();
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);

        // Create a StackPane to layer the image and the quiz interface
        StackPane root = new StackPane();

        // Load the image from the resources folder
        Image image = new Image(getClass().getResourceAsStream("/images/26768.jpg")); // Update the image path
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

        // Create a VBox for the quiz interface
        VBox quizInterface = new VBox(10); // Spacing between components
        quizInterface.setAlignment(Pos.CENTER); // Center everything in the VBox
        quizInterface.setPadding(new Insets(20));
        quizInterface.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);"); // Semi-transparent white background

        // Timer Label
        timeLabel = new Label("Time Left: " + timeLeft);
        timeLabel.getStyleClass().add("time-label");

        // Question Number Label
        Label questionNumberLabel = new Label((currentQuestionIndex + 1) + " of " + questions.size() + " Questions");
        questionNumberLabel.getStyleClass().add("question-number-label");

        // Question Text Label
        Label questionLabel = new Label(currentQuestion.getQuestionText());
        questionLabel.getStyleClass().add("question-label");

        // Radio Buttons for Options
        ToggleGroup optionsGroup = new ToggleGroup();
        VBox optionsContainer = new VBox(5); // Container for the options
        optionsContainer.setAlignment(Pos.CENTER); // Center the options horizontally and vertically

        for (int i = 0; i < currentQuestion.getOptions().length; i++) {
            RadioButton optionButton = new RadioButton(currentQuestion.getOptions()[i]);
            optionButton.setToggleGroup(optionsGroup);
            optionButton.getStyleClass().add("radio-button");
            optionsContainer.getChildren().add(optionButton); // Add options to the container
        }

        // Next Button
        Button nextButton = new Button("Next");
        nextButton.getStyleClass().add("button");
        nextButton.setOnAction(e -> {
            RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
            if (selectedOption != null) {
                int selectedIndex = optionsContainer.getChildren().indexOf(selectedOption);
                if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
                    score++; // Increment score if the answer is correct
                }
                currentQuestionIndex++;
                showNextQuestion(); // Move to the next question
            } else {
                showAlert("Please select an option!");
            }
        });

        // Add components to the quiz interface
        quizInterface.getChildren().addAll(
            timeLabel,              // Timer at the top
            questionNumberLabel,    // Question number below the timer
            questionLabel,          // Question text below the question number
            optionsContainer,       // Options container (centered)
            nextButton              // Next button at the bottom
        );

        // Add the image, overlay, and quiz interface to the StackPane
        root.getChildren().addAll(imageView, overlay, quizInterface);

        // Set up the scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("quiz.css").toExternalForm()); // Use the new CSS file
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    
    private void endQuiz() {
        Result result = new Result();
        result.saveResult(userId, quizId, score);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label scoreLabel = new Label("Quiz Completed! Your Score: " + score + "/" + questions.size());
        scoreLabel.getStyleClass().add("title");

        Button retryButton = new Button("Retry Quiz");
        retryButton.getStyleClass().add("button");
        retryButton.setOnAction(e -> {
            currentQuestionIndex = 0;
            score = 0;
            startQuiz();
        });

        Button backButton = new Button("Back to Dashboard");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            QuizApp quizApp = new QuizApp(primaryStage, userId);
            quizApp.show();
        });

        layout.getChildren().addAll(scoreLabel, retryButton, backButton);

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}