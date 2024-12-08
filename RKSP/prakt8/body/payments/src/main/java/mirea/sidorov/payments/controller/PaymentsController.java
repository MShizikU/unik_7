package mirea.sidorov.payments.controller;

import mirea.sidorov.payments.model.Payment;
import mirea.sidorov.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        return ResponseEntity.ok(paymentService.updatePayment(id, paymentDetails));
    }
}


