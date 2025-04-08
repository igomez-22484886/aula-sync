package model.functions;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportMetric {
    // Función que guarda métricas en CSV dentro de la carpeta "metricas"
    public static void guardarMetricaCSV() {
        // Generar timestamp actual
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Definir la carpeta y el archivo
        File carpeta = new File("metricas");
        File archivo = new File(carpeta, "metrics.csv");

        // Crear la carpeta si no existe
        if (!carpeta.exists()) {
            boolean creada = carpeta.mkdirs();
            if (!creada) {
                System.out.println("Error: no se pudo crear la carpeta 'metricas'");
                return;
            }
        }

        // Escribir al archivo CSV
        try (FileWriter writer = new FileWriter(archivo, true)) {
            // Agregar encabezado si el archivo está vacío
            if (!archivo.exists() || archivo.length() == 0) {
                writer.write("timestamp,total_solicitudes,latencia_promedio_ms\n");
            }

            // Escribir la línea de métricas
            writer.write(String.format("%s,%d,%.2f\n", timestamp, 1, 100.0));
            System.out.println("Métrica guardada en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo:");
            e.printStackTrace();
        }
    }
}
