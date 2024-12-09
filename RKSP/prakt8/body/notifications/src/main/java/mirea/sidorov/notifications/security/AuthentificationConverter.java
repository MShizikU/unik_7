package mirea.sidorov.notifications.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public class AuthentificationConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Authentication convert(String responseBody, String token) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String username = jsonNode.get("username").asText();
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();

            return new UsernamePasswordAuthenticationToken(username, token, authorities);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert authentication response", e);
        }
    }
}
