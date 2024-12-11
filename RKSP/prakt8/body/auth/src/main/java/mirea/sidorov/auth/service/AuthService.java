package mirea.sidorov.auth.service;

import mirea.sidorov.auth.model.AuthToken;
import mirea.sidorov.auth.repository.AuthTokenRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthTokenRepository repository;

    public AuthService(AuthTokenRepository repository) {
        this.repository = repository;
    }

    public String generateToken(OAuth2User user) {
        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken();
        authToken.setUsername(user.getAttribute("login"));
        authToken.setToken(token);
        repository.save(authToken);
        return token;
    }

    public Optional<AuthToken> validateToken(String token) {
        return repository.findByToken(token);
    }
}
