package PR1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//Sidorov
public class MultithreadedFileProcessing {
    public static void main(String[] args) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(5);
        Thread generatorThread = new Thread(new FileGenerator(queue));
        Thread jsonProcessorThread = new Thread(new FileProcessor(queue, "JSON"));
        Thread xmlProcessorThread = new Thread(new FileProcessor(queue, "XML"));
        Thread xlsProcessorThread = new Thread(new FileProcessor(queue, "XLS"));
        generatorThread.start();
        jsonProcessorThread.start();
        xmlProcessorThread.start();
        xlsProcessorThread.start();
    }
}
