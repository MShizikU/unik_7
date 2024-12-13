package mirea.sidorov.clients.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mirea.sidorov.clients.dto.ErrorResponse;
import mirea.sidorov.clients.feign.AuthServiceClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final AuthServiceClient authServiceClient;

    public AuthenticationFilter(ObjectMapper objectMapper, AuthServiceClient authServiceClient) {
        this.objectMapper = objectMapper;
        this.authServiceClient = authServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = attemptAuthentication(request);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    public Authentication attemptAuthentication(HttpServletRequest request) throws Exception {
        String token = extractToken(request);
        log.info("HEaders: {}", request.getHeaderNames());
        if (token != null) {
            try {
                String bearerToken = "Bearer " + token;
                ResponseEntity<String> sourceResponseEntity = authServiceClient.validateToken(bearerToken);
                log.info(sourceResponseEntity.getBody());

                if (sourceResponseEntity.getStatusCode() == HttpStatus.OK && sourceResponseEntity.getBody() != null) {
                    Authentication authentication = AuthentificationConverter.convert(sourceResponseEntity.getBody(), token);
                    return authentication;
                } else {
                    throw new Exception("User unauthorized 0 " + token);
                }
            } catch (Exception e) {
                throw new Exception("User unauthorized 1 " + token, e);
            }
        }
        throw new Exception("User unauthorized 2 " + token);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization или параметра запроса.
     *
     * @param request HTTP-запрос
     * @return Токен или null, если не найден
     */
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

    /**
     * Отправляет JSON-ответ с информацией об ошибке.
     *
     * @param response HTTP-ответ
     * @param status   HTTP-статус
     * @param message  Сообщение об ошибке
     * @throws IOException Если произошла ошибка при записи ответа
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(message, status.value(), System.currentTimeMillis());
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonResponse);
    }
}

