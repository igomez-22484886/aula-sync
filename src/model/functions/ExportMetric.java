package model.functions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import static model.repository.SQLServerConnection.*;

public class ExportMetric {
    private static final String EXPORT_FOLDER = "exported-data" + File.separator;

    private static void createExportFolder() {
        File folder = new File(EXPORT_FOLDER);
        // System.out.println("createExportFolder: Checking if export folder exists at " + folder.getAbsolutePath());

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                // System.out.println("createExportFolder: Folder '" + EXPORT_FOLDER + "' created successfully.");
            } else {
                // 1System.err.println("createExportFolder: Failed to create folder '" + EXPORT_FOLDER + "'.");
            }
        } else {
            // System.out.println("createExportFolder: Folder '" + EXPORT_FOLDER + "' already exists.");
        }
    }

    public static void exportTableToCSV(String tableName) {
        createExportFolder();

        String query = "SELECT * FROM " + tableName;
        String fileName = EXPORT_FOLDER + "aula-sync-data-" + tableName + ".csv";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(fileName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write headers
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Write rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    csvWriter.append(value != null ? value : "");
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            // System.out.println("Table " + tableName + " exported successfully to " + fileName);

        } catch (SQLException | IOException e) {
            // System.out.println("Error exporting CSV: " + e);
        }
    }

    public static void exportMostDemandedClassrooms() {
        createExportFolder();
        LocalDate today = LocalDate.now();
        String datePrefix = today.toString();

        // Daily demand
        String dailyQuery = "SELECT c.ClassroomId, COUNT(r.ReservationId) as reservation_count " +
                "FROM ClassroomTable c " +
                "JOIN ReservationTable r ON c.ClassroomId = r.ClassroomId " +
                "WHERE r.ReservationDate = ? " +
                "GROUP BY c.ClassroomId " +
                "ORDER BY reservation_count DESC";
        exportQueryToCSV(dailyQuery, EXPORT_FOLDER + "most-demanded-classrooms-daily-" + datePrefix + ".csv",
                "Daily Most Demanded Classrooms", new Object[]{Date.valueOf(today)});

        // Weekly demand (Monday to Friday)
        String weeklyQuery = "SELECT c.ClassroomId, COUNT(r.ReservationId) as reservation_count " +
                "FROM ClassroomTable c " +
                "JOIN ReservationTable r ON c.ClassroomId = r.ClassroomId " +
                "WHERE r.ReservationDate BETWEEN ? AND ? " +
                "AND DATEPART(dw, r.ReservationDate) BETWEEN 2 AND 6 " +
                "GROUP BY c.ClassroomId " +
                "ORDER BY reservation_count DESC";
        LocalDate monday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate friday = monday.plusDays(4);
        exportQueryToCSV(weeklyQuery, EXPORT_FOLDER + "most-demanded-classrooms-weekly-" + datePrefix + ".csv",
                "Weekly Most Demanded Classrooms (Monday to Friday)",
                new Object[]{Date.valueOf(monday), Date.valueOf(friday)});

        // Monthly demand
        String monthlyQuery = "SELECT c.ClassroomId, COUNT(r.ReservationId) as reservation_count " +
                "FROM ClassroomTable c " +
                "JOIN ReservationTable r ON c.ClassroomId = r.ClassroomId " +
                "WHERE YEAR(r.ReservationDate) = ? AND MONTH(r.ReservationDate) = ? " +
                "GROUP BY c.ClassroomId " +
                "ORDER BY reservation_count DESC";
        exportQueryToCSV(monthlyQuery, EXPORT_FOLDER + "most-demanded-classrooms-monthly-" + datePrefix + ".csv",
                "Monthly Most Demanded Classrooms",
                new Object[]{today.getYear(), today.getMonthValue()});
    }

    public static void exportPeakHours() {
        createExportFolder();
        String datePrefix = LocalDate.now().toString();

        String query = "SELECT DATEPART(HOUR, CAST(r.StartTime AS DATETIME)) as hour, COUNT(r.ReservationId) as reservation_count " +
                "FROM ReservationTable r " +
                "WHERE r.ReservationDate = ? " +
                "GROUP BY DATEPART(HOUR, CAST(r.StartTime AS DATETIME)) " +
                "ORDER BY reservation_count DESC";
        exportQueryToCSV(query, EXPORT_FOLDER + "peak-hours-" + datePrefix + ".csv",
                "Peak Hours", new Object[]{Date.valueOf(LocalDate.now())});
    }

    public static void exportMostActiveUsers() {
        createExportFolder();
        String datePrefix = LocalDate.now().toString();

        String query = "SELECT u.UserName, COUNT(r.ReservationId) as reservation_count " +
                "FROM UserTable u " +
                "JOIN ReservationTable r ON u.UserId = r.UserId " +
                "WHERE YEAR(r.ReservationDate) = ? AND MONTH(r.ReservationDate) = 0 AND MONTH(r.ReservationDate) = ? " +
                "GROUP BY u.UserName " +
                "ORDER BY reservation_count DESC";
        LocalDate today = LocalDate.now();
        exportQueryToCSV(query, EXPORT_FOLDER + "most-active-users-" + datePrefix + ".csv",
                "Most Active Users", new Object[]{today.getYear(), today.getMonthValue()});
    }

    public static void exportAverageOccupancyTime() {
        createExportFolder();
        String datePrefix = LocalDate.now().toString();

        String query = "SELECT c.ClassroomId, AVG(DATEDIFF(MINUTE, " +
                "CAST(r.ReservationDate AS DATETIME) + CAST(r.StartTime AS DATETIME), " +
                "CAST(r.ReservationDate AS DATETIME) + CAST(r.EndTime AS DATETIME))) as avg_occupancy_time " +
                "FROM ClassroomTable c " +
                "JOIN ReservationTable r ON c.ClassroomId = r.ClassroomId " +
                "WHERE YEAR(r.ReservationDate) = ? AND MONTH(r.ReservationDate) = ? " +
                "GROUP BY c.ClassroomId " +
                "ORDER BY avg_occupancy_time DESC";

        LocalDate today = LocalDate.now();
        exportQueryToCSV(query, EXPORT_FOLDER + "average-occupancy-time-" + datePrefix + ".csv",
                "Average Occupancy Time (Minutes)",
                new Object[]{today.getYear(), today.getMonthValue()});
    }

    public static void exportOccupancyPercentage() {
        createExportFolder();
        String datePrefix = LocalDate.now().toString();

        String query = "SELECT c.ClassroomId, " +
                "(SUM(DATEDIFF(MINUTE, " +
                "CAST(r.ReservationDate AS DATETIME) + CAST(r.StartTime AS DATETIME), " +
                "CAST(r.ReservationDate AS DATETIME) + CAST(r.EndTime AS DATETIME))) * 100.0) / " +
                "(COUNT(DISTINCT r.ReservationDate) * 12 * 60) as occupancy_percentage " +
                "FROM ClassroomTable c " +
                "JOIN ReservationTable r ON c.ClassroomId = r.ClassroomId " +
                "WHERE YEAR(r.ReservationDate) = ? AND MONTH(r.ReservationDate) = ? " +
                "GROUP BY c.ClassroomId " +
                "ORDER BY occupancy_percentage DESC";

        LocalDate today = LocalDate.now();
        exportQueryToCSV(query, EXPORT_FOLDER + "occupancy-percentage-" + datePrefix + ".csv",
                "Occupancy Percentage", new Object[]{today.getYear(), today.getMonthValue()});
    }

    private static void exportQueryToCSV(String query, String fileName, String metricName, Object[] params) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set query parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery();
                 FileWriter csvWriter = new FileWriter(fileName)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Write headers
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(metaData.getColumnName(i));
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");

                // Write rows
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        String value = rs.getString(i);
                        csvWriter.append(value != null ? value : "");
                        if (i < columnCount) csvWriter.append(",");
                    }
                    csvWriter.append("\n");
                }

                // System.out.println(metricName + " exported successfully to " + fileName);

            } catch (IOException e) {
                // System.out.println("Error writing CSV for " + metricName + ": " + e);
            }

        } catch (SQLException e) {
            // System.out.println("Error executing query for " + metricName + ": " + e);
        }
    }

}