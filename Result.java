package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Result {
    private int result_id;
    private int user_id;
    private int quiz_id;
    private int score;
    private Date timestamp;

    public void saveResult(int user_id, int quiz_id, int score) {
        String query = "INSERT INTO results (user_id, quiz_id, score, timestamp) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            stmt.setInt(2, quiz_id);
            stmt.setInt(3, score);
            stmt.executeUpdate();
            System.out.println("Result saved successfully for user ID: " + user_id);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to save result for user ID: " + user_id);
        }
    }

    public void generateReport(int user_id) {
        String query = "SELECT quiz_id, score, timestamp FROM results WHERE user_id = ?";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                showAlert("No results found for this user.");
                return;
            }

            TableView<QuizResult> tableView = new TableView<>();
            ObservableList<QuizResult> data = FXCollections.observableArrayList();

            TableColumn<QuizResult, Integer> quizIdColumn = new TableColumn<>("Quiz ID");
            quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));

            TableColumn<QuizResult, Integer> scoreColumn = new TableColumn<>("Score");
            scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

            TableColumn<QuizResult, String> timestampColumn = new TableColumn<>("Timestamp");
            timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

            tableView.getColumns().addAll(quizIdColumn, scoreColumn, timestampColumn);

            while (rs.next()) {
                int quizId = rs.getInt("quiz_id");
                int score = rs.getInt("score");
                String timestamp = rs.getTimestamp("timestamp").toString();
                data.add(new QuizResult(quizId, score, timestamp));
            }

            tableView.setItems(data);

            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            Label titleLabel = new Label("Quiz Results for User ID: " + user_id);
            titleLabel.getStyleClass().add("title");

            Button closeButton = new Button("Close");
            closeButton.getStyleClass().add("button");
            closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());

            layout.getChildren().addAll(titleLabel, tableView, closeButton);

            Scene scene = new Scene(layout, 600, 400);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Quiz Results");
            stage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to generate report. Please try again.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class QuizResult {
        private int quizId;
        private int score;
        private String timestamp;

        public QuizResult(int quizId, int score, String timestamp) {
            this.quizId = quizId;
            this.score = score;
            this.timestamp = timestamp;
        }

        public int getQuizId() {
            return quizId;
        }

        public int getScore() {
            return score;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}