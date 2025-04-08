package model.functions;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.sql.*;
import static model.repository.SQLServerConnection.*;

public class ExportMetric {
    private static final String EXPORT_FOLDER = "engineering-project" + File.separator + "exported-data";

    private static void createExportFolder() {
        File folder = new File(EXPORT_FOLDER);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("Carpeta '" + EXPORT_FOLDER + "' creada exitosamente.");
            } else {
                System.err.println("Error al crear la carpeta '" + EXPORT_FOLDER + "'.");
            }
        }
    }

    public static void exportTableToCSV(String tableName) {
        createExportFolder();

        String query = "SELECT * FROM " + tableName;
        String fileName = "aula-sycn-data-" + tableName + ".csv";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter csvWriter = new FileWriter(fileName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Escribir encabezados
            for (int i = 1; i <= columnCount; i++) {
                csvWriter.append(metaData.getColumnName(i));
                if (i < columnCount) csvWriter.append(",");
            }
            csvWriter.append("\n");

            // Escribir filas
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    csvWriter.append(rs.getString(i));
                    if (i < columnCount) csvWriter.append(",");
                }
                csvWriter.append("\n");
            }

            System.out.println("Tabla " + tableName + " exportada exitosamente a " + fileName);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
