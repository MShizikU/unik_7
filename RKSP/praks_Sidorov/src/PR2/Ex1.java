package PR2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
public class Ex1 {
    //Sidorov
    public static void main(String[] args) {
        String fileName = "prak2.txt";
        String[] lines = {
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                "Donec magna neque, bibendum ut maximus id, vehicula eu orci.",
                "Praesent interdum sem sed cursus venenatis."
        };
        writeFile(fileName, lines);
        readFile(fileName);
    }
    private static void writeFile(String fileName, String[] lines) {
        Path filePath = Paths.get(fileName);
        try {
            Files.write(filePath, List.of(lines));
            System.out.println("File was created: " + fileName);
        } catch (IOException e) {
            System.err.println("Write error: " + e.getMessage());
        }
    }
    private static void readFile(String fileName) {
        Path filePath = Paths.get(fileName);
        try {
            List<String> fileLines = Files.readAllLines(filePath);
            System.out.println("File: " + fileName + ":");
            for (String line : fileLines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Reading error: " + e.getMessage());
        }
    }
}
