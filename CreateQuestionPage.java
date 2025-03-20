package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateQuestionPage {
    private Stage primaryStage;
    private int quizId;

    public CreateQuestionPage(Stage primaryStage, int quizId) {
        this.primaryStage = primaryStage;
        this.quizId = quizId;
    }

    public void show() {
        primaryStage.setFullScreenExitHint(""); // Disable the hint
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC key for exiting full-screen

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        Label titleLabel = new Label("Create Question for Quiz ID: " + quizId);
        titleLabel.getStyleClass().add("title");

        Label questionLabel = new Label("Question Text:");
        TextField questionField = new TextField();
        questionField.setPromptText("Enter the question");
        questionField.getStyleClass().add("text-field");

        Label option1Label = new Label("Option 1:");
        TextField option1Field = new TextField();
        option1Field.setPromptText("Enter option 1");
        option1Field.getStyleClass().add("text-field");

        Label option2Label = new Label("Option 2:");
        TextField option2Field = new TextField();
        option2Field.setPromptText("Enter option 2");
        option2Field.getStyleClass().add("text-field");

        Label option3Label = new Label("Option 3:");
        TextField option3Field = new TextField();
        option3Field.setPromptText("Enter option 3");
        option3Field.getStyleClass().add("text-field");

        Label option4Label = new Label("Option 4:");
        TextField option4Field = new TextField();
        option4Field.setPromptText("Enter option 4");
        option4Field.getStyleClass().add("text-field");

        Label correctOptionLabel = new Label("Correct Option:");
        ComboBox<String> correctOptionBox = new ComboBox<>();
        correctOptionBox.getItems().addAll("Option 1", "Option 2", "Option 3", "Option 4");
        correctOptionBox.setPromptText("Select the correct option");

        Button createButton = new Button("Create Question");
        createButton.getStyleClass().add("button");
        createButton.setOnAction(e -> {
            String questionText = questionField.getText();
            String option1 = option1Field.getText();
            String option2 = option2Field.getText();
            String option3 = option3Field.getText();
            String option4 = option4Field.getText();
            String selectedOption = correctOptionBox.getValue();

            if (selectedOption == null) {
                showAlert("Please select the correct option!");
                return;
            }

            int correctOption = correctOptionBox.getItems().indexOf(selectedOption) + 1;

            Admin admin = new Admin();
            if (admin.createQuestion(quizId, questionText, option1, option2, option3, option4, correctOption)) {
                System.out.println("Question created successfully!");
                questionField.clear();
                option1Field.clear();
                option2Field.clear();
                option3Field.clear();
                option4Field.clear();
                correctOptionBox.getSelectionModel().clearSelection();
            } else {
                System.out.println("Question creation failed!");
            }
        });

        Button backButton = new Button("Back to Admin Dashboard");
        backButton.getStyleClass().add("button");
        backButton.setOnAction(e -> {
            AdminPage adminPage = new AdminPage(primaryStage);
            adminPage.show();
        });

        layout.getChildren().addAll(
            titleLabel, questionLabel, questionField, option1Label, option1Field,
            option2Label, option2Field, option3Label, option3Field, option4Label, option4Field,
            correctOptionLabel, correctOptionBox, createButton, backButton
        );

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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