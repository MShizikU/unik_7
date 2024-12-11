package mirea.sidorov.auth.controller;

import lombok.Data;
import mirea.sidorov.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login-success")
    public String loginSuccess(OAuth2AuthenticationToken authenticationToken) {
        OAuth2User user = authenticationToken.getPrincipal();
        return authService.generateToken(user);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract the token from the Authorization header
        String token = authorizationHeader.replace("Bearer ", "");

        // Validate the token
        return authService.validateToken(token)
                .map(validToken -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("username", validToken.getUsername());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() ->{
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "invalid token");
                    return ResponseEntity.status(401).body(response);
                });
    }
}
