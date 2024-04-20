package org.example;


import java.sql.*;

public class JobsContext {
    public void increaseCounter(String jobID) {
        String sqlInsertJob = "INSERT INTO jobs VALUES ( " + jobID + " )";
        String connectionUrl = "jdbc:mysql://localhost:3306/job_apply_bot?serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "thecatura");
             PreparedStatement ps = conn.prepareStatement(sqlInsertJob)
             ) {
            ps.executeUpdate();
            System.out.println("Job inserted: " + jobID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
