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

    public boolean hasBeenAppliedTo(String jobID) {
        String sqlSelectJob = "SELECT * FROM jobs WHERE ID = " + jobID;
        String connectionUrl = "jdbc:mysql://localhost:3306/job_apply_bot?serverTimezone=UTC";

        try (Connection conn = DriverManager.getConnection(connectionUrl, "root", "thecatura");
             PreparedStatement ps = conn.prepareStatement(sqlSelectJob)
        ) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Job has been applied to: " + jobID);
                return true;
            } else {
                System.out.println("Job has not been applied to: " + jobID);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
