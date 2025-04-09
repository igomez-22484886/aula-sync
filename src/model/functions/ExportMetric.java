package model.functions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.sql.*;
import static model.repository.SQLServerConnection.*;

public class ExportMetric {
    private static final String EXPORT_FOLDER = "exported-data" + File.separator;

    private static void createExportFolder() {
        File folder = new File(EXPORT_FOLDER);
        System.out.println("createExportFolder: Checking if export folder exists at " + folder.getAbsolutePath());

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("createExportFolder: Folder '" + EXPORT_FOLDER + "' created successfully.");
            } else {
                System.err.println("createExportFolder: Failed to create folder '" + EXPORT_FOLDER + "'.");
            }
        } else {
            System.out.println("createExportFolder: Folder '" + EXPORT_FOLDER + "' already exists.");
        }
    }

    public static void exportTableToCSV(String tableName) {
        createExportFolder();

        String query = "SELECT * FROM " + tableName;
        String fileName = EXPORT_FOLDER + "aula-sycn-data-" + tableName + ".csv";

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
                    csvWriter.append(rs.getString(i));
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            System.out.println("Table " + tableName + " exported successfully to " + fileName);

        } catch (SQLException | IOException e) {
            System.out.println("Error exporting CSV: " + e);
        }
    }
}
