package PR2;

import java.io.*;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Ex4 {
    private static Map<Path, List<String>> fileContentsMap = new HashMap<>();
    private static Map<Path, String> fileHashes = new HashMap<>();
    //Sidorov
    public static void main(String[] args) throws IOException, InterruptedException {
        Path directory = Paths.get("./prak_2_ex_4");
        WatchService watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        firstObserve(directory);
        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path filePath = (Path) event.context();
                    if (filePath.toString().endsWith("~")) continue;
                    System.out.println("New File: " + filePath);
                    fileContentsMap.put(filePath, readLinesFromFile(directory.resolve(filePath)));
                    calculateFileHash(directory.resolve(filePath));
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path filePath = (Path) event.context();
                    if (filePath.toString().endsWith("~")) continue;
                    System.out.println("Change File: " + filePath);
                    detectFileChanges(directory.resolve(filePath));
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    Path filePath = (Path) event.context();
                    if (filePath.toString().endsWith("~")) continue;
                    System.out.println("Delete File: " + filePath);
                    String hash = fileHashes.get(directory.resolve(filePath));
                    if (hash != null) {
                        System.out.println("Hash-summ: " + hash);
                    }
                }
            }
            key.reset();
        }
    }
    private static void firstObserve(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory))
        {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    fileContentsMap.put(filePath, readLinesFromFile(filePath));
                    calculateFileHash(filePath);
                }
            }
        }
    }
    //SSidorov
    private static void detectFileChanges(Path filePath) throws IOException {
        List<String> newFileContents = readLinesFromFile(filePath);
        List<String> oldFileContents = fileContentsMap.get(filePath);
        if (oldFileContents != null) {
            List<String> addedLines = newFileContents.stream()
                    .filter(line -> !oldFileContents.contains(line))
                    .toList();
            List<String> deletedLines = oldFileContents.stream()
                    .filter(line -> !newFileContents.contains(line))
                    .toList();
            if (!addedLines.isEmpty()) {
                System.out.println("Added lines in " + filePath + ":");
                addedLines.forEach(line -> System.out.println("+ " + line));
            }
            if (!deletedLines.isEmpty()) {
                System.out.println("Deleted lines in " + filePath + ":");
                deletedLines.forEach(line -> System.out.println("- " + line));
            }
        }
        calculateFileHash(filePath);
        fileContentsMap.put(filePath, newFileContents);
    }
    private static List<String> readLinesFromFile(Path filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    //Sidorov
    private static void calculateFileHash(Path filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (InputStream is = Files.newInputStream(filePath);
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                while (dis.read() != -1) ;
                String hash = bytesToHex(md.digest());
                fileHashes.put(filePath, hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}