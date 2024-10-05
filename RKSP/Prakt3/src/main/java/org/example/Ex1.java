package org.example;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import java.util.Random;

//SSidorov
// Создаем класс для датчика температуры
class TemperatureSensor extends Observable<Integer> {
    private final PublishSubject<Integer> subject = PublishSubject.create();
    @Override
    protected void subscribeActual(Observer<? super Integer> observer)
    {
        subject.subscribe(observer); // Создаем подписку на события датчика температуры
    }
    public void start() {
        new Thread(() -> {
            while (true) {
                int temperature = new Random().nextInt(31 - 15) + 15; // Генерируем случайное значение температуры
                subject.onNext(temperature); // Отправляем значение температуры подписчикам
                try {
                    Thread.sleep(1000); // Пауза 1 секунда
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Запускаем поток для симуляции работы датчика
    }
}
//SSidorov Создаем класс для датчика CO2
class CO2Sensor extends Observable<Integer> {
    private final PublishSubject<Integer> subject = PublishSubject.create();
    @Override
    protected void subscribeActual(Observer<? super Integer> observer) {
        subject.subscribe(observer); // Создаем подписку на события датчика CO2
    }
    public void start() {
        new Thread(() -> {
            while (true) {
                int co2 = new Random().nextInt(101 - 30) + 30; // Генерируем случайное значение CO2
                subject.onNext(co2); // Отправляем значение CO2 подписчикам
                try {
                    Thread.sleep(1000); // Пауза 1 секунда
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Запускаем поток для симуляции работы датчика
    }
}
//SSidorov Создаем класс для сигнализации
class Alarm implements Observer<Integer> {
    private final int CO2_LIMIT = 70;
    private final int TEMP_LIMIT = 25;
    private int temperature = 0;
    private int co2 = 0;
    @Override
    public void onSubscribe(Disposable d) {
        System.out.println(d.hashCode() + " has subscribed");
    }
    @Override
    public void onNext(Integer value) {
        System.out.println("Next value from Observable= " + value);
        if (value <= 30){
            temperature = value;
        } else {
            co2 = value;
        }
        checkingSystem();
    }
    public void checkingSystem(){
        if (temperature >= TEMP_LIMIT && co2 >= CO2_LIMIT){
            System.out.println("ALARM!!! Temperature/CO2: " + temperature + "/"
                    + co2);
        } else if (temperature >= TEMP_LIMIT){
            System.out.println("Temperature warning: " + temperature);
        } else if (co2 >= CO2_LIMIT){
            System.out.println("CO2 warning: " + co2);
        }
    }
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
    @Override
    public void onComplete() {
        System.out.println("Completed");
    }
}
public class Ex1 {
    public static void main(String[] args) {
        TemperatureSensor temperatureSensor = new TemperatureSensor(); //Создаем датчик температуры
        CO2Sensor co2Sensor = new CO2Sensor(); // Создаем датчик CO2
        Alarm alarm = new Alarm(); // Создаем сигнализацию
        temperatureSensor.subscribe(alarm); // Подписываем сигнализацию на датчик температуры
        co2Sensor.subscribe(alarm); // Подписываем сигнализацию на датчик CO2
        temperatureSensor.start(); // Запускаем датчик температуры
        co2Sensor.start(); // Запускаем датчик CO2
        try {
            Thread.sleep(10000); // Запуск системы на 10 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}