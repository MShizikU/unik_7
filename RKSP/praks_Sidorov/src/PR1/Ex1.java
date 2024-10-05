package PR1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Ex1 {
    //Sidorov
    public static List<Integer> generateArray() {
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int randomNumber = random.nextInt();
            list.add(randomNumber);
        }
        return list;
    }

    //Sidorov
    public static int findMinNumber(List<Integer> list) throws InterruptedException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List is null");
        }
        int minNumber = list.get(0);
        for (int number : list) {
            Thread.sleep(1);
            if (number < minNumber) {
                minNumber = number;
            }
        }
        return minNumber;
    }

    //Sidorov
    public static int findMinNumberMultiThread(List<Integer> list) throws InterruptedException, ExecutionException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List is null");
        }
        int numberOfThreads = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Callable<Integer>> tasks = new ArrayList<>();
        int batchSize = list.size() / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int startIndex = i * batchSize;
            final int endIndex = (i == numberOfThreads - 1) ? list.size() : (i + 1) * batchSize;
            tasks.add(() -> findMinInRange(list.subList(startIndex, endIndex)));
        }

        List<Future<Integer>> futures = executorService.invokeAll(tasks);
        int min = Integer.MAX_VALUE;
        for (Future<Integer> future : futures) {
            int partialMin = future.get();
            Thread.sleep(1);
            if (partialMin < min) {
                min = partialMin;
            }
        }
        executorService.shutdown();
        return min;
    }

    //Sidorov
    private static int findMinInRange(List<Integer> sublist) throws InterruptedException {
        int min = Integer.MAX_VALUE;
        for (int number : sublist) {
            Thread.sleep(1);
            if (number < min) {
                min = number;
            }
        }
        return min;
    }

    //Sidorov
    public static int findMinNumberFork(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List is null");
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MinFinderTask task = new MinFinderTask(list, 0, list.size());
        return forkJoinPool.invoke(task);
    }

    //Sidorov
    static class MinFinderTask extends RecursiveTask<Integer> {
        private List<Integer> list;
        private int start;
        private int end;

        MinFinderTask(List<Integer> list, int start, int end) {
            this.list = list;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            if (end - start <= 1000) {
                try {
                    return findMinInRange(list.subList(start, end));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int middle = start + (end - start) / 2;
            MinFinderTask leftTask = new MinFinderTask(list, start, middle);
            MinFinderTask rightTask = new MinFinderTask(list, middle, end);
            leftTask.fork();
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Math.min(leftResult, rightResult);
        }
    }

    //Sidorov
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Integer> testList = generateArray();
        Runtime runtime = Runtime.getRuntime();

        // Sequential execution
        long startTime = System.nanoTime();
        int result = findMinNumber(testList);
        long endTime = System.nanoTime();
        long durationInMilliseconds = (endTime - startTime) / 1_000_000;
        System.out.println("Sequential function execution time: " + durationInMilliseconds + " ms. Result - " + result);
        System.out.println("Memory used: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");
        System.out.println("Active threads: " + Thread.activeCount());

        // Multi-threaded execution
        startTime = System.nanoTime();
        result = findMinNumberMultiThread(testList);
        endTime = System.nanoTime();
        durationInMilliseconds = (endTime - startTime) / 1_000_000;
        System.out.println("Multi-threaded function execution time: " + durationInMilliseconds + " ms. Result - " + result);
        System.out.println("Memory used: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");
        System.out.println("Active threads: " + Thread.activeCount());

        // Fork/Join execution
        startTime = System.nanoTime();
        result = findMinNumberFork(testList);
        endTime = System.nanoTime();
        durationInMilliseconds = (endTime - startTime) / 1_000_000;
        System.out.println("Fork function execution time: " + durationInMilliseconds + " ms. Result - " + result);
        System.out.println("Memory used: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");
        System.out.println("Active threads: " + Thread.activeCount());
    }
}
