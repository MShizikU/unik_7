package mirea.sidorov.auth.controller;

import lombok.Data;
import mirea.sidorov.auth.model.Token;
import mirea.sidorov.auth.model.User;
import mirea.sidorov.auth.service.TokenService;
import mirea.sidorov.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String jwt = tokenService.createToken(user);
        return ResponseEntity.ok(new TokenResponse(jwt));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        Optional<Token> tokenOpt = tokenService.validateToken(token);
        if (tokenOpt.isPresent()) {
            Token validToken = tokenOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("username", validToken.getUser().getUsername());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

    @Data
    static class RegisterRequest {
        private String username;
        private String password;
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    static class TokenResponse {
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }
    }
}
