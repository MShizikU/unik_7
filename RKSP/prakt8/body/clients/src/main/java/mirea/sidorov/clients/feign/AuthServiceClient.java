package mirea.sidorov.clients.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth")
public interface AuthServiceClient {

    @GetMapping("/auth/validate")
    ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorizationHeader);
}
