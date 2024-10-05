package PR2;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Ex3 {
    //Sidorov
    public static short calculateChecksum(String filePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath); FileChannel fileChannel = fileInputStream.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            short checksum = 0;
            while (fileChannel.read(buffer) != -1) {
                buffer.flip(); // Switch to read
                while (buffer.hasRemaining()) {
                    checksum ^= buffer.get(); // byte XOR
                }
                buffer.clear();
            }
            return checksum;
        }
    }
    public static void main(String[] args) {
        String filePath = "prak2.txt";
        try {
            short checksum = calculateChecksum(filePath);
            System.out.print("Summ: " + checksum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}