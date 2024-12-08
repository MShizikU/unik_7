package mirea.sidorov.payments.feign;

import mirea.sidorov.payments.dto.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clients")
public interface ClientServiceFeignClient {
    @GetMapping("/clients/{id}")
    ResponseEntity<Client> getClientById(@PathVariable("id") Long id);
}
