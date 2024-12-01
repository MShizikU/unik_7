package com.example.notificationservice.kafka;

import com.example.notificationservice.model.Payment;
import com.example.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "payments_topic", groupId = "notification_group")
    public void consume(Payment payment) {
        String message = "Payment received: " + payment.getId() + ", amount: " + payment.getAmount();
        notificationService.saveNotification(message);
    }
}

