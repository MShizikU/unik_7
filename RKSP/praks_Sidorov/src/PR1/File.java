package PR1;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
//Sidorov
public class File {
    private String fileType;
    private int fileSize;
    public File(String fileType, int fileSize) {
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
    public String getFileType() {
        return fileType;
    }
    public int getFileSize() {
        return fileSize;
    }
}
//Sidorov
class FileGenerator implements Runnable {
    private BlockingQueue<File> queue;
    public FileGenerator(BlockingQueue<File> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        Random random = new Random();
        String[] fileTypes = {"XML", "JSON", "XLS"};
        while (true) {
            try {
                Thread.sleep(random.nextInt(901) + 100);
                String randomFileType = fileTypes[random.nextInt(fileTypes.length)];
                int randomFileSize = random.nextInt(91) + 10;
                File file = new File(randomFileType, randomFileSize);
                queue.put(file); // ��������� ���� � �������
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
//Sidorov
class FileProcessor implements Runnable {
    private BlockingQueue<File> queue;
    private String allowedFileType;
    public FileProcessor(BlockingQueue<File> queue, String allowedFileType) {
        this.queue = queue;
        this.allowedFileType = allowedFileType;
    }
    @Override
    public void run() {
        while (true) {
            try {
                File file = queue.take();
                if (file.getFileType().equals(allowedFileType)) {
                    long processingTime = file.getFileSize() * 7;
                    Thread.sleep(processingTime);
                    System.out.println(file.getFileType() +
                            " type file processed with size " +
                            file.getFileSize() +
                            ". Processing time: " + processingTime + " ms.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
