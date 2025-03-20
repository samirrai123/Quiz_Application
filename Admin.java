package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin extends User {

    public int createQuizAndGetId(String title, String category, int timeLimit) {
        String query = "INSERT INTO quizzes (title, category, time_limit) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, category);
            stmt.setInt(3, timeLimit);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Return the quiz ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if quiz creation fails
    }

    public boolean createQuestion(int quizId, String questionText, String option1, String option2, String option3, String option4, int correctOption) {
        String query = "INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, questionText);
            stmt.setString(3, option1);
            stmt.setString(4, option2);
            stmt.setString(5, option3);
            stmt.setString(6, option4);
            stmt.setInt(7, correctOption);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuiz(int quizId) {
        String query = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if a quiz was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void viewAnalytics() {
        String query = "SELECT * FROM results";
        try (Connection conn = DatabaseHandler.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Quiz ID: " + rs.getInt("quiz_id") + ", User ID: " + rs.getInt("user_id") + ", Score: " + rs.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}