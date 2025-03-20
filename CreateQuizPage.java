package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateQuizPage {
    private Stage primaryStage;

    public CreateQuizPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setFullScreenExitHint(""); // Disable the hint
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key for exiting full-screen

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        Label titleLabel = new Label("Create Quiz");
        titleLabel.getStyleClass().add("title");

        Label quizTitleLabel = new Label("Quiz Title:");
        TextField quizTitleField = new TextField();
        quizTitleField.setPromptText("Enter quiz title");
        quizTitleField.getStyleClass().add("text-field");

        Label categoryLabel = new Label("Category:");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Enter category");
        categoryField.getStyleClass().add("text-field");

        Label timeLimitLabel = new Label("Time Limit (minutes):");
        TextField timeLimitField = new TextField();
        timeLimitField.setPromptText("Enter time limit");
        timeLimitField.getStyleClass().add("text-field");

        Button createButton = new Button("Create Quiz and Add Questions");
        createButton.getStyleClass().add("button");
        createButton.setOnAction(e -> {
            String title = quizTitleField.getText();
            String category = categoryField.getText();
            int timeLimit = Integer.parseInt(timeLimitField.getText());

            Admin admin = new Admin();
            int quizId = admin.createQuizAndGetId(title, category, timeLimit);
            if (quizId != -1) {
                System.out.println("Quiz created successfully! Quiz ID: " + quizId);
                CreateQuestionPage createQuestionPage = new CreateQuestionPage(primaryStage, quizId);
                createQuestionPage.show();
            } else {
                System.out.println("Quiz creation failed!");
            }
        });

        Button backButton = new Button("Back to Admin Dashboard");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            AdminPage adminPage = new AdminPage(primaryStage);
            adminPage.show();
        });

        layout.getChildren().addAll(
            titleLabel, quizTitleLabel, quizTitleField, categoryLabel, categoryField,
            timeLimitLabel, timeLimitField, createButton, backButton
        );

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}