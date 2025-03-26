package model.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerManager {

    public static void startDockerContainer() {
        System.out.println("DockerManager: Starting Docker container...");

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker-compose", "-f", "docker/docker-compose.yml", "up", "-d");

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("DockerManager: " + line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("DockerManager: Container started successfully.");
            } else {
                System.err.println("DockerManager: Failed to start container.");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("DockerManager: Error - " + e.getMessage());
        }
    }
}
