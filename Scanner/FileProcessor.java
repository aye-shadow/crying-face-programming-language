package Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileProcessor {
    public void processFiles() {
        Path inputFilesDir = Paths.get("InputFiles");

        try (Stream<Path> paths = Files.walk(inputFilesDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".😭"))
                    .forEach(this::processFile);
        } catch (IOException e) {
            System.err.println("Error walking directory: " + e.getMessage());
        }
    }

    private void processFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            System.out.println("Processing file: " + path);
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line here
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path);
            e.printStackTrace();
        }
    }
}