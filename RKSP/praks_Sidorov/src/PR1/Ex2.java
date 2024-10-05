package PR1;

import java.util.Scanner;
import java.util.concurrent.*;

public class Ex2 {
    //Sidorov
    private static CompletableFuture<Integer> calculateSquare(int number) {
        return CompletableFuture.supplyAsync(() -> {
            int delayInSeconds = ThreadLocalRandom.current().nextInt(1, 6);
            try {
                Thread.sleep(delayInSeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return number * number;
        });
    }

    //Sidorov
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        while (true) {
            try {
                System.out.println("Enter a number (or 'e' to exit):");
                Scanner scanner = new Scanner(System.in);
                String userInput = scanner.nextLine();
                if ("e".equalsIgnoreCase(userInput)) {
                    break;
                }
                int number = Integer.parseInt(userInput);

                calculateSquare(number).thenAcceptAsync(result ->
                                System.out.println("Result: " + result), executorService)
                        .exceptionally(e -> {
                            System.err.println("Error executing the request: " + e.getMessage());
                            return null;
                        });

            } catch (NumberFormatException e) {
                System.err.println("Invalid number format. Please enter an integer.");
            }
        }

        executorService.shutdown();
    }
}
