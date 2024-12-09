package mirea.sidorov.auth.service;

import mirea.sidorov.auth.model.Token;
import mirea.sidorov.auth.model.User;
import mirea.sidorov.auth.repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    private byte[] jwtSecretBytes;
    private SecretKey secretKey;

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @PostConstruct
    public void init() throws DecoderException {
        jwtSecretBytes = Hex.decodeHex(jwtSecret.toCharArray());
        secretKey = new SecretKeySpec(jwtSecretBytes, "HmacSHA512");
    }

    public String createToken(User user) {
        String tokenId = UUID.randomUUID().toString();

        Token token = new Token();
        token.setToken(tokenId);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusSeconds(jwtExpirationInMs / 1000));
        tokenRepository.save(token);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setId(tokenId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public Optional<Token> validateToken(String tokenStr) {
        try {
            var claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(tokenStr)
                    .getBody();

            String tokenId = claims.getId();
            return tokenRepository.findByToken(tokenId)
                    .filter(token -> token.getExpiryDate().isAfter(LocalDateTime.now()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void revokeTokens(User user) {
        tokenRepository.deleteByUser(user);
    }
}