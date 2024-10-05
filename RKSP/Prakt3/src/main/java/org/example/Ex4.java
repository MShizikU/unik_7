package org.example;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.atomic.AtomicInteger;
//SSidorov Класс File представляет файлы с типом и размером
class File {
    private final String fileType;
    private final int fileSize;
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
//SSidorov Генератор файлов
class FileGenerator {
    private static final AtomicInteger counter = new AtomicInteger(0);
    // Генерирует файлы асинхронно с задержкой
    public Observable<File> generateFile() {
        return Observable
                .fromCallable(() -> {
                    try {
                        String[] fileTypes = {"XML", "JSON", "XLS"};
                        String fileType = fileTypes[(int) (Math.random() * 3)];
                        int fileSize = (int) (Math.random() * 91) + 10;
                        Thread.sleep((long) (Math.random() * 901) + 100); // Имитация генерации файла
                        return new File(fileType, fileSize);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .repeat() // Повторяем бесконечно
                .subscribeOn(Schedulers.io()) // Выполняется в фоновом потоке
                .observeOn(Schedulers.io()); // Результаты наблюдаются в фоновом потоке
    }
}
//SSidorov Очередь файлов
class FileQueue {
    private final int capacity;
    private final Observable<File> fileObservable;
    // Создает очередь с заданной вместимостью и подключается к генератору файлов
    public FileQueue(int capacity) {
        this.capacity = capacity;
        this.fileObservable = new FileGenerator().generateFile()
                .replay(capacity) // Буферизирует источник файлов с ограниченной емкостью
                .autoConnect(); // Подключается автоматически к буферизированному источнику
    }
    // Получает наблюдаемый поток файлов
    public Observable<File> getFileObservable() {
        return fileObservable;
    }
}
//SSidorov Обработчик файлов
class FileProcessor {
    private final String supportedFileType;
    // Создает обработчик файлов для определенного типа файлов
    public FileProcessor(String supportedFileType) {
        this.supportedFileType = supportedFileType;
    }
    // Обрабатывает файлы асинхронно с задержкой
    public Completable processFiles(Observable<File> fileObservable) {
        return fileObservable
                .filter(file -> file.getFileType().equals(supportedFileType)) // Фильтрует файлы по типу
                .flatMapCompletable(file -> {
                    long processingTime = file.getFileSize() * 7; // Вычисляет время обработки
                    return Completable
                            .fromAction(() -> {
                                Thread.sleep(processingTime); // Имитация обработки файла
                                System.out.println("Processed " +
                                        supportedFileType + " file with size " + file.getFileSize());
                            })
                            .subscribeOn(Schedulers.io()) // Выполняется в фоновом потоке
                            .observeOn(Schedulers.io()); // Результаты наблюдаются в фоновом потоке
                })
                .onErrorComplete(); // Игнорирует ошибки и завершает успешно
    }
}
//SSidorov Основной класс системы обработки файлов
class FileProcessingSystem {
    public static void main(String[] args) {
        int queueCapacity = 5;
        FileQueue fileQueue = new FileQueue(queueCapacity);
        String[] supportedFileTypes = {"XML", "JSON", "XLS"};
        for (String fileType : supportedFileTypes) {
            new FileProcessor(fileType)
                    .processFiles(fileQueue.getFileObservable())
                    .subscribe(
                            () -> {}, // Обработка успешного завершения
                            throwable -> System.err.println("Error processing file: " + throwable)
                    );
        }
        // Даем системе время для работы (можно изменить)
        try {
            Thread.sleep(10000); // Пусть система работает 10 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
